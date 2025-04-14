package com.devsoft.sexyschreiben.core.dao.chatlogin.entity;

import com.devsoft.sexyschreiben.core.dao.chat.entity.ChatEntity;
import com.devsoft.sexyschreiben.core.dao.model.entity.ModelEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Table(name = "chatlogin")
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChatloginEntity {
    @Id
    @EqualsAndHashCode.Include
    UUID id;
    @ManyToOne(optional = false)
    ChatEntity chat;
    String username;
    String password;
    String displayName;
    @OneToMany(mappedBy = "chatlogin", orphanRemoval = true)
    final List<AssignmentEntity> assignments = new ArrayList<>();
    @ManyToOne(optional = false)
    ModelEntity model;

    public String getDisplay() {
        return username;
    }

    public static Specification<ChatloginEntity> containsText(String search) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(criteriaBuilder.like(root.get("username"), "%" + search + "%"));
    }
}
