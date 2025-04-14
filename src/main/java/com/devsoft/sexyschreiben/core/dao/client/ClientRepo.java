package com.devsoft.sexyschreiben.core.dao.client;


import com.devsoft.sexyschreiben.core.dao.DefaultRepo;
import com.devsoft.sexyschreiben.core.dao.client.entity.ClientEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepo extends DefaultRepo<ClientEntity, UUID> {
    Optional<ClientEntity> findByClientIdentifier(String clientIdentifier);
}
