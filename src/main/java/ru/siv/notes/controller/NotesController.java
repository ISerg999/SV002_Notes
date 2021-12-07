package ru.siv.notes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.siv.notes.config.SharedResources;

@Controller
public class NotesController {

  @Autowired
  private SharedResources res;

  // TODO: Просмотр статей.
  // TODO: Просмотр выбранной статьи.
  // TODO: Добавление новой статьи (не админом).
  // TODO: Редактирование статьи (только автором статьи)
  // TODO: Удаление статьи (только автором статьи или админом)
}
