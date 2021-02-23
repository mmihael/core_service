package dev.mm.core.coreservice.dto.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CreateChatDto {
    private String name;
    private Set<Long> userIds;
}
