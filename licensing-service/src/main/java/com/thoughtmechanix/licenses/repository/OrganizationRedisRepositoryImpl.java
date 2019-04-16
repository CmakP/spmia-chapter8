package com.thoughtmechanix.licenses.repository;

import com.thoughtmechanix.licenses.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

// The @Repository annotation tells Spring that this class is a Repository class used with Spring Data
@Repository
public class OrganizationRedisRepositoryImpl implements OrganizationRedisRepository {

    //The name of the hash in your Redis server where organization data is stored
    private static final String HASH_NAME ="organization";

    private RedisTemplate<String, Organization> redisTemplate;
    //The HashOperations class is a set of Spring helper methods for carrying out data operations on the Redis server
    private HashOperations hashOperations;

    public OrganizationRedisRepositoryImpl(){
        super();
    }

    @Autowired
    private OrganizationRedisRepositoryImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    //All interactions with Redis will be with a single Organization object stored by its key:

    @Override
    public void saveOrganization(Organization org) {
        hashOperations.put(HASH_NAME, org.getId(), org);
    }

    @Override
    public void updateOrganization(Organization org) {
        hashOperations.put(HASH_NAME, org.getId(), org);
    }

    @Override
    public void deleteOrganization(String organizationId) {
        hashOperations.delete(HASH_NAME, organizationId);
    }

    @Override
    public Organization findOrganization(String organizationId) {
       return (Organization) hashOperations.get(HASH_NAME, organizationId);
    }
}
