package no.utdanning.opptak.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GraphiQLController {

  @GetMapping("/graphiql")
  public String graphiql() {
    return "forward:/graphiql.html";
  }
}
