package com.example.chatwebproject.utils;

import com.example.chatwebproject.model.Message;
import com.example.chatwebproject.model.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;

@Component
public class MessageTranslator {
    @Autowired
    private ModelMapper modelMapper;

}
