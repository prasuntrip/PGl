package com.example.PGL.Services;

import com.example.PGL.Model.UserModel;
import com.example.PGL.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class IDService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    private Random rand = new Random();

    public int sendEmail(String email){
        int randInt = rand.nextInt(100000,999999);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP Verification!");
        message.setText("OTP VERIFICATION :- "+randInt);
        mailSender.send(message);
        return randInt;
    }

    public void save(UserModel user){
        userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    public UserModel findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);  // or throw exception if preferred
    }
}
