package com.thoughtmechanix.licenses.events.handlers;

import com.thoughtmechanix.licenses.events.CustomChannels;
import com.thoughtmechanix.licenses.events.models.OrganizationChangeModel;
import com.thoughtmechanix.licenses.repository.OrganizationRedisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

/**
 * To use this class as a default input/output channel with Source and Sink interfaces:
 * 1. comment out @EnableBinding(CustomChannels.class)
 * 2. change @StreamListener("inboundOrgChanges") to @StreamListener(Sink.INPUT)
 * 3. change inboundOrgChanges to be: spring.cloud.stream.bindings.input in application.yml
 * 4. uncomment @EnableBinding(Sink.class) in Application.java
 * 5. uncomment loggerSink method in Application.java
 */
@EnableBinding(CustomChannels.class)
public class OrganizationChangeHandler {

    // The OrganizationRedisRepository class that you use to interact with Redis is injected here
    @Autowired
    private OrganizationRedisRepository organizationRedisRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationChangeHandler.class);

    // Previously used in Application.java
    // Pass in the name of your channel, “inboundOrgChanges”, instead of using Sink.INPUT
    @StreamListener("inboundOrgChanges")
    public void loggerSink(OrganizationChangeModel orgChange) {
        logger.debug("licenses.OrganizationChangeHandler.loggerSink() - Incoming message of type " + orgChange.getType());

        // If the organization data is updated or deleted, evict the organization data from Redis via the
        //OrganizationRedisRepository class
        switch(orgChange.getAction()){
            // Don't cache everytime getOrg endpoint is invoked in organizationservice
            // org data is only cached when there is a getOrganization RestExchange from licensingservice to organizationservice
            case "GET":
                logger.debug("licenses.OrganizationChangeHandler.loggerSink() - GET event detected from the organization service for organization id {}", orgChange.getOrganizationId());
                break;
            case "SAVE": // You don't want to crowd Redis cache & SAVE an org everytime a new org is saved in organizationservice
                logger.debug("licenses.OrganizationChangeHandler.loggerSink() - SAVE event detected from the organization service for organization id {}", orgChange.getOrganizationId());
                break;
            case "UPDATE":
                logger.debug("licenses.OrganizationChangeHandler.loggerSink() - Clearing Redis cache - Received an UPDATE event from the organization service for organization id {}", orgChange.getOrganizationId());
                organizationRedisRepository.deleteOrganization(orgChange.getOrganizationId());
                break;
            case "DELETE":
                logger.debug("licenses.OrganizationChangeHandler.loggerSink() - Clearing Redis cache - Received a DELETE event from the organization service for organization id {}", orgChange.getOrganizationId());
                organizationRedisRepository.deleteOrganization(orgChange.getOrganizationId());
                break;
            default:
                logger.error("licenses.OrganizationChangeHandler.loggerSink() - Received an UNKNOWN event from the organization service of type {}", orgChange.getType());
                break;
        }
        logger.debug("");
    }

}
