package dev.mm.core.coreservice.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageResponseDto {
    private List content;
    private boolean first;
    private boolean last;
    private Long totalPages;
    private Long totalElements;
    private Long size;
    private Long number;
    private Long numberOfElements;
    private Object metadata;
}
