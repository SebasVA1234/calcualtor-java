package com.ecualand.calculator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  // Este endpoint está protegido por tu SecurityConfig ("/api/**" requiere auth)
  @GetMapping("/api/ping")
  public String ping() {
    return "ok";
  }
}
