package com.devsoft.sexyschreiben.core.dao.chatlogin;

import com.devsoft.sexyschreiben.core.dao.DefaultRepo;
import com.devsoft.sexyschreiben.core.dao.chatlogin.entity.ChatloginEntity;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatloginRepo extends DefaultRepo<ChatloginEntity, UUID> {
}
