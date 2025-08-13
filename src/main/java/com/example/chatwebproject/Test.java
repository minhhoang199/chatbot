package com.example.chatwebproject;

import com.example.chatwebproject.model.dto.EmojiDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper ob = new ObjectMapper();
        List<EmojiDto> list = new ArrayList<>();
        list.add(new EmojiDto(1, "name1", "❤"));
        list.add(new EmojiDto(2, "name2", "❤"));
        String json = ob.writeValueAsString(list);
        System.out.println(json);

        List<EmojiDto> l2 = ob.readValue(json, new TypeReference<List<EmojiDto>>() {});
        System.out.println(l2);
    }
}
