package ru.siv.notes.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.siv.notes.config.SharedResources;
import ru.siv.notes.model.Notes;
import ru.siv.notes.model.Status;
import ru.siv.notes.model.Topics;
import ru.siv.notes.model.Users;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NotesService {

  @Autowired
  private SharedResources res;
  @Autowired
  private UserService userService;
  @Autowired
  private TopicService topicService;

  /**
   * Возвращает статью по ключу и статусу.
   * @param id     ключ статьи
   * @param status статус статьи, если null, то любая статья
   * @param isStat если статус не null, то: true - если соответствует статусу, false - если не соответствует статусу
   * @return объект статьи при нахождении, иначе null
   */
  public Notes getNoteForId(Long id, Status status, boolean isStat) {
    Notes note = res.getNotesRep().findById(id).orElse(null);
    note = testNoteStatus(note, status, isStat);
    if (null != note) log.info("IN NotesService.getNoteForId - id = {}, note = {}", id, note);
    else log.info("IN NotesService.getNoteForId -  note not found, id = {}", id);
    return note;
  }

  /**
   * Возвращает список статей по статусу.
   * @param status статус статьи, если null, то любая статья
   * @param isStat если статус не null, то: true - если соответствует статусу, false - если не соответствует статусу
   * @return список статей
   */
  public List<Notes> getAllNote(Status status, boolean isStat) {
    List<Notes> notes = new ArrayList<>();
    if (null == status) notes = res.getNotesRep().queryNoteAllOrderByTopicAndAuthorAndTitle();
    else {
      if (isStat) notes = res.getNotesRep().queryNoteAllForStatusOrderByTopicAndAuthorAndTitle(status);
      else notes = res.getNotesRep().queryNoteAllForNotStatusOrderByTopicAndAuthorAndTitle(status);;
    }
    log.info("IN NotesService.getAllNote - notes found = {}", notes.size());
    return notes;
  }

  /**
   * Добавляет статью.
   * @param idAuthor ключ автора
   * @param idTopic  ключ темы
   * @param title    заголовок
   * @param text     содержимое
   * @param status   устанавливаемый статус, если null, то статус не меняет
   * @return при успехе или если статья существует возвращает ключ статьи, если есть статья совпадающая по заголовку, либо тексту, то возвращается -1, в остальных случаях 0
   */
  public Long addNote(Long idAuthor, Long idTopic, String title, String text, Status status) {
    List<Notes> notesTmp = res.getNotesRep().filterByAuthorAndTitle(idAuthor, title);
    if (!(null == notesTmp || notesTmp.isEmpty())) {
      for (Notes el: notesTmp) {
        if (idTopic == el.getTopic().getId() && text.equals(el.getText())) return el.getId();
        else {
          if (idTopic == el.getTopic().getId() || text.equals(el.getText())) return -1L;
        }
      }
      return 0L;
    }
    Users author = userService.getUserById(idAuthor, Status.DELETED, false);
    Topics topic = topicService.getTopicForId(idTopic);
    if (null == author || null == topic) return 0L;
    Notes note = new Notes();
    if (null != status) note.setStatus(status);
    note.setAuthor(author);
    note.setTopic(topic);
    note.setTitle(title);
    note.setText(text);
    note = res.getNotesRep().save(note);
    return note.getId();
  }

  // TODO: Редактирование статьи.
  // TODO: Деактивация статьи статьи.
  // TODO: Активация статьи.

  private Notes testNoteStatus(Notes note, Status status, boolean isStat) {
    if (null != note && null != status) {
      if ((isStat && note.getStatus() != status) || (!isStat && note.getStatus() == status)) note = null;
    }
    return note;
  }
}
