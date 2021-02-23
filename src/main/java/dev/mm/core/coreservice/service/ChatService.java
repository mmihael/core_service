package dev.mm.core.coreservice.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import dev.mm.core.coreservice.dto.chat.ChatDto;
import dev.mm.core.coreservice.dto.chat.ChatMessageDto;
import dev.mm.core.coreservice.dto.chat.ChatMessagesPageRequestDto;
import dev.mm.core.coreservice.dto.chat.ChatPageRequestDto;
import dev.mm.core.coreservice.dto.chat.CreateChatDto;
import dev.mm.core.coreservice.dto.chat.CreateChatMessageDto;
import dev.mm.core.coreservice.dto.response.PageResponseDto;
import dev.mm.core.coreservice.exception.EntityNotFoundException;
import dev.mm.core.coreservice.exception.ForbiddenException;
import dev.mm.core.coreservice.model.Chat;
import dev.mm.core.coreservice.model.ChatMembership;
import dev.mm.core.coreservice.model.ChatMessage;
import dev.mm.core.coreservice.model.Organization;
import dev.mm.core.coreservice.model.QChat;
import dev.mm.core.coreservice.model.QChatMessage;
import dev.mm.core.coreservice.model.User;
import dev.mm.core.coreservice.repository.ChatMembershipRepository;
import dev.mm.core.coreservice.repository.ChatMessageRepository;
import dev.mm.core.coreservice.repository.ChatRepository;
import dev.mm.core.coreservice.util.MapUtil;
import dev.mm.core.coreservice.util.StringUtil;
import dev.mm.core.coreservice.validator.ChatValidator;
import org.flywaydb.core.internal.jdbc.TransactionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.mm.core.coreservice.util.TranslationUtil.translate;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatMembershipRepository chatMembershipRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ChatValidator chatValidator;

    @Autowired
    private WebsocketService websocketService;

    @Autowired
    private PaginationService paginationService;

    public PageResponseDto getPage(Long organizationId, long userId, ChatPageRequestDto chatPageRequestDto) {

        String name = chatPageRequestDto.getName();

        PageResponseDto pageResponseDto = paginationService.getPage(chatPageRequestDto, jpaQuery -> {
            jpaQuery.from(QChat.chat).distinct();
            jpaQuery.innerJoin(QChat.chat.chatMemberships);

            BooleanExpression expression = QChat.chat.organizationId.eq(organizationId)
                .and(QChat.chat.chatMemberships.any().userId.eq(userId))
                .and(QChat.chat.deleted.isFalse());

            if (StringUtil.isNotBlank(name)) {
                expression = expression.and(QChat.chat.name.containsIgnoreCase(name));
            }

            jpaQuery.where(expression);

            jpaQuery.orderBy(QChat.chat.updatedAt.desc());
        });

        pageResponseDto.setContent(((List<Chat>) pageResponseDto.getContent()).stream().map(ChatDto::new)
            .collect(Collectors.toList()));

        return pageResponseDto;
    }

    public PageResponseDto getChatMessagesPage(
        long organizationId,
        long chatId,
        long userId,
        ChatMessagesPageRequestDto chatMessagesPageRequestDto
    ) {
        Long idLessThan = chatMessagesPageRequestDto.getIdLessThan();

        PageResponseDto pageResponseDto = paginationService.getPage(chatMessagesPageRequestDto, jpaQuery -> {

            jpaQuery.from(QChatMessage.chatMessage);
            jpaQuery.innerJoin(QChatMessage.chatMessage.chat);
            jpaQuery.innerJoin(QChatMessage.chatMessage.user).fetch();

            BooleanExpression expression = QChatMessage.chatMessage.chatId.eq(chatId)
                .and(QChatMessage.chatMessage.chat.organizationId.eq(organizationId));

            if (idLessThan != null) {
                expression = expression.and(QChatMessage.chatMessage.id.lt(idLessThan));
            }

            jpaQuery.where(expression);
            jpaQuery.orderBy(QChatMessage.chatMessage.id.desc());
        });

        pageResponseDto.setContent(((List<ChatMessage>) pageResponseDto.getContent()).stream()
            .map(ChatMessageDto::withUser).collect(Collectors.toList()));

        return pageResponseDto;
    }

    public Chat validateAndCreateChatInOrganization(Long organizationId, long userId, CreateChatDto createChatDto) {
        chatValidator.validateCreateChat(organizationId, createChatDto);
        return createChatInOrganization(organizationId, userId, createChatDto);
    }

    public Chat createChatInOrganization(long organizationId, long userId, CreateChatDto createChatDto) {
        Organization organization = organizationService.getOrganizationByIdOrThrow(organizationId);
        User creator = userService.getUserOrThrow(userId);
        List<User> users = userService.getAllUsersInOrThrow(createChatDto.getUserIds());

        Chat chat = new Chat();
        chat.setName(createChatDto.getName());
        chat.setOrganization(organization);
        chat.setCreator(creator);
        chat.setChatMemberships(users.stream().map(user -> {
            ChatMembership chatMembership = new ChatMembership();
            chatMembership.setChat(chat);
            chatMembership.setUser(user);
            chatMembership.setAdmin(userId == user.getId());
            return chatMembership;
        }).collect(Collectors.toSet()));

        return chatRepository.save(chat);
    }

    public ChatMessageDto sendMessageToOrganizationChatNotifyWsAndGetDto(
        long organizationId,
        long chatId,
        long userId,
        CreateChatMessageDto createChatMessageDto
    ) {
        ChatMessage chatMessage = sendMessageToOrganizationChat(organizationId, chatId, userId, createChatMessageDto);
        ChatMessageDto chatMessageDto = new ChatMessageDto(chatMessage, true);
        Set<String> userNames = chatMessage.getChat().getChatMemberships().stream()
            .filter(chatMembership -> userId != chatMembership.getUserId()).map(ChatMembership::getUser)
            .filter(Objects::nonNull).map(User::getUsername).collect(Collectors.toSet());
        websocketService.sendNewChatMessageToUsers(userNames, chatMessageDto);
        return chatMessageDto;
    }

    public ChatMessage sendMessageToOrganizationChat(
        long organizationId,
        long chatId,
        long userId,
        CreateChatMessageDto createChatMessageDto
    ) {
        User user = userService.getUserOrThrow(userId);
        Chat chat = getChatByIdAndOrganizationIdFetchMembershipsOrThrow(organizationId, chatId);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChat(chat);
        chatMessage.setUser(user);
        chatMessage.setMessage(createChatMessageDto.getMessage());
        chatMessage = chatMessageRepository.save(chatMessage);

        return chatMessage;
    }

    public ChatDto getChatDtoByIdAndOrganizationIdFetchMembershipsOrThrow(long organizationId, long chatId) {
        return new ChatDto(getChatByIdAndOrganizationIdFetchMembershipsOrThrow(organizationId, chatId), true);
    }

    public Chat getChatByIdAndOrganizationIdFetchMembershipsOrThrow(long organizationId, long chatId) {
        return chatRepository
            .getUsersChatByIdAndOrganizationIdFetchMemberships(chatId, organizationId)
            .orElseThrow(() -> new EntityNotFoundException(translate(
                "Chat with id: {{ id }} not found in organization id: {{ organizationId }}",
                MapUtil.mapFromKeyVal("id", chatId, "organizationId", organizationId)
            )));
    }

    public void ensureUserIsPartOfChat(long chatId, long userId) {
        if (chatMembershipRepository.countByChatIdAndUserIdAndChatNotDeleted(chatId, userId) < 1) {
            throw new ForbiddenException("You are not part of the chat");
        }
    }

}
