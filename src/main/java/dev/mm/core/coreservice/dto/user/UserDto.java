package dev.mm.core.coreservice.dto.user;

import dev.mm.core.coreservice.dto.entity.IdAndTimestampsDto;
import dev.mm.core.coreservice.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto extends IdAndTimestampsDto {

    private String username;

    public UserDto(User user) {
        super(user);
        this.username = user.getUsername();
    }

}