package ru.siv.notes.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.siv.notes.repository.NotesRepository;
import ru.siv.notes.repository.RolesRepository;
import ru.siv.notes.repository.TopicsRepository;
import ru.siv.notes.repository.UsersRepository;
import ru.siv.notes.service.UserService;

/**
 * Класс содержащий общедоступные ресурсы.
 */
@Component
@Data
public class SharedResources {

  private String roleUser = "USER";
  private String roleAdmin = "ADMIN";
  private String anonymousUser = "anonymousUser";
  private String fullNameGuest = "";

  private String urlInfoUser = "infoUser";
  private String urlRedirectToMain = "redirect:/";

  @Autowired
  private RolesRepository rolesRep;
  @Autowired
  private UsersRepository usersRep;
  @Autowired
  private TopicsRepository topicsRep;
  @Autowired
  private NotesRepository notesRep;

}
