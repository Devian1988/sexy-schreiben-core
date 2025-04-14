package com.devsoft.sexyschreiben.core.dao.chat;

import com.devsoft.sexyschreiben.core.dao.DefaultRepo;
import com.devsoft.sexyschreiben.core.dao.chat.entity.ChatEntity;
import com.devsoft.sexyschreiben.core.dao.chat.entity.ChatType;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepo extends DefaultRepo<ChatEntity, ChatType> {
}
