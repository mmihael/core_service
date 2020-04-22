package dev.mm.core.coreservice.constants;

public enum Role {

    SUPER_ADMIN(1, false),
    ORGANIZATION_OWNER(2, true),
    ORGANIZATION_MEMBER(3, true);

    public final long id;
    public final boolean organizationRole;

    Role(long id, boolean organizationRole) {
        this.id = id;
        this.organizationRole = organizationRole;
    }

}
