package dev.mm.core.coreservice.dto.chat;

import dev.mm.core.coreservice.dto.request.PageRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatPageRequestDto extends PageRequestDto {
    private String name;
}
