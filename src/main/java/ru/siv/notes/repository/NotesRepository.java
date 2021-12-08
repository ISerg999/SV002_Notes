package ru.siv.notes.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.siv.notes.model.Notes;
import ru.siv.notes.model.Status;

import java.util.List;

@Repository
public interface NotesRepository extends CrudRepository<Notes, Long> {
  @Query("SELECT n FROM Notes n ORDER BY n.topic.name, n.author.fullName, n.title")
  List<Notes> queryNoteAllOrderByTopicAndAuthorAndTitle();
  @Query("SELECT n FROM Notes n WHERE n.status = :status ORDER BY n.topic.name, n.author.fullName, n.title")
  List<Notes> queryNoteAllForStatusOrderByTopicAndAuthorAndTitle(Status status);
  @Query("SELECT n FROM Notes n WHERE n.status != :status ORDER BY n.topic.name, n.author.fullName, n.title")
  List<Notes> queryNoteAllForNotStatusOrderByTopicAndAuthorAndTitle(Status status);
  @Query("SELECT n FROM Notes n WHERE n.author.id = :idAuthor AND lower(n.title) = lower(:title)")
  List<Notes> filterByAuthorAndTitle(Long idAuthor, String title);
  @Query("SELECT n FROM Notes n WHERE lower(n.title) = lower(:title) AND lower(n.text) = lower(:text)")
  List<Notes> filterCompareByTitleAndByText(String title, String text);
  @Query("SELECT n FROM Notes n WHERE n.author.id = :idAuthor")
  List<Notes> filterNoteAllForAuthor(Long idAuthor);
}
