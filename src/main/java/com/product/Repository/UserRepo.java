package com.product.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.product.Entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long>{
	
	Optional<User> findByMail(String mail);
}
