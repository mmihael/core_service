package dev.mm.core.coreservice.dto.file;

import dev.mm.core.coreservice.dto.request.PageRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilePageRequestDto extends PageRequestDto {

    private String originalName;
    private String fileType;

}
