package ru.siv.notes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.siv.notes.config.SharedResources;
import ru.siv.notes.service.InfoUser;
import ru.siv.notes.service.UserService;

@Controller
public class MainController {

  @Autowired
  private SharedResources res;
  @Autowired
  private UserService userService;

  @GetMapping("/")
  public String index(Model model) {
    InfoUser infoUser = userService.getCurrentUser();
    model.addAttribute(res.getUrlInfoUser(), infoUser);
    return "index";
  }
}
