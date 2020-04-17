package dev.mm.core.coreservice.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PageRequestDto {

    private Long page;
    private Long size;

}
