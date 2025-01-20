package com.lordbyron.backendByronLibrary.repository;

import com.lordbyron.backendByronLibrary.entity.Borrow;
import com.lordbyron.backendByronLibrary.entity.Reserve;
import com.lordbyron.backendByronLibrary.entity.StateReserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReserveRepository extends JpaRepository<Reserve, Long> {

    Optional<Reserve> findById(Long id);
    @Query("SELECT r FROM Reserve r WHERE r.book.id = :bookId AND r.state = :state")
    Optional<Reserve> findByBook_IdAndState(@Param("bookId") Long bookId, @Param("state")StateReserve activa);
    List<Reserve> findByState(StateReserve state);
    @Query("SELECT r FROM Reserve r WHERE r.user.id = :idUser ORDER BY CASE WHEN r.state = 'ACTIVA' THEN 1 ELSE 2 END, r.dateReserve DESC")
    List<Reserve> findByUserId(@Param("idUser") Long idUser);
    Long countByState(StateReserve state);
}


