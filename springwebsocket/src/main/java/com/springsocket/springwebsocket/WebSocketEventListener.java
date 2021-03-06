package com.springsocket.springwebsocket;

import static java.lang.String.format;

import com.springsocket.springwebsocket.ChatMessage.MessageType;
import java.util.Date;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

  @Autowired
  private SimpMessageSendingOperations messagingTemplate;
  
  @Resource
  private ChatPool chatPool;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    logger.info("Received a new web socket connection.");
    //logger.info(event.getUser().getName());
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

    String username = (String) headerAccessor.getSessionAttributes().get("username");
    String roomId = (String) headerAccessor.getSessionAttributes().get("room_id");
    for(int i =0; i < chatPool.getRoom().size(); i ++)
    {
      if(chatPool.getRoom().get(i).getRoomName().equals(roomId))
      {
        chatPool.getRoom().get(i).setIsOccupied("N");
        chatPool.getRoom().get(i).setParticipantCount(chatPool.getRoom().get(i).getParticipantCount()-1);
        logger.info("After Room changed ->"+chatPool);
        break;
      }
    }
    if (username != null) {
      logger.info("User Disconnected: " + username);

      ChatMessage chatMessage = new ChatMessage();
      chatMessage.setType(MessageType.LEAVE);
      chatMessage.setSender(username);

      messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
    }
  }
}
