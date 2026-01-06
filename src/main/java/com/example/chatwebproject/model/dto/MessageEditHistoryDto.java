package com.example.chatwebproject.model.dto;

import com.example.chatwebproject.model.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageEditHistoryDto extends BaseEntity {
    private Long id;

    private String content;

    private Long messageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageEditHistoryDto)) return false;
        MessageEditHistoryDto room = (MessageEditHistoryDto) o;
        return Objects.equals(getId(), room.getId());
    }
}
