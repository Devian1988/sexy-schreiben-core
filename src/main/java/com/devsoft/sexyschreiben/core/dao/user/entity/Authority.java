package com.devsoft.sexyschreiben.core.dao.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Getter
@Setter
@Table(name = "authorities")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Authority implements GrantedAuthority {

    public static final String ADMIN_ROLE = "ADMIN";
    public static final String CLIENT_ADMIN_ROLE = "CLIENT_ADMIN";
    public static final String CLIENT_USER_ROLE = "CLIENT_USER";
    public static final String GUEST_ROLE = "GUEST";
    /**
     *
     */
    private static final long serialVersionUID = 3420579434329640159L;
    @ManyToOne()
    @JoinColumn(name = "userid", nullable = false, insertable = false, updatable = false)
    @Setter(AccessLevel.PROTECTED)
    UserEntity user;
    @Column(name = "authority", insertable = false, updatable = false)
    @Setter(AccessLevel.PROTECTED)
    @Enumerated(EnumType.STRING)
    Type type;
    @EmbeddedId
    @EqualsAndHashCode.Include
    private AuthorityId id;

    public Authority(AuthorityId id) {
        this.id = id;
    }

    @Override
    public String getAuthority() {
        return type.name();
    }

    public enum Type {
        ADMIN, CLIENT_ADMIN, CLIENT_USER, GUEST;

        public static Type getForString(String type) {
            Type found = null;
            for (Type typeEnum : Type.values()) {
                if (typeEnum.name().equals(type)) {
                    found = typeEnum;
                    break;
                }
            }
            return found;
        }
    }
}