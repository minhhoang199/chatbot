package com.example.chatwebproject.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeUserListRequest {
    @NotEmpty
    private List<String> emails;
    @NotNull
    private Long roomId;
}
