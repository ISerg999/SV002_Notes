package ru.siv.notes.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.siv.notes.config.SharedResources;

@Service
@Slf4j
public class NotesService {

  @Autowired
  private SharedResources res;

  // TODO: Методы для работы со статьями.
}
