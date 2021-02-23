package dev.mm.core.coreservice.repository;

import dev.mm.core.coreservice.model.ChatMembership;
import org.springframework.data.jpa.repository.Query;

public interface ChatMembershipRepository extends JpaQueryDslRepository<ChatMembership, Long> {

    @Query("select count(distinct cm.id) from ChatMembership cm inner join Chat c on c.id = cm.chatId where cm.chatId = ?1 and cm.userId = ?2 and c.deleted = false")
    int countByChatIdAndUserIdAndChatNotDeleted(long chatId, long userId);
}
