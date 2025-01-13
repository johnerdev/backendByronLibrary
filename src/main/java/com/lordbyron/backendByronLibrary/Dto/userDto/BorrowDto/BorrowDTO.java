package com.lordbyron.backendByronLibrary.Dto.userDto.BorrowDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BorrowDTO {
    private Long id;
    private String bookTitle;
    private String userEmail;
    private String dateBorrow;
    private String dateDevolution;
    private String state;
    private String description;
}

