package ru.siv.notes.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.siv.notes.model.Topics;

import java.util.List;

@Repository
public interface TopicsRepository extends CrudRepository<Topics, Long> {
  Topics findByNameIgnoreCase(String name);
  @Query("SELECT t FROM Topics t ORDER BY t.name")
  List<Topics> filterFindAllOrderByName();
}
