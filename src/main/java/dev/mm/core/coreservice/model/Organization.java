package dev.mm.core.coreservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class Organization extends BaseIdAndTimestamps {

    @Column(unique = true)
    private String name;

    private boolean deleted;

}