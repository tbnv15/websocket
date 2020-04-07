package com.springsocket.springwebsocket;

import java.util.ArrayList;
import javax.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SpringWebsocketApplication implements ApplicationRunner
{
	@Resource
	ChatPool chatPool;
	
	public static void main(String[] args)
	{
		SpringApplication.run(SpringWebsocketApplication.class, args);
	}
	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		System.out.println("Hello World from Application Runner");
		Room room1 = new Room();
		room1.setIsOccupied("N");
		room1.setAssoicateName("Associate1");
		room1.setRoomName("Room1");
		
		Room room2 = new Room();
		room2.setIsOccupied("N");
		room2.setAssoicateName("Associate2");
		room2.setRoomName("Room2");
		
		Room room3 = new Room();
		room3.setIsOccupied("N");
		room3.setAssoicateName("Associate3");
		room3.setRoomName("Room3");
		
		Room room4 = new Room();
		room4.setIsOccupied("N");
		room4.setAssoicateName("Associate4");
		room4.setRoomName("Room4");
		
		Room room5 = new Room();
		room5.setIsOccupied("N");
		room5.setAssoicateName("Associate3");
		room5.setRoomName("Room5");
		
		if(chatPool.getRoom()==null){
			chatPool.setRoom(new ArrayList<>()) ;
		}
		chatPool.getRoom().add(room1);
		chatPool.getRoom().add(room2);
		chatPool.getRoom().add(room3);
		chatPool.getRoom().add(room4);
		chatPool.getRoom().add(room5);
	}
	
}
