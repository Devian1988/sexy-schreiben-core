package com.devsoft.sexyschreiben.core.dao.client.entity;


import com.devsoft.sexyschreiben.core.dao.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Table(name = "client", uniqueConstraints = {@UniqueConstraint(columnNames = {"client_identifier"})})
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ClientEntity {
    @Id
    @EqualsAndHashCode.Include
    UUID id;
    @Column(name = "client_identifier")
    String clientIdentifier;
    String name;
    String status;
    String plan;
    String loadBalancerListenerARN;
    @ManyToMany
    @JoinTable(name = "client_leader", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<UserEntity> leaders = new HashSet<>();
    boolean enabled;

    public static Specification<ClientEntity> containsText(String search) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(criteriaBuilder.like(root.get("name"), "%" + search + "%"));
    }
}
