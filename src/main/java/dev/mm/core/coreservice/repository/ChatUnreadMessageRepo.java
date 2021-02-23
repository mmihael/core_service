package dev.mm.core.coreservice.repository;

import dev.mm.core.coreservice.model.Chat;
import dev.mm.core.coreservice.model.ChatUnreadMessage;

public interface ChatUnreadMessageRepo extends JpaQueryDslRepository<ChatUnreadMessage, Long> {

}
