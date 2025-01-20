package com.lordbyron.backendByronLibrary.services;

import com.lordbyron.backendByronLibrary.Dto.userDto.BorrowDto.BorrowDTO;
import com.lordbyron.backendByronLibrary.Dto.userDto.ReserveDto;
import com.lordbyron.backendByronLibrary.entity.Reserve;
import org.springframework.http.ResponseEntity;


import java.util.List;


public interface ReserveService {
    List<ReserveDto> getReserves();
    List<ReserveDto> getReservesByState(String state);
    ResponseEntity<?> makeReserve(Long id, String email);
    ResponseEntity<?> cancelReserve(Long id);
     List<ReserveDto> listReserveByUserEmail(String email);
    Long countReservesActives();
}
