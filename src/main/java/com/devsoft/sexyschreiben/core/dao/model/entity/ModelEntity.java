package com.devsoft.sexyschreiben.core.dao.model.entity;


import com.devsoft.sexyschreiben.core.dao.chatlogin.entity.ChatloginEntity;
import com.devsoft.sexyschreiben.core.dao.client.entity.ClientEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Table(name = "model", uniqueConstraints = {@UniqueConstraint(name = "proxy_uc", columnNames = {"proxy"})})
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ModelEntity {
    @Id
    @EqualsAndHashCode.Include
    UUID id;
    String name;
    String proxy;
    @ManyToOne(optional = false)
    ClientEntity client;
    @OneToMany(cascade = CascadeType.ALL)
    List<ChatloginEntity> chatlogins;

    public static Specification<ModelEntity> containsText(String search) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(criteriaBuilder.like(root.get("name"), "%" + search + "%"));
    }
}
