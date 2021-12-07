package ru.siv.notes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.siv.notes.config.SharedResources;
import ru.siv.notes.model.Users;
import ru.siv.notes.service.UserService;

import javax.validation.Valid;

@Controller
public class RegistrationAndLoginController {

  @Autowired
  private SharedResources res;
  @Autowired
  private UserService userService;

  /*
   * Переход на страницу регистрации пользователя.
   */
  @GetMapping("/registration")
  public String registration(Model model) {
    UserService.InfoUser infoUser = userService.getCurrentUser();
    if (infoUser.getTypeRoleUser() >= 0) return res.getUrlRedirectToMain();
    model.addAttribute(res.getUrlInfoUser(), infoUser);
    model.addAttribute(res.getUrlUsrForm(), new Users());
    model.addAttribute(res.getUrlError(), "");
    return "registration";
  }

  /*
   * Создание нового пользователя.
   */
  @PostMapping("/registration")
  public String addUser(@ModelAttribute("userForm") @Valid Users userForm, Model model) {
    String msgError;
    UserService.InfoUser infoUser = userService.getCurrentUser();
    if (infoUser.getTypeRoleUser() >= 0) return res.getUrlRedirectToMain();
    model.addAttribute(res.getUrlInfoUser(), infoUser);
    model.addAttribute(res.getUrlUsrForm(), userForm);
    // Аннотация Valid проверяет выполняются ли ограничения, установленные на поля, в данном случае длина не меньше 2 символов.
    // Если ограничения не были выполнены, то bindingResult будет содержать ошибки.
    if (userForm.getPassword().length() < 2 || !userForm.getPassword().equals(userForm.getPasswordConfirm())) {
      if (userForm.getPassword().length() < 2) msgError = res.getMsgErrorPasswordShortLength();
      else msgError = res.getMsgErrorPasswordDoNotMatch();
      model.addAttribute(res.getUrlError(), msgError);
      return "registration";
    }
    if (userForm.getFullName().length() < 2) {
      model.addAttribute(res.getUrlError(), res.getMsgErrorFullUserShortLength());
      return "registration";
    }
    if (userForm.getUsername().length() < 2 || !userService.addUser(userForm, 1)) {
      if (userForm.getUsername().length() < 2) msgError = res.getMsgErrorUserShortLength();
      else msgError = res.getMsgErrorUser();
      model.addAttribute(res.getUrlError(), msgError);
      return "registration";
    }
    return res.getUrlRedirectToMain();
  }

  @GetMapping("/login")
  public String loginUser(Model model) {
    UserService.InfoUser infoUser = userService.getCurrentUser();
    if (infoUser.getTypeRoleUser() >= 0) return res.getUrlRedirectToMain();
    model.addAttribute(res.getUrlInfoUser(), infoUser);
    return "login";
  }

}
