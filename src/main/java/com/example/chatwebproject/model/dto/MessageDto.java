package com.example.chatwebproject.model.dto;

import com.example.chatwebproject.model.entity.BaseEntity;
import com.example.chatwebproject.model.enums.MessageStatus;
import com.example.chatwebproject.model.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
    @NotBlank
    private Long senderId;
    @NotBlank
    private String content;
    @NotBlank
    private MessageType type;
    @NotBlank
    private MessageStatus messageStatus;
    @Size(min = 1)
    private Long roomId;
    //reply
    private Long replyId;
    private Boolean isReply = false;
    private String replyContent;

    //attachedFile
    private Set<AttachedFileDto> attachedFiles;
}
