package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    @Operation(summary = "Boot check endpoint")
    public String hello() {
        return "Hello world!";
    }
}
