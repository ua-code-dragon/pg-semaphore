package com.sagax.semaphore;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertNotNull;


import com.sagax.semaphore.Semaphore;

@SpringBootTest
class SemaphoreApplicationTests {

    @Autowired
    private Semaphore semaphore;        

	@Test
	void semaphoreTest1() {
        
        semaphore.setcount("test1", 2);
        UUID s = semaphore.acquire("test1", "1m", "tester", 2, "1s");
        assertNotNull(s);
        semaphore.release(s);

	}
    



}
