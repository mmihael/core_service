package dev.mm.core.coreservice.repository;

import dev.mm.core.coreservice.model.Chat;
import dev.mm.core.coreservice.model.ChatMessage;

public interface ChatMessageRepository extends JpaQueryDslRepository<ChatMessage, Long> {

}
