package com.gotcoder.Myhome.repository;

import com.gotcoder.Myhome.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
