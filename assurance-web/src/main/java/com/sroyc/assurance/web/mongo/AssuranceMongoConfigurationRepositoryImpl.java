package com.sroyc.assurance.web.mongo;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.sroyc.assurance.core.data.AssuranceSSOConfig;
import com.sroyc.assurance.core.data.SSOType;
import com.sroyc.assurance.core.exception.AssuranceEntityException;

@Profile("sroyc.data.mongo")
@Component
public class AssuranceMongoConfigurationRepositoryImpl implements AssuranceMongoConfigurationRepository {

	private MongoSSOConfigRepository mongoRepo;
	private MongoTemplate mongoTemplate;

	@Autowired
	public AssuranceMongoConfigurationRepositoryImpl(MongoSSOConfigRepository mongoRepo, MongoTemplate mongoTemplate) {
		this.mongoRepo = mongoRepo;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public AssuranceSSOConfig findActiveSSOConfig(SSOType type) throws AssuranceEntityException {
		List<MongoSSOConfigEntity> configs = this.findAllActive();
		if (!CollectionUtils.isEmpty(configs)) {
			for (MongoSSOConfigEntity entity : configs) {
				if (type == entity.getType()) {
					return entity;
				}
			}
		}
		return null;
	}

	@Override
	public List<AssuranceSSOConfig> findAll() {
		return this.mongoRepo.findAll().stream().map(entity -> (AssuranceSSOConfig) entity)
				.collect(Collectors.toList());
	}

	@Override
	public String save(AssuranceSSOConfig config) {
		MongoSSOConfigEntity entity = this.cast(config);
		entity = this.mongoRepo.save(entity);
		return entity.getId();
	}

	protected MongoSSOConfigEntity cast(AssuranceSSOConfig config) {
		if (config instanceof MongoSSOConfigEntity) {
			return (MongoSSOConfigEntity) config;
		} else {
			MongoSSOConfigEntity entity = new MongoSSOConfigEntity();
			entity.setActive(config.isActive());
			entity.setMetadata(config.getMetadata());
			entity.setName(config.getName());
			entity.setType(config.getType());
			entity.autoGenerateId();
			return entity;
		}
	}

	@Override
	public boolean update(String configId, boolean active) {
		Query q = new Query();
		q.addCriteria(Criteria.where("id").is(configId));
		Update update = new Update();
		update.set("active", Boolean.valueOf(active));
		return this.mongoTemplate.update(MongoSSOConfigEntity.class).matching(q).apply(update).all().wasAcknowledged();
	}

	@Override
	public void delete(String configId) throws AssuranceEntityException {
		Optional<MongoSSOConfigEntity> entity = this.mongoRepo.findById(configId);
		if (entity.isPresent()) {
			this.mongoRepo.deleteById(configId);
		} else {
			throw new AssuranceEntityException("Invalid configuration id");
		}
	}

	@Override
	public Optional<MongoSSOConfigEntity> findById(String configId) {
		return this.mongoRepo.findById(configId);
	}

	@Override
	public List<MongoSSOConfigEntity> findAllActive() {
		Query q = new Query();
		q.addCriteria(Criteria.where("active").is(Boolean.valueOf(true)));
		return this.mongoTemplate.find(q, MongoSSOConfigEntity.class);
	}

	@Override
	public List<AssuranceSSOConfig> findSSOConfigs(SSOType type) throws AssuranceEntityException {
		Query q = new Query();
		q.addCriteria(Criteria.where("type").is(type.name()));
		List<MongoSSOConfigEntity> mongoConfigs = this.mongoTemplate.find(q, MongoSSOConfigEntity.class);
		if (CollectionUtils.isEmpty(mongoConfigs)) {
			return Collections.emptyList();
		} else {
			return mongoConfigs.stream().map(mc -> ((AssuranceSSOConfig) mc)).collect(Collectors.toList());
		}
	}

}
