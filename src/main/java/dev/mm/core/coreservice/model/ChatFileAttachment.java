package dev.mm.core.coreservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class ChatFileAttachment extends BaseIdAndTimestamps {

    @Column(name = "file_id", insertable = false, updatable = false)
    private Long fileId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "file_id")
    private File file;

    @Column(name = "chat_message_id", insertable = false, updatable = false)
    private Long chatMessageId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "chat_message_id")
    private ChatMessage chatMessage;

}
