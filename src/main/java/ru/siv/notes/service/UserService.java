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
   * Возвращает объект пользователя по его ключу и статусу.
   * @param id     ключ пользователя
   * @param status статус пользователя, если null, то любой
   * @param isStat если статус не null, то: true - если соответствует статусу, false - если не соответствует статусу
   * @return если нашел, то объект пользователя, иначе null
   */
  public Users getUserById(Long id, Status status, boolean isStat) {
    Users user = res.getUsersRep().findById(id).orElse(null);
    user = testUserStatus(user, status, isStat);
    if (null != user) log.info("IN UserService.getUserById - id = {}, user = {}", id, user);
    else log.info("IN UserService.getUserById - user not found, id = {}", id);
    return user;
  }

  /**
   * Возвращает объект пользователя по его имени и статусу.
   * @param userName имя пользователя
   * @param status   статус пользователя, если null, то любой
   * @param isStat   если статус не null, то: true - если соответствует статусу, false - если не соответствует статусу
   * @return объект пользователя, либо null, если не найден
   */
  public Users getUserByUsername(String userName, Status status, boolean isStat) {
    Users user = res.getUsersRep().findByUsername(userName);
    user = testUserStatus(user, status, isStat);
    log.info("IN UserService.getUserByUsername - username = {}, user = {}", userName, user);
    return user;
  }

  /**
   * Возвращает список пользователей с заданным статусом.
   * @param status статус пользователя, если null, то любой
   * @param isStat если статус не null, то: true - если соответствует статусу, false - если не соответствует статусу
   * @return список пользователей
   */
  public List<Users> getAllUser(Status status, boolean isStat) {
    List<Users> users;
    if (null == status) users = res.getUsersRep().queryAllUserOredrByName();
    else {
      if (isStat) users = res.getUsersRep().queryUserAllForStatusOrderByName(status);
      else users = res.getUsersRep().queryUserAllForNotStatusOrderByName(status);
    }
    log.info("IN UserService.getAll - users found = {}", users.size());
    return users;
  }

  /**
   * Добавляет пользователя.
   * @param user   объект нового пользователя
   * @param status устанавливаемый статус, если null, то статус не меняет
   * @return true - добавление прошло успешно, false - добавление пользователя не удалось
   */
  public boolean addUser(Users user, Status status) {
    Users userFromDB = res.getUsersRep().findByUsername(user.getUsername());
    if (null != userFromDB) {
      log.info("IN UserService.addUser - a user with the name '{}' exists in the database", user.getUsername());
      return false;
    }
    Roles role = res.getRolesRep().findByName(res.getRoleUser());
    user.setRole(role);
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    if (null != status) user.setStatus(status);
    res.getUsersRep().save(user);
    log.info("IN UserService.addUser - he user with the name '{}' is added to the database", user.getUsername());
    return true;
  }

  /**
   * Клонирование объекта пользователя.
   * @param user клонируемый объект.
   * @return новый объект пользователя
   */
  public Users cloneUser(Users user) {
    Users newUser = new Users();
    newUser.setCreated(user.getCreated());
    newUser.setStatus(user.getStatus());
    newUser.setUsername(user.getUsername());
    newUser.setFullName(user.getFullName());
    newUser.setRole(user.getRole());
    log.info("IN UserService.cloneUser - clone user = {}", newUser);
    return newUser;
  }

  /**
   * Изменяет данные пользователя.
   * @param user изменяемые данные
   * @return true - изменение  прошло успешно, false - изменение не удалось
   */
  public boolean updateUser(Long id, Users user) {
    Users userFromDB = getUserById(id, null, true);
    if (null == userFromDB)
    {
      log.info("IN UserService.updateUser - user not found, id = {}", id);
      return false;
    }
    userFromDB.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    userFromDB.setFullName(user.getFullName());
    res.getUsersRep().save(userFromDB);
    log.info("IN UserService.updateUser - user change was successful, id = {}", id);
    return true;
  }

  /**
   * Помечаем пользователя как удалённого.
   * @param id ключ пользователя
   * @return true - пользователь помечен как удалённый, false - пометить пользователя как удалённый не удалось
   */
  public boolean deleteUser(Long id) {
    Users user = getUserById(id, Status.DELETED, false);
    if (null != user) {
      if (res.getRoleAdmin().equals(user.getRole().getName())) {
        log.info("IN UserService.deleteUser - It is impossible to execute, username = {}, role = {}", user.getUsername(), res.getRoleAdmin());
        return false;
      } else {
        user.setStatus(Status.DELETED);
        res.getUsersRep().save(user);
        log.info("IN UserService.deleteUser - user with id: {} successfully deleted", user.getUsername());
        return true;
      }
    }
    log.info("IN UserService.deleteUser - user not found, id = {}", id);
    return false;
  }

  /**
   * Активация пользователя.
   * @param id ключ пользователя
   * @return true - активация удалась, false - активация не удалась
   */
  public boolean activatedUser(Long id) {
    Users user = getUserById(id, Status.DELETED, true);
    if (null != user) {
      user.setStatus(Status.ACTIVE);
      res.getUsersRep().save(user);
      log.info("IN UserService.activatedUser - user with id: {} successfully activate", user.getUsername());
      return true;
    }
    log.info("IN UserService.activatedUser - user not found, id = {}", id);
    return false;
  }

  /**
   * Получение объекта с информацией о текущем пользователе.
   * @return объект с информацией о текущем пользователе
   */
  public InfoUser getCurrentUser() {
    Authentication authentication = authenticationFacade.getAuthentication();
    String userName = authentication.getName();
    if (res.getAnonymousUser().equals(userName)) return new InfoUser();
    Users user = getUserByUsername(userName, Status.DELETED, false);
    InfoUser infoUser = new InfoUser();
    return infoUser.setUser(user);
  }

  /**
   * Проверка пользователя на заданный статус.
   * @param user   проверяемый пользователь
   * @param status статус пользователя, если null, то любой
   * @param isStat если статус не null, то: true - если соответствует статусу, false - если не соответствует статусу
   * @return проверенный пользователь, или null, если пользователь не соответствует проверенному
   */
  private Users testUserStatus(Users user, Status status, boolean isStat) {
    if (null != user && null != status) {
      if ((isStat && user.getStatus() != status) || (!isStat && user.getStatus() == status)) user = null;
    }
    return user;
  }

  public class InfoUser {

    private Users user;
    private String userName;
    private int typeRoleUser;

    public InfoUser() {
      user = null;
      userName = res.getFullNameGuest();
      typeRoleUser = -1;
    }

    public InfoUser setUser(Users user) {
      if (null != user) {
        this.user = user;
        this.userName = user.getFullName();
        typeRoleUser = res.getRoleAdmin().equals(user.getRole().getAuthority()) ? 0: 1;
      }
      return this;
    }

    public Users getUser() {
      return user;
    }

    public String getUserName() {
      return userName;
    }

    public int getTypeRoleUser() {
      return typeRoleUser;
    }
  }
}
