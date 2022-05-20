package com.example.ecom.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    MainController underTest;

    @Test
    void indexOkTest() throws Exception {
        String ip = UUID.randomUUID().toString();
        mvc.perform(get("/")
                .header("X-REAL-IP", ip)
            )
            .andExpect(status().isOk());
    }

    @Test
    void indexNotOkTest() throws Exception {
        String ip = UUID.randomUUID().toString();
        for (int i = 0; i < 10; ++i) {
            mvc.perform(get("/")
                    .header("X-REAL-IP", ip))
                .andExpect(status().isOk())
                .andDo(print());
        }
        mvc.perform(get("/")
                    .header("X-REAL-IP", ip))
                .andExpect(status().is(502))
                .andDo(print());
    }
}