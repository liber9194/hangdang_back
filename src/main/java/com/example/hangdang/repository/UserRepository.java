package com.example.hangdang.repository;

import com.example.hangdang.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    //Optional<UserEntity> findByLoginId(String loginId);
}
