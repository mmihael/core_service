package dev.mm.core.coreservice.model;

import com.querydsl.core.annotations.QueryInit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class User extends BaseIdAndTimestamps {

    @Column(unique = true)
    private String username;
    private String password;
    private boolean enabled;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "user", fetch = FetchType.LAZY)
    // @QueryInit({"role.name", "role.id"})
    private Set<UserRole> userRoles = new HashSet<>();

}