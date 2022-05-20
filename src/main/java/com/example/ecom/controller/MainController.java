package com.example.ecom.controller;

import com.example.ecom.aspects.annotations.ThrottleByIp;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@ThrottleByIp
@RequestMapping(value = "/", produces = "application/json")
public class MainController {

    @GetMapping
    @ThrottleByIp
    @ResponseStatus(HttpStatus.OK)
    public String index(HttpServletRequest request) {
        return "";
    }

}
