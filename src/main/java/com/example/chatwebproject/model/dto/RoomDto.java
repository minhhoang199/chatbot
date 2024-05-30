package com.example.chatwebproject.model.dto;

import com.example.chatwebproject.model.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Long id;
    @NotBlank(message = "Name may not be blank")
    private String name;
    @NotEmpty(message = "Phones list may not be empty")
    @Size(min = 2, message = "Number of usernames must be higher than 1")
    private List<String> usernames;
    //@Pattern(regexp = "^0\\d{9}$|^84\\d{9}$", message = "Invalid invitor phone")
    @NotBlank(message = "RoomType may not be blank")
    private RoomType roomType;
}
