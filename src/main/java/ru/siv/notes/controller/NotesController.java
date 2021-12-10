package ru.siv.notes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.siv.notes.config.SharedResources;
import ru.siv.notes.model.Notes;
import ru.siv.notes.model.Status;
import ru.siv.notes.service.NotesService;
import ru.siv.notes.service.TopicService;
import ru.siv.notes.service.UserService;

@Controller
public class NotesController {

  @Autowired
  private SharedResources res;
  @Autowired
  private UserService userService;
  @Autowired
  private NotesService notesService;
  @Autowired
  private TopicService topicService;

  @GetMapping("/note/list")
  public String noteList(Model model) {
    UserService.InfoUser infoUser = userService.getCurrentUser();
    model.addAttribute(res.getUrlInfoUser(), infoUser);
    model.addAttribute(res.getUrlAllNote(), notesService.getAllNote(Status.DELETED, false));
    return "notes/note-list";
  }

  @GetMapping("/note/{id}")
  public String noteRead(@PathVariable(value="id") Long id, Model model) {
    Notes note = notesService.getNoteForId(id, Status.DELETED, false);
    if (null == note) return res.getUrlRedirectToNoteList();
    UserService.InfoUser infoUser = userService.getCurrentUser();
    model.addAttribute(res.getUrlInfoUser(), infoUser);
    model.addAttribute(res.getUrlNote(), note);
    return "notes/note-read";
  }

  @GetMapping("/note/add")
  public String noteAdd(Model model) {
    UserService.InfoUser infoUser = userService.getCurrentUser();
    if (infoUser.getTypeRoleUser() <= 0) return res.getUrlRedirectToNoteList();
    model.addAttribute(res.getUrlInfoUser(), infoUser);
    Notes note = notesService.getBlankNote(infoUser.getUser());
    model.addAttribute(res.getUrlNewNote(), note);
    model.addAttribute(res.getUrlTopicList(), topicService.getAllTopicWithNull());
    model.addAttribute(res.getUrlError(), "");
    return "notes/note-add";
  }

  @PostMapping("/note/add")
  public String notePostAdd(@RequestParam(name = "idTopic") Long idTopic,
                            @RequestParam(name = "noteTitle") String noteTitle,
                            @RequestParam(name = "noteText") String noteText,
                            Model model) {
    UserService.InfoUser infoUser = userService.getCurrentUser();
    if (infoUser.getTypeRoleUser() > 0) {
      String msgError = "";
      if (noteTitle.isEmpty()) msgError = "msg.error.note.title.shortLength";
      else {
        if (noteText.isEmpty()) msgError = "msg.error.note.text.shortLength";
        else {
          Long newId = notesService.addNote(infoUser.getUser().getId(), idTopic, noteTitle, noteText, Status.ACTIVE);
          if (newId < 0L) msgError = "msg.error.note.matching";
          else {
            if (newId > 0L) return res.getUrlRedirectToRead() + newId;
          }
        }
      }
      if (!msgError.isEmpty()) {
        model.addAttribute(res.getUrlInfoUser(), infoUser);
        Notes note = notesService.getBlankNote(infoUser.getUser());
        note.setTopic(topicService.getTopicForId(idTopic));
        note.setTitle(noteTitle);
        note.setText(noteText);
        model.addAttribute(res.getUrlNewNote(), note);
        model.addAttribute(res.getUrlTopicList(), topicService.getAllTopicWithNull());
        model.addAttribute(res.getUrlError(), msgError);
        return "notes/note-add";
      }
    }
    return res.getUrlRedirectToNoteList();
  }

  @GetMapping("/note/{id}/edit")
  public String noteEdit(@PathVariable(value="id") Long id, Model model) {
    UserService.InfoUser infoUser = userService.getCurrentUser();
    if (infoUser.getTypeRoleUser() <= 0) return res.getUrlRedirectToNoteList();
    model.addAttribute(res.getUrlInfoUser(), infoUser);
    Notes note = notesService.getNoteForId(id, Status.DELETED, false);
    if (null == note || note.getAuthor().getId() != infoUser.getUser().getId()) return res.getUrlRedirectToNoteList();
    model.addAttribute(res.getUrlNewNote(), note);
    model.addAttribute(res.getUrlTopicList(), topicService.getAllTopicWithNull());
    model.addAttribute(res.getUrlError(), "");
    return "notes/note-edit";
  }

  @PostMapping("/note/{id}/edit")
  public String notePostUpdate(@PathVariable(value="id") Long id,
                               @RequestParam(name = "idTopic") Long idTopic,
                               @RequestParam(name = "noteTitle") String noteTitle,
                               @RequestParam(name = "noteText") String noteText,
                               Model model) {
    UserService.InfoUser infoUser = userService.getCurrentUser();
    if (infoUser.getTypeRoleUser() > 0) {
      String msgError = "";
      if (noteTitle.isEmpty()) msgError = "msg.error.note.title.shortLength";
      else {
        if (noteText.isEmpty()) msgError = "msg.error.note.text.shortLength";
        else {
          Long newId = notesService.updateNote(id, idTopic, noteTitle, noteText);
          if (newId > 0) return res.getUrlRedirectToRead() + id;
          else {
            if (newId < 0) msgError = "msg.error.note.matching";
          }
        }
      }
      if (!msgError.isEmpty()) {
        model.addAttribute(res.getUrlInfoUser(), infoUser);
        Notes note = notesService.getNoteForId(id, Status.DELETED, false);
        if (null == note || note.getAuthor().getId() != infoUser.getUser().getId()) return res.getUrlRedirectToNoteList();
        note.setTopic(topicService.getTopicForId(idTopic));
        note.setTitle(noteTitle);
        note.setText(noteText);
        model.addAttribute(res.getUrlNewNote(), note);
        model.addAttribute(res.getUrlTopicList(), topicService.getAllTopicWithNull());
        model.addAttribute(res.getUrlError(), msgError);
        return "notes/note-edit";
      }
    }
    return res.getUrlRedirectToNoteList();
  }

  @GetMapping("/note/{id}/remove")
  public String noteRemove(@PathVariable(value="id") Long id, Model model) {
    UserService.InfoUser infoUser = userService.getCurrentUser();
    Notes note = notesService.getNoteForId(id, Status.DELETED, false);
    if (null != note) {
      if (0 == infoUser.getTypeRoleUser() || infoUser.getUser().getId() == note.getAuthor().getId()) {
        notesService.deleteNote(id);
      }
    }
    return res.getUrlRedirectToNoteList();
  }
}
