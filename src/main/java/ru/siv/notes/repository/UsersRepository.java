package ru.siv.notes.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.siv.notes.model.Users;

@Repository
public interface UsersRepository extends CrudRepository<Users, Long> {
}
