package com.sagax.semaphore;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.transaction.Transactional;
import jakarta.persistence.*;

@Service
public class Semaphore
{

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    public void setcount(
        String name, 
        Integer count
    ){
        Object res = entityManager.createNativeQuery("select semaphore.setcount(:name, :count)")
            .setParameter("name", name)
            .setParameter("count", count)
            .getSingleResult();
    }
    
    public void flush(
        String name, 
        Boolean force
    ){
        Object res = entityManager.createNativeQuery("select semaphore.flush(:name, :force)")
            .setParameter("name", name)
            .setParameter("force", force)
            .getSingleResult();
    }

    public void release(
        UUID id 
    ){
        Object res = entityManager.createNativeQuery("select semaphore.release(:iid)")
            .setParameter("iid", id)
            .getSingleResult();
    }
    
    public UUID acquire(
        String  name, 
        String  interval,
        String  owner,
        Integer polls,
        String  pollinterval
    ){
        return (UUID)(entityManager.createNativeQuery("select semaphore.acquire(:name, (:interval)\\:\\:interval, :owner, :polls, (:pollinterval)\\:\\:interval)")
            .setParameter("name",        name        )
            .setParameter("interval",    interval    )
            .setParameter("owner",       owner       )
            .setParameter("polls",       polls       )
            .setParameter("pollinterval",pollinterval)
            .getSingleResult());
    }

}
