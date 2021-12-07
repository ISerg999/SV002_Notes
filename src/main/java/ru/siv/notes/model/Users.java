package ru.siv.notes.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class Users extends BaseEntity {

  @Column(name = "username", unique = true, length = 100)
  @Size(min=2, message = "Не меньше 2 знаков")
  @NonNull
  private String username;

  @Column(name = "password")
  @Size(min=2, message = "Не меньше 2 знаков")
  @NonNull
  private String password;

  @Column(name = "full_name")
  @Size(min=2, message = "Не меньше 2 знаков")
  @NonNull
  private String fullName;

  @ManyToOne
  @JoinColumn(name = "role_id")
  private Roles role;

  @Transient
  private String passwordConfirm;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Users users = (Users) o;
    return id != null && Objects.equals(id, users.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}
