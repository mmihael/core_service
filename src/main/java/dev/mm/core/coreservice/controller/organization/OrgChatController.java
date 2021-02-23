package dev.mm.core.coreservice.controller.organization;

import dev.mm.core.coreservice.dto.chat.ChatDto;
import dev.mm.core.coreservice.dto.chat.ChatMessagesPageRequestDto;
import dev.mm.core.coreservice.dto.chat.ChatPageRequestDto;
import dev.mm.core.coreservice.dto.chat.CreateChatDto;
import dev.mm.core.coreservice.dto.chat.CreateChatMessageDto;
import dev.mm.core.coreservice.dto.request.PageRequestDto;
import dev.mm.core.coreservice.dto.user.UserPageRequestDto;
import dev.mm.core.coreservice.security.UserDetailsImpl;
import dev.mm.core.coreservice.service.AuthorizationService;
import dev.mm.core.coreservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static dev.mm.core.coreservice.constants.Uris.API_ORGANIZATION_CHAT;
import static dev.mm.core.coreservice.constants.Uris.API_ORGANIZATION_CHAT_ID;
import static dev.mm.core.coreservice.constants.Uris.API_ORGANIZATION_CHAT_ID_CHAT_MESSAGE;

@RestController
public class OrgChatController {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ChatService chatService;

    @GetMapping(API_ORGANIZATION_CHAT)
    public ResponseEntity getChatsPage(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable long organizationId,
        ChatPageRequestDto chatPageRequestDto
    ) {
        authorizationService.ensureUserIsOrganizationOwnerOrMember(organizationId, userDetails.getId());
        return ResponseEntity.ok(chatService.getPage(organizationId, userDetails.getId(), chatPageRequestDto));
    }


    @GetMapping(API_ORGANIZATION_CHAT_ID)
    public ResponseEntity getChatById(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable long organizationId,
        @PathVariable long chatId
    ) {
        authorizationService.ensureUserIsOrganizationOwnerOrMember(organizationId, userDetails.getId());
        chatService.ensureUserIsPartOfChat(chatId, userDetails.getId());
        return ResponseEntity.ok(
            chatService.getChatDtoByIdAndOrganizationIdFetchMembershipsOrThrow(organizationId, chatId)
        );
    }

    @PostMapping(API_ORGANIZATION_CHAT)
    public ResponseEntity createChat(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable long organizationId,
        @RequestBody CreateChatDto createChatDto
    ) {
        authorizationService.ensureUserIsOrganizationOwnerOrMember(organizationId, userDetails.getId());
        return ResponseEntity.ok(new ChatDto(
            chatService.validateAndCreateChatInOrganization(organizationId, userDetails.getId(), createChatDto)
        ));
    }

    @GetMapping(API_ORGANIZATION_CHAT_ID_CHAT_MESSAGE)
    public ResponseEntity getChatMessagesPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable long organizationId,
            @PathVariable long chatId,
            ChatMessagesPageRequestDto chatMessagesPageRequestDto
    ) {
        authorizationService.ensureUserIsOrganizationOwnerOrMember(organizationId, userDetails.getId());
        chatService.ensureUserIsPartOfChat(chatId, userDetails.getId());
        return ResponseEntity.ok(
            chatService.getChatMessagesPage(organizationId, chatId, userDetails.getId(), chatMessagesPageRequestDto)
        );
    }

    @PostMapping(API_ORGANIZATION_CHAT_ID_CHAT_MESSAGE)
    public ResponseEntity createMessage(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable long organizationId,
        @PathVariable long chatId,
        @RequestBody CreateChatMessageDto createChatMessageDto
    ) {
        authorizationService.ensureUserIsOrganizationOwnerOrMember(organizationId, userDetails.getId());
        chatService.ensureUserIsPartOfChat(chatId, userDetails.getId());
        return ResponseEntity.ok(chatService.sendMessageToOrganizationChatNotifyWsAndGetDto(
            organizationId, chatId, userDetails.getId(), createChatMessageDto
        ));
    }
}
