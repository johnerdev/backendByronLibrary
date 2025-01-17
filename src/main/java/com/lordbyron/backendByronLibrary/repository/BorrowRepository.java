package com.lordbyron.backendByronLibrary.repository;

import com.lordbyron.backendByronLibrary.entity.Borrow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BorrowRepository extends CrudRepository<Borrow, Long> {
//    Optional<Borrow> findByEmail(String email);
    @Query("SELECT b FROM Borrow b WHERE b.user.id = :userId")
    List<Borrow> findByUserId(@Param("userId") Long userId);


}
