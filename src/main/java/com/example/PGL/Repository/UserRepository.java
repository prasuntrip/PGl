package com.example.PGL.Repository;

import com.example.PGL.Model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {
    boolean existsByUsername(String username);
    Optional<UserModel> findByUsername(String username);
}
