package com.lordbyron.backendByronLibrary.repository;

import com.lordbyron.backendByronLibrary.entity.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsersRepository extends CrudRepository<Users,Long> {
    Users findByEmailAndPassword(String email, String password);
    Optional<Users> findByEmail(String email);

}
