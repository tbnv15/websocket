package com.springsocket.springwebsocket;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MessageController
{
	@Autowired
	ChatPool chatPool;
	
	@GetMapping("/getChatPools")
	public ChatPool isOccupied ()
	{
		System.out.println("Message controller chat pool "+ chatPool);
		return chatPool;
	}
}
