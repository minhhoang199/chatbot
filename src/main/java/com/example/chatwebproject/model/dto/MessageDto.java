package com.example.chatwebproject.model.dto;

import com.example.chatwebproject.model.entity.BaseEntity;
import com.example.chatwebproject.model.enums.MessageStatus;
import com.example.chatwebproject.model.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MessageDto extends BaseEntity {

    private Long id;
    private String sender;
    @NotNull
    private Long senderId;
    @NotBlank
    private String content;

    private MessageType type;

    private MessageStatus messageStatus;
    @Min(1)
    private Long roomId;
    //reply
    private Long replyId;
    private Boolean isReply = false;
    private String replyContent;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime replyCreatedDate;
    private List<EmojiDto> emoji;

    //attachedFile
    private AttachedFileDto attachedFile;
}
