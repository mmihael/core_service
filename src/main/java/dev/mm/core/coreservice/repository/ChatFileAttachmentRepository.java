package dev.mm.core.coreservice.repository;

import dev.mm.core.coreservice.model.Chat;
import dev.mm.core.coreservice.model.ChatFileAttachment;

public interface ChatFileAttachmentRepository extends JpaQueryDslRepository<ChatFileAttachment, Long> {
    
}
