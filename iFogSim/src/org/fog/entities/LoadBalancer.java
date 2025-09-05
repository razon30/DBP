package org.fog.entities;

import dynamicOptimisation.components.MultiLevelServiceDiscovery;

/**
 * Created by Samodha Pallewatta
 */
public interface LoadBalancer {
   int getDeviceId(String microservice, ControllerComponent.ServiceDiscovery serviceDiscoveryInfo);
    //String getDeviceId(String microservice, ControllerComponent.ServiceDiscovery serviceDiscoveryInfo);
    //int getDeviceId(String microservice, MultiLevelServiceDiscovery serviceDiscoveryInfo);
}
