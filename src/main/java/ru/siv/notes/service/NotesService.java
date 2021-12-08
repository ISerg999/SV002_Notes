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
          if (idTopic == el.getTopic().getId() || text.equals(el.getText())) {
            log.info("IN NotesService.addNote - the article with the current title and topic or text existst, title = {}, topic = {}, text = {}", title, el.getTopic().getName(), text);
            return -1L;
          }
        }
      }
      log.info("IN NotesService.addNote - article creation error, author = {}, title = {}", idAuthor, title);
      return 0L;
    }
    Users author = userService.getUserById(idAuthor, Status.DELETED, false);
    Topics topic = topicService.getTopicForId(idTopic);
    if (null == author || null == topic) {
      log.info("IN NotesService.addNote - article creation error, idAuthor = {}, idTopic = {}", idAuthor, idTopic);
      return 0L;
    }
    Notes note = new Notes();
    if (null != status) note.setStatus(status);
    note.setAuthor(author);
    note.setTopic(topic);
    note.setTitle(title);
    note.setText(text);
    note = res.getNotesRep().save(note);
    log.info("IN NotesService.addNote - article created, id = {}, title = {}", note.getId(), title);
    return note.getId();
  }

  /**
   * Изменение статьи.
   * @param id      ключ статьи
   * @param idTopic ключ темы
   * @param title   заголовок
   * @param text    текст
   * @return при успехе возвращает ключ статьи, если есть совпадение по заголовку и тексту, возвращает -1, иначе 0.
   */
  public Long updateNote(Long id, Long idTopic, String title, String text) {
    Notes note = getNoteForId(id, Status.DELETED, false);
    if (null == title || null == note) {
      log.info("IN NotesService.updateNote - article update error, id = {}, title = {}", id, title);
      return 0L;
    }
    if (idTopic == note.getTopic().getId() && title.equals(note.getTitle()) && text.equals(note.getText())) {
      log.info("IN NotesService.updateNote - an article with specified parameters exists, idTopic = {} and title = {} and text", idTopic, title);
      return id;
    }
    Topics topic = topicService.getTopicForId(idTopic);
    List<Notes> notes = res.getNotesRep().filterCompareByTitleAndByText(title, text);
    if (null != notes && !notes.isEmpty()) {
      log.info("IN NotesService.updateNote - an article with this title or with this text exists, title = {} or text", title);
      return -1L;
    }
    note.setTopic(topic);
    note.setTitle(title);
    note.setText(text);
    res.getNotesRep().save(note);
    log.info("IN NotesService.updateNote - the update was successful, id = {}", note.getId());
    return note.getId();
  }

  /**
   * Помечаем статью как удалённую.
   * @param id ключ статьи
   * @return true - статья помечена как удалённая, false - пометить статью как удалённую не удалось
   */
  public boolean deleteNote(Long id) {
    Notes note = getNoteForId(id, Status.DELETED, false);
    if (!noteSetStatus(note, Status.DELETED)) {
      log.info("IN NotesService.deleteNote - it is not possible to delete an note, id = {}", id);
      return false;
    }
    log.info("IN NotesService.deleteNote - notes is marked for deletion, id = {}", id);
    return true;
  }

  /**
   * Снятие удаление у статьи.
   * @param id ключ статьи
   * @return true - статус удаленный снят, false - статус удалённый снять не получилось
   */
  public boolean activatedNote(Long id) {
    Notes note = getNoteForId(id, Status.DELETED, true);
    if (!noteSetStatus(note, Status.ACTIVE)) {
      log.info("IN NotesService.activatedNote - it is impossible to restore the note, id = {}", id);
      return false;
    }
    log.info("IN NotesService.activatedNote - the note has been reinstated, id = {}", id);
    return true;
  }

  /**
   * Помечаем статьи как удалённые для заданного автора.
   * @param idAuthor ключ автора
   */
  public void deleteNoteForAuthor(Long idAuthor) {
    Users author = userService.getUserById(idAuthor, null, true);
    if (null == author) {
      log.info("IN NotesService.deleteNoteForAuthor - author not found, idAuthor = {}", idAuthor);
      return;
    }
    List<Notes> notes = res.getNotesRep().filterNoteAllForAuthor(idAuthor);
    if (null == notes || notes.isEmpty()) {
      log.info("IN NotesService.deleteNoteForAuthor - the author has no notes, idAuthor = {}", idAuthor);
      return;
    }
    for (Notes el: notes) {
      noteSetStatus(el, Status.DELETED);
    }
    log.info("IN NotesService.deleteNoteForAuthor - notes deactivated, idAuthor = {}", idAuthor);
  }

  /**
   * Восстанавливаем статьи для заданного автора.
   * @param idAuthor ключ автора
   */
  public void activateNoteForAuthor(Long idAuthor) {
    Users author = userService.getUserById(idAuthor, Status.DELETED, false);
    if (null == author) {
      log.info("IN NotesService.activateNoteForAuthor - author not found, idAuthor = {}", idAuthor);
      return;
    }
    List<Notes> notes = res.getNotesRep().filterNoteAllForAuthor(idAuthor);
    if (null == notes || notes.isEmpty()) {
      log.info("IN NotesService.activateNoteForAuthor - the author has no notes, idAuthor = {}", idAuthor);
      return;
    }
    for (Notes el: notes) {
      noteSetStatus(el, Status.ACTIVE);
    }
    log.info("IN NotesService.activateNoteForAuthor - notes deactivated, idAuthor = {}", idAuthor);
  }

  /**
   * Удаление тем у статей с заданной темой.
   * @param idTopic ключ удаляемой темы
   */
  public void removeTopicForNotes(Long idTopic) {
    Topics topic = topicService.getTopicForId(idTopic);
    if (null == topic) return;
    // TODO: Получаем список всех статей не зависимо от статуса с данной темой.
    // TODO: Обнуляем темы у найденных статей.
  }

  /**
   * Проверка статьи на статус.
   * @param note   объект статьи
   * @param status проверяемый статус, если null, то статус может быть любой
   * @param isStat true - соответствует статусу, false - не соответствует статусу
   * @return объект статьи, либо null, если статусу не соответствует
   */
  private Notes testNoteStatus(Notes note, Status status, boolean isStat) {
    if (null != note && null != status) {
      if ((isStat && note.getStatus() != status) || (!isStat && note.getStatus() == status)) note = null;
    }
    return note;
  }

  /**
   * Устанавливает в статье заданный статус
   * @param note   объект статьи
   * @param status устанавливаемый статус
   * @return true - изменение статуса удачно, false - статус не был изменен
   */
  private boolean noteSetStatus(Notes note, Status status) {
    if (null == note) return false;
    note.setStatus(status);
    res.getNotesRep().save(note);
    return true;
  }
}
