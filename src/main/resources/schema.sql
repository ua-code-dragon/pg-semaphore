create schema if not exists semaphore;

create table if not exists semaphore.semaphore (
    id uuid not null default gen_random_uuid() primary key,         -- semaphore id for release
    name varchar(255) not null,                                     -- semaphore name
    acquired boolean not null default false,                        -- is acquired
    acquired_at timestamptz,                                        -- when was acquired
    acquired_for interval,                                          -- for which time interval
    acquired_by varchar(255)                                        -- who acquired
);

-- flush semaphores
-- flushes all acquired but expired ( acquired_at + acquired_for < current_timestamp )
-- iname = semaphore.name or __all__ for all semaphores
-- iforce means flush all including acquired
--
create or replace function semaphore.flush( iname varchar, iforce boolean default false ) returns void as ' 
    update semaphore.semaphore 
    set acquired = false, acquired_at = null, acquired_for = null, acquired_by = null
    where 
        ( name = iname or iname = ''__all__'' )
        and acquired
        and ((acquired_at + acquired_for < current_timestamp) or iforce);
 ' language sql;    


-- release semaphore
-- by specific id
--
create or replace function semaphore.release ( iid uuid ) returns void as ' 
    update semaphore.semaphore
    set acquired = false, acquired_at = null, acquired_for = null, acquired_by = null
    where id = iid;
 ' language sql;


-- set semaphore count
-- by name
-- my shrink or extend current semaphore depth
--
create or replace function semaphore.setcount( iname varchar, icount int default 1 ) returns void as ' 
declare
    cnt int;
begin
    select into cnt count(*) from semaphore.semaphore where name = iname;
    if cnt > icount then
        perform semaphore.flush( iname, true );
        delete from semaphore.semaphore 
        where id in ( 
            select id from semaphore.semaphore where name = iname limit cnt - icount
        );
    else
        insert into semaphore.semaphore (name) select iname from generate_series(1,icount-cnt);    
    end if;
end; 
 ' language plpgsql;


-- acquire semaphore
-- by name
-- itimeout goes to semaphore.acquired_for
-- iowner goes to semaphore.acquired_by
-- ipolls means number of loops to wait for semaphore release
-- ipollinterval means delay between waiting loops
-- 
create or replace function semaphore.acquire ( iname varchar, itimeout interval, iowner varchar default null, ipolls int default 1, ipollinterval interval default '1ms' ) returns uuid as ' 
declare 
    sid uuid;
    i int;
begin    
    perform semaphore.flush(iname);
    i := 0;
    while i < ipolls loop
        update semaphore.semaphore
        set acquired = true, acquired_at = current_timestamp, acquired_for = itimeout, acquired_by = iowner
        where id in (
            select id
            from semaphore.semaphore
            where 
                name = iname
                and (not acquired or (acquired_at is not null and acquired_for is not null and acquired_at < current_timestamp - acquired_for))
                for update skip locked
                limit 1
        )
        returning id into sid;
        if found then
            return sid;
        end if;
        perform pg_sleep(extract (seconds from ipollinterval));
        i := i + 1;
    end loop;
    return sid;
end;    
 ' language plpgsql;





