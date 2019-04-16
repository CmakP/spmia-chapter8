package com.thoughtmechanix.organization.events.source;

import com.thoughtmechanix.organization.events.models.OrganizationChangeModel;
import com.thoughtmechanix.organization.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


/**
 * Remember, all communication to a specific message topic occurs through a Spring Cloud Stream construct called a channel.
 */
@Component
public class SimpleSourceBean {
    private Source source;

    private static final Logger logger = LoggerFactory.getLogger(SimpleSourceBean.class);

    // Spring Cloud Stream will inject a Source interface implementation for use by the service.
    @Autowired
    public SimpleSourceBean(Source source){
        this.source = source;
    }

    // The actual publication of the message occurs in this method
    public void publishOrgChange(String action,String orgId){
       logger.debug("organization.SimpleSourceBean.publishOrgChange() - Sending Kafka message {} for Organization Id: {}", action, orgId);
        // The message to be published is a Java POJO
        OrganizationChangeModel change =  new OrganizationChangeModel(
                OrganizationChangeModel.class.getTypeName(),
                action,
                orgId,
                UserContext.getCorrelationId());

        /**
         * When you’re ready to send the message, use the send() method from a channel defined on the Source class
         * The configuration property spring.stream.bindings.output in application.yml maps the source.output() channel
         * to the orgChangeTopic on the message broker you’re going to communicate with.
         */
        source.output().send(MessageBuilder.withPayload(change).build());
        logger.debug("organization.SimpleSourceBean.publishOrgChange() - Kafka message sent!");
    }
}
