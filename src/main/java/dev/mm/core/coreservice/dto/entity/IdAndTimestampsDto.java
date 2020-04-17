package dev.mm.core.coreservice.dto.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.mm.core.coreservice.model.BaseIdAndTimestamps;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class IdAndTimestampsDto {

    private long id;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Timestamp createdAt;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Timestamp updatedAt;

    public IdAndTimestampsDto(BaseIdAndTimestamps baseIdAndTimestamps) {
        this.id = baseIdAndTimestamps.getId();
        this.createdAt = baseIdAndTimestamps.getCreatedAt();
        this.updatedAt = baseIdAndTimestamps.getUpdatedAt();
    }

}
