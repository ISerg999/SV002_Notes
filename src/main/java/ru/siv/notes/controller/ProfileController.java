package ru.siv.notes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.siv.notes.config.SharedResources;
import ru.siv.notes.service.UserService;

/**
 * Позволяет пользователю редактировать свое полное имя.
 */
@Controller
public class ProfileController {

  @Autowired
  private SharedResources res;
  @Autowired
  private UserService userService;

  // TODO: Возможность для любого пользователя изменить свое полное имя.
}
