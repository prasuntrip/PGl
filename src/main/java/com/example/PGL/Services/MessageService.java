package com.example.PGL.Services;

import com.example.PGL.Model.Messages;
import com.example.PGL.Repository.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepo messageRepo;
    public void saveMessage(Messages message) {
        messageRepo.save(message);
    }
    public List<Messages> getMessagesByUsername(String username) {
        return messageRepo.findByUsername(username);
    }

    public Messages getMessageById(int id) {
        return messageRepo.findById(id).orElse(null);
    }

    public void deleteMessageById(int id) {
        messageRepo.deleteById(id);
    }
}
