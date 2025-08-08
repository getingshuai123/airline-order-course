package com.postion.airlineorderbackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.postion.airlineorderbackend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 获取用户信息
    Optional<User> findByUsername(String username);

}
