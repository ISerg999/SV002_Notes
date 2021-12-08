package ru.siv.notes.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.siv.notes.model.Status;
import ru.siv.notes.model.Users;

import java.util.List;

@Repository
public interface UsersRepository extends CrudRepository<Users, Long> {
  Users findByUsername(String username);
  @Query("SELECT u FROM Users u ORDER BY u.fullName")
  List<Users> queryAllUserOredrByName();
  @Query("SELECT u FROM Users u WHERE u.status = :status ORDER BY u.fullName")
  List<Users> queryUserAllForStatusOrderByName(Status status);
  @Query("SELECT u FROM Users u WHERE u.status != :status ORDER BY u.fullName")
  List<Users> queryUserAllForNotStatusOrderByName(Status status);
}
