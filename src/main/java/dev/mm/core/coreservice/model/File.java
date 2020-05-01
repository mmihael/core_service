package dev.mm.core.coreservice.model;

import dev.mm.core.coreservice.constants.FileAccessType;
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
public class File extends BaseIdAndTimestamps {

    @Column(unique = true)
    private String uuid;
    private String url;
    private String originalName;
    private String fileType;
    private FileAccessType accessType;
    private long size;
    private Long width;
    private Long height;
    private Long length;

    @Column(name = "owner_id", insertable = false, updatable = false)
    private Long ownerId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "organization_id", insertable = false, updatable = false)
    private Long organizationId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "organization_id")
    private Organization organization;

}