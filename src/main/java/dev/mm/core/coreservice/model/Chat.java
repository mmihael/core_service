package dev.mm.core.coreservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Chat extends BaseIdAndTimestamps {

    private String name;

    private boolean deleted;

    @Column(name = "creator_id", insertable = false, updatable = false)
    private Long creatorId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "organization_id", insertable = false, updatable = false)
    private Long organizationId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "chat", fetch = FetchType.LAZY)
    private Set<ChatMembership> chatMemberships = new HashSet<>();

}
