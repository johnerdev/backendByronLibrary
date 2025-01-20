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
    public List<ReserveDto> getReservesByState(String state) {
        log.info("Fetching reserves with state: {}", state);

        try {
            // Validar si el estado proporcionado es válido
            StateReserve stateEnum;
            try {
                stateEnum = StateReserve.valueOf(state.toUpperCase());
            } catch (IllegalArgumentException ex) {
                log.error("Invalid state provided: {}", state);
                throw new ExceptionMessage("El estado proporcionado no es válido. Estados permitidos: ACTIVA, COMPLETADA, CANCELADA.");
            }

            // Obtener la lista de reservas según el estado
            List<Reserve> reserves = reserveRepository.findByState(stateEnum);

            // Validar si la lista está vacía
            if (reserves.isEmpty()) {
                log.warn("No reserves found with state: {}", stateEnum);
                throw new ExceptionMessage("No se encontraron reservas con el estado: " + stateEnum);
            }

            // Convertir la lista de reservas a una lista de DTOs
            List<ReserveDto> reserveDtos = reserves.stream().map(reserve -> new ReserveDto(
                    reserve.getIdReserve(),
                    reserve.getBook().getTitle(),
                    reserve.getUser().getEmail(),
                    reserve.getDateReserve().toString(),
                    reserve.getState().toString()
            )).collect(Collectors.toList());

            log.info("Total reserves found with state {}: {}", stateEnum, reserveDtos.size());
            return reserveDtos;

        } catch (ExceptionMessage ex) {
            log.error("Error fetching reserves: {}", ex.getMessage());
            throw ex; // Lanza de nuevo la excepción personalizada
        } catch (Exception ex) {
            log.error("Unexpected error fetching reserves: {}", ex.getMessage(), ex);
            throw new ExceptionMessage("Ocurrió un error inesperado al buscar reservas.");
        }
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
    @Override
    public ResponseEntity<?> cancelReserve(Long id) {
        log.info("Processing cancellation for reservation ID: {}", id);

        // Validar entrada
        if (id == null) {
            log.warn("Invalid input for reservation cancellation: Reservation ID is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El parámetro 'id' es obligatorio y no puede estar vacío."));
        }

        try {
            // Validar si la reserva existe
            Reserve reserve = reserveRepository.findById(id)
                    .orElseThrow(() -> new ExceptionMessage("Reserva no encontrada con el ID: " + id));

            // Validar el estado actual de la reserva
            if (reserve.getState() == StateReserve.CANCELADA) {
                log.warn("Attempted to cancel a reservation that is already canceled. Reservation ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "La reserva ya está cancelada."));
            }

            // Obtener el libro asociado
            Book book = reserve.getBook();
            if (book == null) {
                log.error("No book associated with reservation ID: {}", id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Error interno: No se encontró el libro asociado a la reserva."));
            }

            // Actualizar estado de la reserva y del libro
            reserve.setState(StateReserve.CANCELADA);
            book.setAvailable(true);

            // Guardar cambios en la base de datos
            reserveRepository.save(reserve);
            bookRepository.save(book);

            log.info("Reservation ID {} successfully canceled. Book ID {} marked as available.", id, book.getId());

            // Construir y devolver la respuesta
            Map<String, Object> response = Map.of(
                    "message", "Reserva cancelada con éxito!",
                    "reservationId", id,
                    "bookId", book.getId(),
                    "bookTitle", book.getTitle(),
                    "state", reserve.getState().toString()
            );
            return ResponseEntity.ok(response);

        } catch (ExceptionMessage ex) {
            log.error("Error during cancellation: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Unexpected error during cancellation: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error inesperado. Por favor, inténtelo más tarde."));
        }
    }


    @Override
    public List<ReserveDto> listReserveByUserEmail(String email) {
        log.info("Fetching reserves for user email: {}", email);

        // Validar que el correo no sea nulo o vacío
        if (email == null || email.isBlank()) {
            log.warn("Invalid input: email is null or empty");
            throw new ExceptionMessage("El correo electrónico del usuario es obligatorio y no puede estar vacío.");
        }

        try {
            // Verificar si el usuario existe
            Users user = usersRepository.findByEmail(email)
                    .orElseThrow(() -> new ExceptionMessage("Usuario no encontrado con el email: " + email));

            // Obtener las reservas del usuario
            List<Reserve> reserves = reserveRepository.findByUserId(user.getId());

            // Validar si el usuario no tiene reservas
            if (reserves.isEmpty()) {
                log.warn("No reserves found for user email: {}", email);
                throw new ExceptionMessage("No se encontraron reservas para el usuario con email: " + email);
            }

            // Convertir las reservas a DTOs para la respuesta
            List<ReserveDto> reserveDtos = reserves.stream().map(reserve -> new ReserveDto(
                    reserve.getIdReserve(),
                    reserve.getBook().getTitle(),
                    reserve.getUser().getEmail(),
                    reserve.getDateReserve().toString(),
                    reserve.getState().toString()
            )).collect(Collectors.toList());

            log.info("Found {} reserves for user email: {}", reserveDtos.size(), email);
            return reserveDtos;

        } catch (ExceptionMessage ex) {
            log.error("Error fetching reserves for user email {}: {}", email, ex.getMessage());
            throw ex; // Lanza de nuevo la excepción personalizada
        } catch (Exception ex) {
            log.error("Unexpected error fetching reserves for user email {}: {}", email, ex.getMessage(), ex);
            throw new ExceptionMessage("Ocurrió un error inesperado al buscar las reservas para el usuario.");
        }
    }




    @Override
    public Long countReservesActives() {
        log.info("Counting active reserves");

        try {
            // Usar el valor del enum directamente en lugar de convertir un string
            StateReserve stateActive = StateReserve.ACTIVA;

            // Consultar las reservas activas y contarlas
            Long activeReservesCount = reserveRepository.countByState(stateActive);

            log.info("Total active reserves: {}", activeReservesCount);
            return activeReservesCount;

        } catch (Exception ex) {
            log.error("Unexpected error counting active reserves: {}", ex.getMessage(), ex);
            throw new ExceptionMessage("Ocurrió un error inesperado al contar las reservas activas.");
        }
    }

}
