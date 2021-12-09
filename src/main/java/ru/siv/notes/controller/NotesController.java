package ru.siv.notes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.siv.notes.config.SharedResources;
import ru.siv.notes.model.Status;
import ru.siv.notes.service.NotesService;
import ru.siv.notes.service.UserService;

@Controller
public class NotesController {

  @Autowired
  private SharedResources res;
  @Autowired
  private UserService userService;
  @Autowired
  private NotesService notesService;

  @GetMapping("/note/list")
  public String noteList(Model model) {
    UserService.InfoUser infoUser = userService.getCurrentUser();
    model.addAttribute(res.getUrlInfoUser(), infoUser);
    model.addAttribute(res.getUrlAllNote(), notesService.getAllNote(Status.DELETED, false));
    return "notes/note-list";
  }

  // TODO: Просмотр выбранной статьи.
  // TODO: Добавление новой статьи (не админом).
  // TODO: Редактирование статьи (только автором статьи)
  // TODO: Удаление статьи (только автором статьи или админом)
}
