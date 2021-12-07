package ru.siv.notes.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.siv.notes.config.IAuthenticationFacade;
import ru.siv.notes.config.SharedResources;
import ru.siv.notes.model.Roles;
import ru.siv.notes.model.Status;
import ru.siv.notes.model.Users;

import java.util.List;

@Service
@Slf4j
public class UserService {

  @Autowired
  private SharedResources res;
  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Autowired
  private IAuthenticationFacade authenticationFacade;

  /**
   * Возвращает объект пользователя по его ключу.
   * @param id    ключ пользователя
   * @param isAny true - любого, false - для тех, кто не помечен как DELETED
   * @return если нашел, то объект пользователя, иначе null
   */
  public Users getUserById(Long id, boolean isAny) {
    Users user = res.getUsersRep().findById(id).orElse(null);
    if (!isAny && null != user && user.getStatus() == Status.DELETED) user = null;
    if (null != user) {
      log.info("IN UserService.getUserById - id = {}, user = {}, new id = {}", id, user, user.getId());
    } else {
      log.info("IN UserService.getUserById - user not found, id = {}", id);
    }
    return user;
  }

  /**
   * Возвращает объект пользователя по его имени.
   * @param userName имя пользователя
   * @param isAny    true - любого, false - для тех, кто не помечен как DELETED
   * @return объект пользователя, либо null, если не найден
   */
  public Users getUserByUsername(String userName, boolean isAny) {
    Users user = res.getUsersRep().findByUsername(userName);
    if (!isAny && null != user && user.getStatus() == Status.DELETED) user = null;
    log.info("IN UserService.getUserByUsername - username = {}, user = {}", userName, user);
    return user;
  }

  /**
   * Возвращает список пользователей.
   * @param isAny true - любого, false - для тех, кто не помечен как DELETED
   * @return список пользователей
   */
  public List<Users> getAllUser(boolean isAny) {
    List<Users> users;
    if (isAny) users = (List<Users>) res.getUsersRep().findAll();
    else users = res.getUsersRep().queryAllNotDeleted();
    log.info("IN UserService.getAll - users found = {}", users.size());
    return users;
  }

  /**
   * Добавляет пользователя.
   * @param user  объект нового пользователя
   * @param isNew 1 - устанавливает статус в ACTIVE, 0 - оставляет статус по умолчанию, что был в объекте, -1 устанавливает в NOT_ACTIVE
   * @return true - добавление прошло успешно, false - добавление пользователя не удалось
   */
  public boolean addUser(Users user, int isNew) {
    Users userFromDB = res.getUsersRep().findByUsername(user.getUsername());
    if (null != userFromDB) {
      log.info("IN UserService.addUser - a user with the name '{}' exists in the database", user.getUsername());
      return false;
    }
    Roles role = res.getRolesRep().findByName(res.getRoleUser());
    user.setRole(role);
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    if (isNew > 0) user.setStatus(Status.ACTIVE);
    else {
      if (isNew < 0) user.setStatus(Status.NOT_ACTIVE);
    }
    res.getUsersRep().save(user);
    log.info("IN UserService.addUser - he user with the name '{}' is added to the database", user.getUsername());
    return true;
  }

  /**
   * Помечаем пользователя по ключу как удалённого.
   * @param id ключ пользователя
   * @return true - пользователь помечен как удалённый, false - пометить пользователя как удалённый не удалось
   */
  public boolean deleteUser(Long id) {
    Users user = getUserById(id, false);
    if (null != user) {
      if (res.getRoleAdmin().equals(user.getRole().getName())) {
        log.info("IN UserService.deleteUser - It is impossible to execute, username = {}, role = {}", user.getUsername(), res.getRoleAdmin());
        return false;
      } else {
        String username = user.getUsername();
        // TODO: Деактивируем все статьи данного пользователя.
        user.setStatus(Status.DELETED);
        res.getUsersRep().save(user);
        log.info("IN UserService.deleteUser - user with id: {} successfully deleted", username);
        return true;
      }
    }
    log.info("IN UserService.deleteUser - user not found, id = {}", id);
    return false;
  }

  public InfoUser getCurrentUser() {
    Authentication authentication = authenticationFacade.getAuthentication();
    String userName = authentication.getName();
    if (res.getAnonymousUser().equals(userName)) return new InfoUser();
    Users user = getUserByUsername(userName, false);
    return new InfoUser(user);
  }

}
