package dev.mm.core.coreservice.service;

import dev.mm.core.coreservice.constants.WebsocketPayloadType;
import dev.mm.core.coreservice.dto.chat.ChatMessageDto;
import dev.mm.core.coreservice.dto.websocket.WsPayloadDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

import static dev.mm.core.coreservice.constants.WebsocketDestinations.EVENTS_STREAM;

@Service
public class WebsocketService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void sendNewChatMessageToUsers(Collection<String> usernames, ChatMessageDto chatMessageDto) {
        WsPayloadDto wsPayloadDto = new WsPayloadDto(WebsocketPayloadType.NEW_CHAT_MESSAGE, chatMessageDto);
        usernames.forEach(username -> simpMessagingTemplate.convertAndSendToUser(username, EVENTS_STREAM, wsPayloadDto));
    }

}
