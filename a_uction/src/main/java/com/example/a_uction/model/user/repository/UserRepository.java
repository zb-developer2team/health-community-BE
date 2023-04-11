package com.example.a_uction.model.user.repository;

import com.example.a_uction.model.user.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByUserEmail(String email);
	Optional<UserEntity> findByPhoneNumber(String phoneNumber);
	boolean existsByUserEmail(String email);
}
