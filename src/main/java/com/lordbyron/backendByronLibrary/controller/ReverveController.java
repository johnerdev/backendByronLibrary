package com.lordbyron.backendByronLibrary.controller;

import com.lordbyron.backendByronLibrary.Dto.userDto.BorrowDto.BorrowDTO;
import com.lordbyron.backendByronLibrary.Dto.userDto.ReserveDto;
import com.lordbyron.backendByronLibrary.entity.Reserve;
import com.lordbyron.backendByronLibrary.exception.ExceptionMessage;
import com.lordbyron.backendByronLibrary.services.BorrowService;
import com.lordbyron.backendByronLibrary.services.ReserveService;
import com.lordbyron.backendByronLibrary.services.UsersServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;



@RestController
@Slf4j
/*@CrossOrigin("*")*/
@RequestMapping("/reserve")
public class ReverveController {

    private final ReserveService reserveService;

    public ReverveController(ReserveService reserveService) {
        this.reserveService = reserveService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllReserves() {
        try {
            List<ReserveDto> reserve = reserveService.getReserves();
            return ResponseEntity.ok(reserve);
        } catch (ExceptionMessage e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/all/{state}")
    public ResponseEntity<?> getAllReservesByState(@PathVariable String state) {
        try {
            List<ReserveDto> reserve = reserveService.getReservesByState(state);
            return ResponseEntity.ok(reserve);
        } catch (ExceptionMessage e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/add/{bookId}/{email}")
    public ResponseEntity<?> createReservation(
            @PathVariable Long bookId,
            @PathVariable String email
    ) {
        log.info("Received request to create reservation for book ID: {} and user email: {}", bookId, email);
        try {
            return reserveService.makeReserve(bookId, email);
        } catch (ExceptionMessage ex) {
            log.error("Error creating reservation: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Unexpected error creating reservation: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error inesperado. Por favor, inténtelo más tarde."));
        }
    }
    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        log.info("Received request to cancel reservation with ID: {}", id);
        try {
            return reserveService.cancelReserve(id);
        } catch (ExceptionMessage ex) {
            log.error("Error canceling reservation: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Unexpected error canceling reservation: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error inesperado. Por favor, inténtelo más tarde."));
        }
    }
    @GetMapping("/count")
    public ResponseEntity<?> countActiveReserves() {
        log.info("Received request to count active reserves");

        try {
            Long activeReservesCount = reserveService.countReservesActives();
            return ResponseEntity.ok(Map.of("activeReserves", activeReservesCount));
        } catch (ExceptionMessage ex) {
            log.error("Error counting active reserves: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Unexpected error counting active reserves: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error inesperado. Por favor, inténtelo más tarde."));
        }
    }
    @GetMapping("/find/{email}")
    public ResponseEntity<?> getReservesByUserEmail(@PathVariable String email) {
        log.info("Received request to fetch reserves for user email: {}", email);

        try {
            List<ReserveDto> reserves = reserveService.listReserveByUserEmail(email);
            return ResponseEntity.ok(reserves);
        } catch (ExceptionMessage ex) {
            log.error("Error fetching reserves for user email {}: {}", email, ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Unexpected error fetching reserves for user email {}: {}", email, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error inesperado. Por favor, inténtelo más tarde."));
        }
    }

}
