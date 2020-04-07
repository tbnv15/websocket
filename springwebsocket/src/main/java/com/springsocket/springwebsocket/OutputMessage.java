package com.springsocket.springwebsocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OutputMessage {
	
	private String from;
	private String text;
	private String time;
}
