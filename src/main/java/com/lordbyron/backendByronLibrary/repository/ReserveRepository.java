package com.lordbyron.backendByronLibrary.repository;

import com.lordbyron.backendByronLibrary.entity.Reserve;
import com.lordbyron.backendByronLibrary.entity.StateReserve;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReserveRepository extends JpaRepository<Reserve, Long> {
    List<Reserve> findByUserId(Long idUser);
    List<Reserve> findByState(StateReserve state);
}


