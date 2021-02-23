package dev.mm.core.coreservice.dto.chat;

import dev.mm.core.coreservice.dto.entity.IdAndTimestampsDto;
import dev.mm.core.coreservice.dto.user.UserDto;
import dev.mm.core.coreservice.model.Chat;
import dev.mm.core.coreservice.model.ChatMembership;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMembershipDto extends IdAndTimestampsDto {

    private Long userId;
    private Long chatId;
    private boolean admin;
    private UserDto userDto;

    public ChatMembershipDto(ChatMembership chatMembership) {
        super(chatMembership);
        userId = chatMembership.getUserId();
        chatId = chatMembership.getChatId();
        admin = chatMembership.isAdmin();
        if (chatMembership.getUser() != null) {
            userDto = new UserDto(chatMembership.getUser(), false);
        }
    }
}
