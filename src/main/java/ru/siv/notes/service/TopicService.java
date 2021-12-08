package ru.siv.notes.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.siv.notes.config.SharedResources;
import ru.siv.notes.model.Topics;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TopicService {

  @Autowired
  private SharedResources res;

  /**
   * Поиск название темы по ключу.
   * @param id ключ темы
   * @return название темы
   */
  public String getNameForId(Long id) {
    String name;
    Topics topic = res.getTopicsRep().findById(id).orElse(null);
    if (null == topic) name = res.getTopicNull();
    else name = topic.getName();
    log.info("IN TopicService.getTopicNameById - id = {}, name = {}", id, name);
    return name;
  }

  public Topics getTopicForId(Long id) {
    Topics topic = res.getTopicsRep().findById(id).orElse(null);
    log.info("IN TopicService.getTopicForId - id = {}, topic = {}", id, topic);
    return topic;
  }
  /**
   * Поиск ключа по названию темы.
   * @param name название темы
   * @return ключ темы, если найдено, 0 если без категории и -1 если не найдено
   */
  public Long getIdByName(String name) {
    if (res.getTopicNull().equals(name)) {
      log.info("IN TopicService.getIdByName - uncategorized topic, id = 0");
      return 0L;
    }
    Topics topic = res.getTopicsRep().findByNameIgnoreCase(name);
    if (null == topic) {
      log.info("IN TopicService.getIdByName - topic not found, name = {}, id < 0 ", name);
      return -1L;
    }
    log.info("IN TopicService.getIdByName - topic found, name = {}, id = {}", name, topic.getId());
    return topic.getId();
  }

  /**
   * Возвращение списка отсортированных тем.
   * @return список тем
   */
  public List<Topics> getAllTopic() {
    List<Topics> topics = res.getTopicsRep().filterFindAllOrderByName();
    if (null == topics) topics = new ArrayList<>();
    log.info("IN TopicService.getAllTopic - size = {}", topics.size());
    return topics;
  }

  /**
   * Возвращение списка отсортированных тем с добавлением в начало темы <Без темы>
   * @return список тем
   */
  public List<Topics> getAllTopicWithNull() {
    List<Topics> topics = new ArrayList<>();
    Topics nullTopic = new Topics();
    nullTopic.setId(0L);
    nullTopic.setName(res.getTopicNull());
    topics.add(nullTopic);
    log.info("IN TopicService.getAllTopicWithNull - added to the list: off topic");
    topics.addAll(getAllTopic());
    return topics;
  }

  /**
   * Создание и добавление новой темы.
   * @param newTopic название новой темы
   * @return true - создание и добавление прошло успешно, false - создание и добавление не произошло
   */
  public boolean addTopic(String newTopic) {
    Topics topic = res.getTopicsRep().findByNameIgnoreCase(newTopic);
    if (null != topic) {
      log.info("IN TopicService.addTopic - a topic with that name exists");
      return false;
    }
    topic = new Topics();
    topic.setName(newTopic);
    res.getTopicsRep().save(topic);
    log.info("IN TopicService.addTopic - new topic add, name = {}", newTopic);
    return true;
  }

  /**
   * Изменение название темы.
   * @param id      ключ изменяемой темы
   * @param newName новое название темы
   * @return true - изменение прошло успешно, false - изменение не произошло
   */
  public boolean updateTopic(Long id, String newName) {
    Topics topic = res.getTopicsRep().findById(id).orElse(null);
    if (null != topic) {
      if (!newName.equalsIgnoreCase(topic.getName())) {
        topic.setName(newName);
        res.getTopicsRep().save(topic);
        log.info("IN TopicService.updateTopic - the topic title was changed. id = {}, new name = {}", id, newName);
        return true;
      }
      log.info("IN TopicService.updateTopic - the topic has not been changed, the old and new names matched, id = {}", id);
    } else log.info("IN TopicService.updateTopic - topic not foud, id = {}", id);
    return false;
  }

  /**
   * Удаление темы по заданному ключу.
   * @param id ключ удаляемой темы
   * @return true - удаление прошло успешно, false - удаление не произошло
   */
  public boolean removeTopic(Long id) {
    Topics topic = res.getTopicsRep().findById(id).orElse(null);
    if (null != topic) {
      // TODO: Всем статьям с данной темой присвоить пустую тему, <Без темы>.
      res.getTopicsRep().delete(topic);
      log.info("IN TopicService.removeTopic - topic with id = {} deleted", id);
      return true;
    }
    log.info("IN TopicService.removeTopic - the topic does not exist, id = {}", id);
    return false;
  }

}
