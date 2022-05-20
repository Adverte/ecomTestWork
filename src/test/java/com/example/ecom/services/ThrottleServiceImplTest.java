package com.example.ecom.services;

import com.example.ecom.exception.ThrottleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ThrottleServiceImplTest {
    private String ip;
    private static final int LIMIT_QUANT = 7;
    private static final int LIMIT_TIME = 1;
    private ThrottleService underTest;

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
    void isAcceptableOneIpTest() {
        ip = UUID.randomUUID().toString();
        underTest.isAcceptable(ip);
        assertDoesNotThrow(() -> ThrottleException.class);
    }

    @Test
    void isAcceptableOneIpExceptionTest() {
        ip = UUID.randomUUID().toString();
        assertThrows(ThrottleException.class, () -> {
            for (int i = 0; i < LIMIT_QUANT; ++i) {
                underTest.isAcceptable(ip);
            }
            underTest.isAcceptable(ip);
            underTest.isAcceptable(ip);
        });
    }

    @Test
    void isAcceptableDifferentIpTest() {
        for (int i = 0; i < 2 * LIMIT_QUANT; ++i) {
            ip = UUID.randomUUID().toString();
            underTest.isAcceptable(ip);
        }
        assertDoesNotThrow(() -> ThrottleException.class);
    }


    @Test
    void isAcceptableIpAfterTimeoutTest() throws InterruptedException {
        ip = UUID.randomUUID().toString();
        for (int i = 0; i < LIMIT_QUANT; ++i) {
            underTest.isAcceptable(ip);
        }
        TimeUnit.SECONDS.sleep(LIMIT_TIME+1);
        underTest.isAcceptable(ip);
        assertDoesNotThrow(() -> ThrottleException.class);
    }
}
