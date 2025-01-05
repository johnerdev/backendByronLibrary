package com.lordbyron.backendByronLibrary.services;

import com.lordbyron.backendByronLibrary.Dto.userDto.RoleAssignmentDTO;
import com.lordbyron.backendByronLibrary.Dto.userDto.UpdatePasswordDto;
import com.lordbyron.backendByronLibrary.Dto.userDto.UserDto;
import com.lordbyron.backendByronLibrary.Dto.userDto.UserStatusDto;
import com.lordbyron.backendByronLibrary.entity.Role;
import com.lordbyron.backendByronLibrary.entity.Book;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BookService {
    ResponseEntity<?> saveBook(Book book);
    List<Book> getBook(String title);
    List<Book> getbooks();
    ResponseEntity<Map<String, String>> updateBook(final Book book);
    Long countBooks();
//    Por género, idioma, año de publicación, autor,
//[Sugerir libros similares basados en género, autor o área.]

}
