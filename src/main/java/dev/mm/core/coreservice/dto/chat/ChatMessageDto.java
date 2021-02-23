package dev.mm.core.coreservice.dto.chat;

import dev.mm.core.coreservice.dto.entity.IdAndTimestampsDto;
import dev.mm.core.coreservice.dto.user.UserDto;
import dev.mm.core.coreservice.model.Chat;
import dev.mm.core.coreservice.model.ChatMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ChatMessageDto extends IdAndTimestampsDto {

    private String message;
    private Long userId;
    private Long chatId;
    private ChatDto chat;
    private UserDto user;

    public static ChatMessageDto withUser(ChatMessage chatMessage) {
        ChatMessageDto chatMessageDto = new ChatMessageDto(chatMessage);
        if (chatMessage.getUser() != null) {
            chatMessageDto.setUser(new UserDto(chatMessage.getUser(), false));
        }
        return chatMessageDto;
    }

    public ChatMessageDto(ChatMessage chatMessage) {
        this(chatMessage, false);
    }

    public ChatMessageDto(ChatMessage chatMessage, boolean includeRelations) {
        super(chatMessage);
        message = chatMessage.getMessage();
        userId = chatMessage.getUserId();
        chatId = chatMessage.getChatId();
        if (includeRelations) {
            chat = chatMessage.getChat() != null ? new ChatDto(chatMessage.getChat(), false) : null;
            user = chatMessage.getUser() != null ? new UserDto(chatMessage.getUser(), false) : null;
        }
    }
}
