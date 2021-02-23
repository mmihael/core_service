package dev.mm.core.coreservice.repository;

import dev.mm.core.coreservice.model.Chat;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRepository extends JpaQueryDslRepository<Chat, Long> {

    @Query("select c from Chat c left join fetch c.chatMemberships cm left join fetch cm.user where c.id = ?1 and c.organizationId = ?2")
    Optional<Chat> getUsersChatByIdAndOrganizationIdFetchMemberships(long id, long organizationId);
}
