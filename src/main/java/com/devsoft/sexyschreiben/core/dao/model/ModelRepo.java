package com.devsoft.sexyschreiben.core.dao.model;


import com.devsoft.sexyschreiben.core.dao.DefaultRepo;
import com.devsoft.sexyschreiben.core.dao.model.entity.ModelEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModelRepo extends DefaultRepo<ModelEntity, UUID> {
    @Query("SELECT DISTINCT m.proxy FROM ModelEntity m WHERE m.proxy IS NOT NULL")
    public List<String> getProxies();
}
