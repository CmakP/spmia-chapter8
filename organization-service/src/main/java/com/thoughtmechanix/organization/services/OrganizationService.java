package com.thoughtmechanix.organization.services;

import com.thoughtmechanix.organization.events.source.SimpleSourceBean;
import com.thoughtmechanix.organization.model.Organization;
import com.thoughtmechanix.organization.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    @Autowired
    private OrganizationRepository orgRepository;

    @Autowired
    SimpleSourceBean simpleSourceBean;

    public Organization getOrg(String organizationId) {
        return orgRepository.findById(organizationId);
    }

    public void saveOrg(Organization org){
        org.setId( UUID.randomUUID().toString());

        orgRepository.save(org);
        logger.debug("organization.OrganizationService.saveOrg() - invoking the publisher...");
        simpleSourceBean.publishOrgChange("SAVE", org.getId());
    }

    public void updateOrg(Organization org){
        orgRepository.save(org);
        logger.debug("organization.OrganizationService.updateOrg() - invoking the publisher...");
        simpleSourceBean.publishOrgChange("UPDATE", org.getId());

    }

    public void deleteOrg(String  orgId){
        orgRepository.delete( orgId );
        logger.debug("organization.OrganizationService.deleteOrg() - invoking the publisher...");
        simpleSourceBean.publishOrgChange("DELETE", orgId);
    }
}
