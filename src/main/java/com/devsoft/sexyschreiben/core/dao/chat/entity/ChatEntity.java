package com.devsoft.sexyschreiben.core.dao.chat.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@Table(name = "chat")
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChatEntity {
    @Id
    @EqualsAndHashCode.Include
    ChatType id;
    String name;

    public static Specification<ChatEntity> containsText(String search) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(criteriaBuilder.like(root.get("name"), "%" + search + "%"));
    }
}
