package com.lordbyron.backendByronLibrary.controller;

import com.lordbyron.backendByronLibrary.Dto.userDto.BorrowDto.BorrowDTO;
import com.lordbyron.backendByronLibrary.entity.Book;
import com.lordbyron.backendByronLibrary.entity.Borrow;
import com.lordbyron.backendByronLibrary.exception.ExceptionMessage;
import com.lordbyron.backendByronLibrary.services.BorrowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
/*@CrossOrigin("*")*/
@RequestMapping("/borrow")

public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBorrows() {
        try {
            List<BorrowDTO> borrow = borrowService.getBorrows();
            return ResponseEntity.ok(borrow);
        } catch (ExceptionMessage e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/add/{id}/{email}")
    public ResponseEntity<?> createBorrow(@PathVariable Long id, @PathVariable String email) {
        log.info("Received request to create borrow. Book ID: {}, User Email: {}", id, email);

        // Validar entradas
        if (id == null || email == null || email.isBlank()) {
            log.warn("Invalid request parameters. ID: {}, Email: {}", id, email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Los parámetros 'id' y 'email' son obligatorios y no pueden estar vacíos."));
        }

        try {
            // Llamar al servicio para guardar el préstamo
            return borrowService.saveBorrow(id, email);
        } catch (ExceptionMessage ex) {
            log.error("Error creating borrow: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Unexpected error creating borrow: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error inesperado. Por favor intente más tarde."));
        }
    }

}
