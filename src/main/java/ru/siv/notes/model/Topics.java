package ru.siv.notes.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "topics")
@Getter
@Setter
@ToString
public class Topics {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "name", length = 80)
  @NonNull
  private String name;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Topics topics = (Topics) o;
    return id != null && Objects.equals(id, topics.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}
