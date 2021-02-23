package dev.mm.core.coreservice.dto.chat;

import dev.mm.core.coreservice.dto.entity.IdAndTimestampsDto;
import dev.mm.core.coreservice.model.Chat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ChatDto extends IdAndTimestampsDto {

    private String name;
    private Long creatorId;
    private Long organizationId;
    private List<ChatMembershipDto> chatMemberships;

    public ChatDto(Chat chat) {
        this(chat, false);
    }
    public ChatDto(Chat chat, boolean includeRelations) {
        super(chat);
        name = chat.getName();
        creatorId = chat.getCreatorId();
        organizationId = chat.getOrganizationId();
        if (includeRelations) {
            chatMemberships = chat.getChatMemberships().stream().map(ChatMembershipDto::new).collect(Collectors.toList());
        }
    }
}
