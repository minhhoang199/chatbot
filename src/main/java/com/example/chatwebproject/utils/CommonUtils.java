package com.example.chatwebproject.utils;

import com.example.chatwebproject.model.dto.EmojiDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
public class CommonUtils {
    public static String getFileExtension(MultipartFile file){
        if(!file.isEmpty())
        {
            String fileName = file.getOriginalFilename();
            if (StringUtils.isNotEmpty(fileName)){
                return fileName.substring(fileName.lastIndexOf(".") + 1);
            }
        }
        return null;
    }

    public static List<EmojiDto> convertStringToEmojiObject(String emojiString) {
        try {
            if (StringUtils.isBlank(emojiString)) return null;
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(emojiString, new TypeReference<List<EmojiDto>>() {});
        } catch (Exception e) {
            log.error("convertStringToEmojiObject failed " + e);
            return null;
        }
    }
}
