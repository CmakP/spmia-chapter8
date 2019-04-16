package com.thoughtmechanix.licenses.events;


import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {
    @Input("inboundOrgChanges") // method-level annotation that defines the name of the channel.
    SubscribableChannel orgs(); //Each channel exposed through the @Input annotation must return a SubscribableChannel class
}
