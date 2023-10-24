package com.hasil.lppaik.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/hallo")
public class HelloWorld {

  @GetMapping
  public String hallo(){
    return "Hallo guys";
  }
}
