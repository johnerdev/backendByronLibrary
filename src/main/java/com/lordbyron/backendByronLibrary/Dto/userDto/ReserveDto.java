package com.lordbyron.backendByronLibrary.Dto.userDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReserveDto {
    private Long id;
    private String bookTitle;
    private String userEmail;
    private String dateReserve;
    private String state;

}
