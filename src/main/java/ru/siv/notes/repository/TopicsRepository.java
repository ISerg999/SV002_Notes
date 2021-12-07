package ru.siv.notes.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.siv.notes.model.Topics;

@Repository
public interface TopicsRepository extends CrudRepository<Topics, Long> {
}
