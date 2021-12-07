package ru.siv.notes.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "notes")
@Getter
@Setter
@ToString
public class Notes extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "author_id")
  private Users author;

  @ManyToOne
  @JoinColumn(name = "topic_id")
  private Topics topic;

  @Column(name = "title", unique = true, length = 160)
  @NonNull
  private String title;

  @Column(name="text", length = 32768)
  private String text;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Notes notes = (Notes) o;
    return id != null && Objects.equals(id, notes.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}
