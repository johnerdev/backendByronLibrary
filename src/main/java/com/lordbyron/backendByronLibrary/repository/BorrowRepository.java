package com.lordbyron.backendByronLibrary.repository;

import com.lordbyron.backendByronLibrary.entity.Borrow;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BorrowRepository extends CrudRepository<Borrow, Long> {
    Optional<Borrow> findById(Long id);
}
