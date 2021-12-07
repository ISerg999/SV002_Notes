package ru.siv.notes.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.siv.notes.model.Notes;

@Repository
public interface NotesRepository extends CrudRepository<Notes, Long> {
}
