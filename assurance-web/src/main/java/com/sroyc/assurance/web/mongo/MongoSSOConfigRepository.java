package com.sroyc.assurance.web.mongo;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Profile("sroyc.data.mongo")
@Repository
public interface MongoSSOConfigRepository extends MongoRepository<MongoSSOConfigEntity, String> {

	public List<MongoSSOConfigEntity> findByActive(Boolean active);

}
