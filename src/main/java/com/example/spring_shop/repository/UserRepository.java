package com.example.spring_shop.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.spring_shop.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findFirstByEmail(String email);
    Optional<User> deleteFirstByEmail(String email);
}
