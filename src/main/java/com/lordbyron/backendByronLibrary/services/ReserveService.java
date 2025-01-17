package com.lordbyron.backendByronLibrary.services;

import com.lordbyron.backendByronLibrary.Dto.userDto.BorrowDto.BorrowDTO;
import com.lordbyron.backendByronLibrary.Dto.userDto.ReserveDto;
import com.lordbyron.backendByronLibrary.entity.Reserve;
import org.springframework.http.ResponseEntity;


import java.util.List;


public interface ReserveService {
    List<ReserveDto> getReserves();
    ResponseEntity<?> makeReserve(Long id, String email);
    void cancelReserve(Long id);
    List<Reserve> listReserveBYUser(Long idUser);
}
