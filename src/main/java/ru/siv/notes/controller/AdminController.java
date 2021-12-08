package ru.siv.notes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.siv.notes.config.SharedResources;
import ru.siv.notes.model.Status;
import ru.siv.notes.service.TopicService;
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
  @Autowired
  private TopicService topicService;

  // Просмотр списка и деактивация действующих пользователей.

  @GetMapping("admin/user/list/enable")
  public String userListEnable(Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    model.addAttribute(res.getUrlAllUser(), userService.getAllUser(Status.DELETED, false));
    return "admins/user-list-enable";
  }

  @GetMapping("/admin/user/{uId}/remove")
  public String  deleteUser(@PathVariable(value = "uId") long uId, Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    if (uId > 0) userService.deleteUser(uId);
    return res.getUrlRedirectToUserListEnable();
  }

  // Просмотр списка "удалённых" пользователей и их активация.

  @GetMapping("admin/user/list/disable")
  public String userListDeleted(Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    model.addAttribute(res.getUrlAllUser(), userService.getAllUser(Status.DELETED, true));
    return "admins/user-list-disable";
  }

  @GetMapping("/admin/user/{uId}/activated")
  public String activatedUser(@PathVariable(value = "uId") long uId, Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    userService.activatedUser(uId);
    return res.getUrlRedirectToUserListDisable();
  }

  // Темы. Просмотр, добавление, редактирование, удаление.

  @GetMapping("/admin/topic/list")
  public String topicList(Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    model.addAttribute(res.getUrlAllTopic(), topicService.getAllTopic());
    return "admins/topic-list";
  }

  @GetMapping("/admin/topic/add")
  public String topicAdd(Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    model.addAttribute(res.getUrlError(), "");
    return "admins/topic-add";
  }

  @PostMapping("/admin/topic/add")
  public String topicPostAdd(@RequestParam(name = "nameTopic") String nameTopic, Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    if (nameTopic.length() < 1) {
      model.addAttribute(res.getUrlError(), res.getMsgErrorTopicShortLength());
      return "admins/topic-add";
    }
    if (!topicService.addTopic(nameTopic)) {
      model.addAttribute(res.getUrlError(), res.getMsgErrorTopic());
      return "admins/topic-add";
    }
    return res.getUrlRedirectToTopicList();
  }

  @GetMapping("/admin/topic/{id}/edit")
  public String topicEdit(@PathVariable(value="id") Long id, Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    String nameTopic = topicService.getNameForId(id);
    if (nameTopic.equals(res.getTopicNull())) return res.getUrlRedirectToTopicList();
    model.addAttribute(res.getUrlTopicId(), id);
    model.addAttribute(res.getUrlTopicName(), nameTopic);
    model.addAttribute(res.getUrlError(), "");
    return "admins/topic-edit";
  }

  @PostMapping("/admin/topic/{id}/edit")
  public String topicPostUpdate(@PathVariable(value="id") Long id, @RequestParam(name = "nameTopic") String nameTopic, Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    String oldName = topicService.getNameForId(id);
    if (nameTopic.length() < 1 || oldName.equals(nameTopic)) {
      if (nameTopic.length() < 1) model.addAttribute(res.getUrlError(), res.getMsgErrorTopicShortLength());
      else model.addAttribute(res.getUrlError(), res.getMsgErrorTopic());
      model.addAttribute(res.getUrlTopicId(), id);
      model.addAttribute(res.getUrlTopicName(), oldName);
      return "admins/topic-edit";
    }
    if (!topicService.updateTopic(id, nameTopic)) {
      model.addAttribute(res.getUrlError(), res.getMsgErrorAdd());
      model.addAttribute(res.getUrlTopicId(), id);
      model.addAttribute(res.getUrlTopicName(), oldName);
      return "admins/topic-edit";
    }
    return res.getUrlRedirectToTopicList();
  }

  @GetMapping("/admin/topic/{id}/remove")
  public String topicDelete(@PathVariable(value="id") Long id, Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    topicService.removeTopic(id);
    return res.getUrlRedirectToTopicList();
  }

  // Удалённые статьи, если автор не удалён. Просмотр списка статей, просмотр статьи, восстановление статьи.

  // TODO: Список удалённых статей (если не удалён автор).
  // TODO: Просмотр удалённой статьи (если не удалён автор).
  // TODO: Восстановление удалённой статьи (если не удалён автор).

  /**
   * Проверка текущего пользователя на роль администратора.
   * @param model модель обработки страницы
   * @return true - проверка успешна, false - проверка провалилась
   */
  private boolean testUserRoleAdmin(Model model) {
    UserService.InfoUser infoUser = userService.getCurrentUser();
    if (0 != infoUser.getTypeRoleUser()) return false;
    model.addAttribute(res.getUrlInfoUser(), infoUser);
    return true;
  }
}
