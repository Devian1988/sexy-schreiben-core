package com.devsoft.sexyschreiben.core.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface DefaultRepo<EntityClass, IDClass> extends JpaRepository<EntityClass, IDClass>, JpaSpecificationExecutor<EntityClass> {
}
