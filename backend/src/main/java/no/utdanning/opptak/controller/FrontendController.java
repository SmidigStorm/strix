package no.utdanning.opptak.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

  @GetMapping(value = {"/"})
  public String index() {
    return "forward:/index.html";
  }

  @GetMapping(value = {"/opptak/**", "/organisasjon/**", "/utdanning/**"})
  public String spa() {
    return "forward:/index.html";
  }
}
