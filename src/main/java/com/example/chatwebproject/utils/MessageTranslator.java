package com.example.chatwebproject.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;

@Component
public class MessageTranslator {
    @Autowired
    private ModelMapper modelMapper;

}
