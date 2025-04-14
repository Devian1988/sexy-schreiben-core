package com.devsoft.sexyschreiben.core.dao.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class AuthorityId implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5644201164924014202L;

    @Column(name = "authority", nullable = false)
    @Enumerated(EnumType.STRING)
    Authority.Type authority;

    @Column(name = "userid", nullable = false)
    UUID userId;

    public static AuthorityId build(UUID userId,
                                    Authority.Type authority) {
        AuthorityId authorityId = new AuthorityId();
        authorityId.userId = userId;
        authorityId.authority = authority;
        return authorityId;
    }
}