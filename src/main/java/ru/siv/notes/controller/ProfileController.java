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

/**
 * Позволяет пользователю редактировать свое полное имя и пароль.
 */
@Controller
public class ProfileController {

  @Autowired
  private SharedResources res;
  @Autowired
  private UserService userService;

  @GetMapping("/profile")
  public String userEdit(Model model) {
    UserService.InfoUser infoUser = userService.getCurrentUser();
    if (infoUser.getTypeRoleUser() < 0) return res.getUrlRedirectToMain();
    model.addAttribute(res.getUrlInfoUser(), infoUser);
    Users newUser = userService.cloneUser(infoUser.getUser());
    model.addAttribute(res.getUrlUsrForm(), newUser);
    model.addAttribute(res.getUrlError(), "");
    return "profile";
  }

  @PostMapping("/profile")
  public String userPostUpdate(@ModelAttribute("userForm") @Valid Users userForm, Model model) {
    String msgError;
    UserService.InfoUser infoUser = userService.getCurrentUser();
    if (infoUser.getTypeRoleUser() < 0) return res.getUrlRedirectToMain();
    model.addAttribute(res.getUrlInfoUser(), infoUser);
    model.addAttribute(res.getUrlUsrForm(), userForm);
    if (userForm.getPassword().length() < 2 || !userForm.getPassword().equals(userForm.getPasswordConfirm())) {
      if (userForm.getPassword().length() < 2) msgError = res.getMsgErrorPasswordShortLength();
      else msgError = res.getMsgErrorPasswordDoNotMatch();
      model.addAttribute(res.getUrlError(), msgError);
      return "profile";
    }
    if (userForm.getFullName().length() < 2) {
      model.addAttribute(res.getUrlError(), res.getMsgErrorFullUserShortLength());
      return "profile";
    }
    userService.updateUser(infoUser.getUser().getId(), userForm);
    return res.getUrlRedirectToMain();
  }
}
