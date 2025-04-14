package com.devsoft.sexyschreiben.core.dao.chatlogin;

import com.devsoft.sexyschreiben.core.dao.DefaultRepo;
import com.devsoft.sexyschreiben.core.dao.chatlogin.entity.AssignmentEntity;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssignmentRepo extends DefaultRepo<AssignmentEntity, UUID> {
}
