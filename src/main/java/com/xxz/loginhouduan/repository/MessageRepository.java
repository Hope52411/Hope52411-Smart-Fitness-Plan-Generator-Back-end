package com.xxz.loginhouduan.repository;

import com.xxz.loginhouduan.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender = ?1 AND m.receiver = ?2) OR " +
            "(m.sender = ?2 AND m.receiver = ?1) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findMessagesBetween(String user1, String user2);
}
