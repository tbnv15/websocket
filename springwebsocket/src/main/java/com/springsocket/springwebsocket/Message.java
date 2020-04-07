package com.springsocket.springwebsocket;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Message {
 
    private String from;
    private String text;
 
}
