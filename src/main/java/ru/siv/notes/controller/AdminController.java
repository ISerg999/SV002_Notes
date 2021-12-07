package ru.siv.notes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.siv.notes.config.SharedResources;
import ru.siv.notes.service.UserService;

/**
 * Контроллер администрирования данных.
 */
@Controller
public class AdminController {

  @Autowired
  private SharedResources res;
  @Autowired
  private UserService userService;

  @GetMapping("/admin/user/list")
  public String userList(Model model) {
    UserService.InfoUser infoUser = userService.getCurrentUser();
    if (0 != infoUser.getTypeRoleUser()) return res.getUrlRedirectToMain();
    model.addAttribute(res.getUrlInfoUser(), infoUser);
    model.addAttribute(res.getUrlAllUser(), userService.getAllUser(false));
    return "admins/user-list";
  }

  @GetMapping("/admin/user/{uId}/remove")
  public String  deleteUser(@PathVariable(value = "uId") long uId, Model model) {
    UserService.InfoUser infoUser = userService.getCurrentUser();
    if (0 != infoUser.getTypeRoleUser()) return res.getUrlRedirectToMain();
    if (uId > 0) userService.deleteUser(uId);
    return res.getUrlRedirectToListUser();
  }

  // TODO: Список удалённых пользователей.
  // TODO: Восстановление удалённых пользователей.
  // TODO: Список тем.
  // TODO: Добавить тему.
  // TODO: Редактировать тему.
  // TODO: Удалить тему.
  // TODO: Список удалённых статей (если не удалён автор).
  // TODO: Просмотр удалённой статьи (если не удалён автор).
  // TODO: Восстановление удалённой статьи (если не удалён автор).

}
