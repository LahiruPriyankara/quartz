package com.aeturnum.quartz.repository;

import com.aeturnum.quartz.model.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MessageRepository extends CrudRepository<Message, Integer> {
}

