package ru.siv.notes.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @CreationTimestamp
  @Column(name = "created", columnDefinition = "TIMESTAMP")
  protected LocalDateTime created;

  @UpdateTimestamp
  @Column(name = "updated", columnDefinition = "TIMESTAMP")
  protected LocalDateTime updated;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  protected Status status;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    BaseEntity that = (BaseEntity) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}
