package dev.mm.core.coreservice.dto.file;

import dev.mm.core.coreservice.constants.FileAccessType;
import dev.mm.core.coreservice.dto.entity.IdAndTimestampsDto;
import dev.mm.core.coreservice.model.File;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDto extends IdAndTimestampsDto {

    private String uuid;
    private String url;
    private String originalName;
    private String fileType;
    private FileAccessType accessType;
    private long size;
    private Long width;
    private Long height;
    private Long length;
    private Long ownerId;
    private Long organizationId;

    public FileDto(File file) {
        super(file);
        uuid = file.getUuid();
        url = file.getUrl();
        originalName = file.getOriginalName();
        fileType = file.getFileType();
        accessType = file.getAccessType();
        size = file.getSize();
        width = file.getWidth();
        height = file.getHeight();
        length = file.getLength();
        ownerId = file.getOwnerId();
        organizationId = file.getOrganizationId();
    }
}
