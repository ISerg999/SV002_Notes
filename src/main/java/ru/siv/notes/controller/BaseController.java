package ru.siv.notes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import ru.siv.notes.service.UserService;

/**
 * Базовый класс для контроллеров, содержащий общедоступные ресурсы.
 */
public abstract class BaseController {

  protected String INFO_USER = "infoUser";
  protected String REDIRECT_TO_INDEX = "redirect:/";

  @Autowired
  protected UserService userService;

  public BaseController() {}
}
