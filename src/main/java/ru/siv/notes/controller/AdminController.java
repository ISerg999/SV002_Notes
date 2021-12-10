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
import ru.siv.notes.service.NotesService;
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
  @Autowired
  private NotesService notesService;

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
    if (uId > 0) {
      notesService.deleteNoteForAuthor(uId);
      userService.deleteUser(uId);
    }
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
    notesService.activateNoteForAuthor(uId);
    return res.getUrlRedirectToUserListDisable();
  }

  // Темы. Просмотр, добавление, редактирование, удаление.

  @GetMapping("/admin/topic/list")
  public String topicList(Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    model.addAttribute(res.getUrlAllTopic(), topicService.getAllTopic());
    return "admins/topic/topic-list";
  }

  @GetMapping("/admin/topic/add")
  public String topicAdd(Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    model.addAttribute(res.getUrlError(), "");
    return "admins/topic/topic-add";
  }

  @PostMapping("/admin/topic/add")
  public String topicPostAdd(@RequestParam(name = "nameTopic") String nameTopic, Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    String msgError = "";
    if (nameTopic.length() < 1) msgError = res.getMsgErrorTopicShortLength();
    else {
      if (!topicService.addTopic(nameTopic)) msgError = res.getMsgErrorTopic();
    }
    if (!msgError.isEmpty()) {
      model.addAttribute(res.getUrlError(), msgError);
      return "admins/topic/topic-add";
    } else return res.getUrlRedirectToTopicList();
  }

  @GetMapping("/admin/topic/{id}/edit")
  public String topicEdit(@PathVariable(value="id") Long id, Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    String nameTopic = topicService.getNameForId(id);
    if (nameTopic.equals(res.getTopicNull())) return res.getUrlRedirectToTopicList();
    model.addAttribute(res.getUrlTopicId(), id);
    model.addAttribute(res.getUrlTopicName(), nameTopic);
    model.addAttribute(res.getUrlError(), "");
    return "admins/topic/topic-edit";
  }

  @PostMapping("/admin/topic/{id}/edit")
  public String topicPostUpdate(@PathVariable(value="id") Long id, @RequestParam(name = "nameTopic") String nameTopic, Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    String msgError = "";
    String oldName = topicService.getNameForId(id);
    if (nameTopic.length() < 1 || oldName.equals(nameTopic)) {
      if (nameTopic.length() < 1) msgError = res.getMsgErrorTopicShortLength();
      else msgError = res.getMsgErrorTopic();
    } else {
      if (!topicService.updateTopic(id, nameTopic)) msgError = res.getMsgErrorAdd();
    }
    if (!msgError.isEmpty()) {
      model.addAttribute(res.getUrlError(), msgError);
      model.addAttribute(res.getUrlTopicId(), id);
      model.addAttribute(res.getUrlTopicName(), oldName);
      return "admins/topic/topic-edit";
    } else return res.getUrlRedirectToTopicList();
  }

  @GetMapping("/admin/topic/{id}/remove")
  public String topicDelete(@PathVariable(value="id") Long id, Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    notesService.removeNotesForTopic(id);
    topicService.removeTopic(id);
    return res.getUrlRedirectToTopicList();
  }

  // Удалённые статьи, если автор не удалён. Просмотр списка статей, просмотр статьи, восстановление статьи.

  @GetMapping("/admin/note/list/disable")
  public String noteListDeleted(Model model) {
    if (!testUserRoleAdmin(model)) return res.getUrlRedirectToMain();
    model.addAttribute(res.getUrlAllNote(), notesService.getAllNote(Status.DELETED, true));
    return "admins/note/note-list-deleted";
  }

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
