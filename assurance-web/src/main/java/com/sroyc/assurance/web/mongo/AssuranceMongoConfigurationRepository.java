package com.sroyc.assurance.web.mongo;

import java.util.List;
import java.util.Optional;

import com.sroyc.assurance.core.AssuranceConfigurationRepository;

public interface AssuranceMongoConfigurationRepository extends AssuranceConfigurationRepository {

	public Optional<MongoSSOConfigEntity> findById(String configId);

	public List<MongoSSOConfigEntity> findAllActive();

}
