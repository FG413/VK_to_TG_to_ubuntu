package com.example.nlmessagebot.repository;

import com.example.nlmessagebot.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
