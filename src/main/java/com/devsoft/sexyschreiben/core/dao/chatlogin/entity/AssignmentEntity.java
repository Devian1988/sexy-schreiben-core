package com.devsoft.sexyschreiben.core.dao.chatlogin.entity;


import com.devsoft.sexyschreiben.core.dao.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "chatlogin_assignment")
@Getter
@Setter
@EqualsAndHashCode
public class AssignmentEntity {

    @Id
    @EqualsAndHashCode.Include
    UUID id;
    @ManyToOne(optional = false)
    ChatloginEntity chatlogin;
    @ManyToOne(optional = false)
    UserEntity user;
    @Column(name = "from_")
    LocalDate from;
    @Column(name = "to_")
    LocalDate to;
}
