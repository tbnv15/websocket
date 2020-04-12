package com.springsocket.springwebsocket;

import static java.lang.String.format;

import com.springsocket.springwebsocket.ChatMessage.MessageType;
import java.util.Date;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@CrossOrigin(origins = "http://homedepotchatbot-vtdffy.appspot.com/")
public class ChatController
{

	private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
	public static final int MAX_PARTICIPANT_COUNT = 2;

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;
	
	@Resource
	private ChatPool chatPool;
	
	@MessageMapping("/chat/{roomId}/sendMessage")
	public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage)
	{
		messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
	}
	
	@MessageMapping("/chat/{roomId}/addUser")
	public String addUser(@DestinationVariable String roomId, @Payload ChatMessage chatMessage,
			SimpMessageHeaderAccessor headerAccessor)
	{
		String currentRoomId = (String) headerAccessor.getSessionAttributes().put("room_id", roomId);
		// itreate chatPool room list till first isOccupied= N then set isOccupied flat to Y &ncoocupoied on date  if no object found in taht pool Return ro Room available ,
		logger.info("The chat pool data ->{}\n room name is ->{}" , chatPool, roomId);
		for(int i =0; i < chatPool.getRoom().size(); i ++)
		{
			if(chatPool.getRoom().get(i).getRoomName().equals(roomId))
			{
				if(chatPool.getRoom().get(i).getIsOccupied().equals("Y") )
				{
					logger.info("Room Occupied");
					return "OCCUPIED";
				}
				if( chatPool.getRoom().get(i).getParticipantCount() < MAX_PARTICIPANT_COUNT)
				{
					chatPool.getRoom().get(i).setParticipantCount(chatPool.getRoom().get(i).getParticipantCount()+1);
					if( chatPool.getRoom().get(i).getParticipantCount() == MAX_PARTICIPANT_COUNT)
					{
						chatPool.getRoom().get(i).setIsOccupied("Y");
						chatPool.getRoom().get(i).setOccupiedOn(new Date());
						logger.info("After Room changed ->" + chatPool);
					}
					break;
				}
			}
		}
		if (currentRoomId != null)
		{
			ChatMessage leaveMessage = new ChatMessage();
			leaveMessage.setType(MessageType.LEAVE);
			leaveMessage.setSender(chatMessage.getSender());
			messagingTemplate.convertAndSend(format("/channel/%s", currentRoomId), leaveMessage);
		}
		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
		return currentRoomId;
	}
	
	@RequestMapping
	public String getPage()
	{
		return "index";
	}
}
