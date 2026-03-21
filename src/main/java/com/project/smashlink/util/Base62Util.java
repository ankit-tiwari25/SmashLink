package com.project.smashlink.util;


import org.springframework.stereotype.Component;

@Component
public class Base62Util {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = 62;
    private static final int CODE_LENGTH = 7;

    public String encode(Long id){
        StringBuilder sb = new StringBuilder();
        while(id > 0){
            sb.append(CHARACTERS.charAt((int) (id % BASE)));
            id = id / BASE;
        }

        // Padding to CODE_LENGTH with 'x'

        while(sb.length() < CODE_LENGTH){
            sb.append('x');
        }
        return sb.reverse().toString();
    }
}
