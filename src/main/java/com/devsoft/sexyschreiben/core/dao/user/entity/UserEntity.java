package com.devsoft.sexyschreiben.core.dao.user.entity;


import com.devsoft.sexyschreiben.core.dao.chatlogin.entity.AssignmentEntity;
import com.devsoft.sexyschreiben.core.dao.client.entity.ClientEntity;
import jakarta.persistence.*;
import jakarta.persistence.criteria.Predicate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"username", "client_id"}))
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity implements UserDetails {
    @Id
    @EqualsAndHashCode.Include
    UUID id;
    String username;
    String name;
    String surname;
    @ManyToOne(optional = false)
    ClientEntity client;
    String password;
    String mail;
    boolean enabled;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
    Set<Authority> authorities;
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    Set<AssignmentEntity> chatloginAssignments;

    public String getDisplay() {
        return surname + ", " + name;
    }

    public static Specification<ClientEntity> containsText(String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (String s : search.split(" ")) {
                predicates.add(criteriaBuilder.or(criteriaBuilder.like(root.get("name"), "%" + s + "%"), criteriaBuilder.like(root.get("surname"), "%" + s + "%")));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
