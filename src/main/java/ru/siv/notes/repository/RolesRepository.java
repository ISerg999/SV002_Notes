package ru.siv.notes.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.siv.notes.model.Roles;

@Repository
public interface RolesRepository extends CrudRepository<Roles, Long> {
  Roles findByName(String name);
}
