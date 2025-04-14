package com.devsoft.sexyschreiben.core.dao.user;


import com.devsoft.sexyschreiben.core.dao.DefaultRepo;
import com.devsoft.sexyschreiben.core.dao.client.entity.ClientEntity;
import com.devsoft.sexyschreiben.core.dao.user.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepo extends DefaultRepo<UserEntity, UUID> {
    UserEntity findByUsernameAndClient(String username,
                                       ClientEntity client);
    UserEntity findByUsernameAndClientId(String username,
                                         UUID clientId);
}
