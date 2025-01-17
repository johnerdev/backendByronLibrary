package com.lordbyron.backendByronLibrary.services;

import com.lordbyron.backendByronLibrary.Dto.userDto.BorrowDto.BorrowDTO;
import com.lordbyron.backendByronLibrary.Dto.userDto.ReserveDto;
import com.lordbyron.backendByronLibrary.entity.*;
import com.lordbyron.backendByronLibrary.exception.ExceptionMessage;
import com.lordbyron.backendByronLibrary.repository.BookRepository;
import com.lordbyron.backendByronLibrary.repository.ReserveRepository;
import com.lordbyron.backendByronLibrary.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ReserveServiceImpl implements ReserveService{
    private static final Logger log = LoggerFactory.getLogger(UsersServiceImpl.class);

    @Autowired
    private ReserveRepository reserveRepository;

    @Autowired
    private BookRepository bookRepository;
    private UsersRepository usersRepository;

    @Override
    public List<ReserveDto> getReserves() {
        log.info("Fetching all reserves");

        // Obtener la lista de préstamos
        List<Reserve> reserves = reserveRepository.findAll();

        // Validar si la lista está vacía
        if (reserves.isEmpty()) {
            log.warn("No reserve found in the database");
            throw new ExceptionMessage("No se encontraron reservas registradas");
        }

        // Convertir la lista de préstamos a una lista de DTOs
        List<ReserveDto> reserveDtos = reserves.stream().map(nreserve -> new ReserveDto(
                nreserve.getIdReserve(),
                nreserve.getBook().getTitle(),
                nreserve.getUser().getEmail(),
                nreserve.getDateReserve().toString(),
                nreserve.getState().toString()
        )).collect(Collectors.toList());

        log.info("Total borrows found: {}", reserveDtos.size());
        return reserveDtos;
    }

    @Override
    public ResponseEntity<?> makeReserve(Long id, String email) {
        log.info("Processing reservation for user email: {} and book ID: {}", email, id);

        // Validar entradas
        if (id == null || email == null || email.isBlank()) {
            log.warn("Invalid inputs for reservation: Book ID: {}, Email: {}", id, email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Los parámetros 'id' y 'email' son obligatorios y no pueden estar vacíos."));
        }

        try {
            // Validar si el usuario existe
            Users user = usersRepository.findByEmail(email)
                    .orElseThrow(() -> new ExceptionMessage("Usuario no encontrado con el email: " + email));

            // Validar si el libro existe
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new ExceptionMessage("Libro no encontrado con el ID: " + id));

            // Validar disponibilidad del libro
            if (!book.getAvailable()) {
                log.warn("Attempted reservation for unavailable book ID: {}", book.getId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El libro no está disponible para reservar."));
            }

            // Crear y configurar la reserva
            Reserve newReserve = new Reserve();
            newReserve.setBook(book);
            newReserve.setUser(user);
            newReserve.setDateReserve(LocalDate.now());
            newReserve.setState(StateReserve.ACTIVA);

            // Actualizar estado del libro y guardar entidades
            book.setAvailable(false);
            bookRepository.save(book);
            reserveRepository.save(newReserve);

            log.info("Reservation created successfully for user email: {} and book ID: {}", email, id);

            // Construir y devolver la respuesta
            Map<String, Object> response = Map.of(
                    "message", "Reserva creada con éxito!"
            );
            return ResponseEntity.ok(response);

        } catch (ExceptionMessage ex) {
            log.error("Error during reservation: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Unexpected error during reservation: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error inesperado. Por favor, inténtelo más tarde."));
        }
    }



    public void cancelReserve(Long id) {
        Reserve reserve = reserveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        reserve.setState(StateReserve.CANCELADA);
        reserveRepository.save(reserve);
    }

    public List<Reserve> listReserveBYUser(Long idUser) {
        return reserveRepository.findByUserId(idUser);
    }
}
