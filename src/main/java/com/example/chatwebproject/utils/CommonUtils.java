package com.example.chatwebproject.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
}
