package com.example.ecom.services;

import com.example.ecom.exception.ThrottleException;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ThrottleServiceMultithreadTest {

    private static final int THREAD_QUANT = 10;
    private static final int LIMIT_QUANT = 7;
    private static final int LIMIT_TIME = 100;
    final CyclicBarrier startingBarrier = new CyclicBarrier(THREAD_QUANT);
    final CyclicBarrier endingBarrier = new CyclicBarrier(THREAD_QUANT);
    private Object underTest;

    @BeforeEach
    public void init() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        underTest = ThrottleServiceImpl.class.getDeclaredConstructor().newInstance();
        Field time = ThrottleServiceImpl.class.getDeclaredField("limitTime");
        time.setAccessible(true);
        time.set(underTest, LIMIT_TIME);
        Field quant = ThrottleServiceImpl.class.getDeclaredField("limitQuantity");
        quant.setAccessible(true);
        quant.set(underTest, LIMIT_QUANT);
    }

    @Test
    void isAcceptableOneIpTest() throws BrokenBarrierException, InterruptedException {
        String ip = UUID.randomUUID().toString();
        final AtomicInteger exceptionCounter = new AtomicInteger();
        Runnable task = getTask(ip, exceptionCounter);
        for (int i = 0; i < THREAD_QUANT; ++i) {
            Thread worker = new Thread(task);
            worker.setName("Thread " + i);
            worker.start();
        }

        endingBarrier.await();
        assertEquals(THREAD_QUANT - LIMIT_QUANT, exceptionCounter.get());
        System.out.println("size:" + ((ThrottleServiceImpl) underTest).getIpTimeMap().get(ip).size());
    }

    private Runnable getTask(String ip, AtomicInteger exceptionCounter) {
        Runnable task = () -> {
            try {
                startingBarrier.await();
                try {
                    ((ThrottleService) underTest).isAcceptable(ip);
                } catch (ThrottleException e) {
                    exceptionCounter.getAndIncrement();
                }
                endingBarrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                ex.printStackTrace();
            }
        };
        return task;
    }

}
