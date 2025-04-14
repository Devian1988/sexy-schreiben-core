package com.devsoft.sexyschreiben.core.dao.user;


import com.devsoft.sexyschreiben.core.dao.user.entity.Authority;
import com.devsoft.sexyschreiben.core.dao.user.entity.AuthorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepo extends JpaRepository<Authority, AuthorityId> {
}
