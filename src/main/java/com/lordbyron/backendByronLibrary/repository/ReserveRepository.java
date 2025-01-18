package com.lordbyron.backendByronLibrary.repository;

import com.lordbyron.backendByronLibrary.entity.Reserve;
import com.lordbyron.backendByronLibrary.entity.StateReserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReserveRepository extends JpaRepository<Reserve, Long> {
    List<Reserve> findByUserId(Long idUser);
    Optional<Reserve> findById(Long id);
    @Query("SELECT r FROM Reserve r WHERE r.book.id = :bookId AND r.state = :state")
    Optional<Reserve> findByBook_IdAndState(@Param("bookId") Long bookId, @Param("state")StateReserve activa);
    List<Reserve> findByState(StateReserve state);
}


