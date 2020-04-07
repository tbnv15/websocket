package com.springsocket.springwebsocket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Date;
import lombok.Data;


@Data
public class Room
{
	private String isOccupied;
	private String roomName;
	private Date occupiedOn;
	private String assoicateName;
	private int participantCount;
}
