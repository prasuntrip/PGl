package com.example.PGL.Repository;

import com.example.PGL.Model.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Messages, Integer> {
    List<Messages> findByUsername(String username);
}
