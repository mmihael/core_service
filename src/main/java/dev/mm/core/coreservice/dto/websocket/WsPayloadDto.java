package dev.mm.core.coreservice.dto.websocket;

import dev.mm.core.coreservice.constants.WebsocketPayloadType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WsPayloadDto {
    private WebsocketPayloadType type;
    private Object payload;
}
