package com.transformers.usersmanagementsystem.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.transformers.usersmanagementsystem.entity.Users;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Integer> {

    Optional<Users> findByEmail(String email);
}
