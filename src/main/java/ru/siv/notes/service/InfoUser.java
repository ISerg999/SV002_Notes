package ru.siv.notes.service;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import ru.siv.notes.config.SharedResources;
import ru.siv.notes.model.Users;

/**
 * Класс содержащий информацию о текущем пользователе.
 * user - ссылается на сам объект пользователя.
 * userName - полное имя текущего пользователя.
 * typeRoleUser - определяет тип текущего пользователя, -1 - гость, 0 - администратор, 1 - обычный пользователь.
 */
@Getter
@ToString
public class InfoUser {

  @Autowired
  private SharedResources res;

  private Users user;
  private String userName;
  private int typeRoleUser;

  public InfoUser() { setGuest(); }

  public InfoUser(Users user) {
    if (null == user) setGuest();
    else {
      this.user = user;
      this.userName = user.getFullName();
      typeRoleUser = res.getRoleUser().equals(user.getRole().getAuthority()) ? 0: 1;
    }
  }

  private void setGuest() {
    user = null;
    userName = res.getFullNameGuest();
    typeRoleUser = -1;
  }
}
