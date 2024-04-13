package com.sagax.semaphore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SpringBootApplication
public class SemaphoreApplication 
implements CommandLineRunner
{

    private static Logger LOG = LoggerFactory
        .getLogger(SemaphoreApplication.class);

	public static void main(String[] args) {
		LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(SemaphoreApplication.class, args);
        LOG.info("APPLICATION FINISHED");
	}

    @Override
    public void run(String... args) {
        LOG.info("EXECUTING : command line runner");
        for (int i = 0; i < args.length; ++i) {
            LOG.info("args[{}]: {}", i, args[i]);
        }            
    
    }

}
