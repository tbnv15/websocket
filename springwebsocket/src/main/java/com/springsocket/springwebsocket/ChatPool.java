package com.springsocket.springwebsocket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;


@Data
@Component
@ApplicationScope
@JsonIgnoreProperties({"targetClass", "targetSource", "targetObject", "advisors", "frozen", "exposeProxy", "preFiltered", "proxiedInterfaces", "proxyTargetClass"})
public class ChatPool
{
	private List<Room> room;
}

