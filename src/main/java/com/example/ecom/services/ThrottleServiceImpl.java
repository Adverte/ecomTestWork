package com.example.ecom.services;

import com.example.ecom.exception.ThrottleException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.*;

@Service
@Getter
public class ThrottleServiceImpl implements ThrottleService {

    @Value("${request.limittime}")
    private long limitTime;
    @Value("${request.limitquantity}")
    private int limitQuantity;
    private Map<String, ConcurrentLinkedDeque<RequestsTime>> ipTimeMap = new ConcurrentHashMap<>();

    @Override
    public void isAcceptable(String ip) {
        ConcurrentLinkedDeque<RequestsTime> queue = ipTimeMap.get(ip);
        if (queue == null) {
            queue = addIp(ip);
        }
        synchronized (this) {
            try {
                while (System.currentTimeMillis() - queue.getFirst().getDateTime() > (limitTime * 1000)) {
                    queue.pollFirst();
                }
            } catch (NoSuchElementException e) {
                //noop
            }

            if (queue.size() >= limitQuantity) {
                throw new ThrottleException();
            }
            queue.addLast(RequestsTime.builder().dateTime(System.currentTimeMillis()).build());
        }
    }

    private synchronized ConcurrentLinkedDeque<RequestsTime> addIp(String ip) {
        return ipTimeMap.computeIfAbsent(ip, i -> new ConcurrentLinkedDeque<>());
    }

}
