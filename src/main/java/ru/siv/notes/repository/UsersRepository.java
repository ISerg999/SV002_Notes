package ru.siv.notes.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.siv.notes.model.Users;

import java.util.List;

@Repository
public interface UsersRepository extends CrudRepository<Users, Long> {
  Users findByUsername(String username);
  @Query("SELECT u FROM Users u WHERE u.status != 'DELETED'")
  List<Users> queryAllNotDeleted();
}
