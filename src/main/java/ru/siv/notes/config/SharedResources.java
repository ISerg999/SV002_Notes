package ru.siv.notes.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.siv.notes.repository.NotesRepository;
import ru.siv.notes.repository.RolesRepository;
import ru.siv.notes.repository.TopicsRepository;
import ru.siv.notes.repository.UsersRepository;

/**
 * Класс содержащий общедоступные ресурсы.
 */
@Component
@Data
public class SharedResources {

  private String roleUser = "USER";
  private String roleAdmin = "ADMIN";
  private String anonymousUser = "anonymousUser";
  private String fullNameGuest = "Гость";
  private String topicNull = "<Без темы>";

  private String urlInfoUser = "infoUser";
  private String urlUsrForm = "userForm";
  private String urlError = "msgError";
  private String urlRedirectToMain = "redirect:/";
  private String msgErrorPasswordDoNotMatch = "Пароли не совпадают.";
  private String msgErrorPasswordShortLength = "Короткая длина пароля.";
  private String msgErrorUser = "Пользователь с таким именем уже существует.";
  private String msgErrorUserShortLength = "Короткая длина имени пользователя.";
  private String msgErrorFullUserShortLength = "Короткая длина полного имени пользователя.";

  @Autowired
  private RolesRepository rolesRep;
  @Autowired
  private UsersRepository usersRep;
  @Autowired
  private TopicsRepository topicsRep;
  @Autowired
  private NotesRepository notesRep;

}
