package dev.mm.core.coreservice.validator;

import dev.mm.core.coreservice.dto.chat.CreateChatDto;
import dev.mm.core.coreservice.exception.ValidationErrorException;
import dev.mm.core.coreservice.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static dev.mm.core.coreservice.util.TranslationUtil.translate;

@Service
public class ChatValidator {

    @Autowired
    private UserRoleRepository userRoleRepository;

    public void validateCreateChat(long organizationId, CreateChatDto createChatDto) {

        if (createChatDto.getUserIds() == null || createChatDto.getUserIds().isEmpty()) {
            throw new ValidationErrorException("userIds", translate("User ids must not be empty or null"));
        }

        int count = userRoleRepository.countDistinctUsersInOrgFromSet(organizationId, createChatDto.getUserIds());

        if (count != createChatDto.getUserIds().size()) {
            throw new ValidationErrorException("userIds", translate("Some of users are not part of organization"));
        }
    }
}
