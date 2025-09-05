package org.fog.entities;

import dynamicOptimisation.utils.UtilFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Samodha Pallewatta
 * Round Robin LoadBalancer
 */

public class RRLoadBalancer implements LoadBalancer {
    protected Map<String, Integer> loadBalancerPosition = new HashMap();

    public int getDeviceId(String microservice, ControllerComponent.ServiceDiscovery serviceDiscoveryInfo) {
        if (loadBalancerPosition.containsKey(microservice) && serviceDiscoveryInfo.getServiceDiscoveryInfo().containsKey(microservice)) {
            int pos = loadBalancerPosition.get(microservice);
            if (pos + 1 > serviceDiscoveryInfo.getServiceDiscoveryInfo().get(microservice).size() - 1)
                pos = 0;
            else
                pos = pos + 1;
            loadBalancerPosition.put(microservice, pos);
            return serviceDiscoveryInfo.getServiceDiscoveryInfo().get(microservice).get(pos);
        } else {
            if(serviceDiscoveryInfo.getServiceDiscoveryInfo().containsKey(microservice)) {
                loadBalancerPosition.put(microservice, 0);
                if (serviceDiscoveryInfo.getServiceDiscoveryInfo().get(microservice) == null)
                    System.out.println("null");
                int deviceId = serviceDiscoveryInfo.getServiceDiscoveryInfo().get(microservice).get(0);
                return deviceId;
            }
            System.out.println("Service Discovery Information Missing");
            return -1;
        }
    }
}




//public class RRLoadBalancer implements LoadBalancer {
//    protected Map<String, Integer> loadBalancerPosition = new HashMap<>(); // For msType
//    protected Map<String, Map<String, Integer>> instancePositionMap = new HashMap<>(); // For msInstance within each msType
//
//    @Override
//    public String getDeviceId(String microservice, ControllerComponent.ServiceDiscovery serviceDiscoveryInfo) {
//        // Derive msType from microservice name by removing the last two characters
//        String msType = microservice.substring(0, microservice.length() - 2);
//
//        // Get the list of microservice instances for the msType
//        ArrayList<String> nameOfInstances = UtilFactory.microserviceTypeToInstance.get(msType);
//
//        if (nameOfInstances == null || nameOfInstances.isEmpty()) {
//           System.out.println("No instances found for msType: " + msType);
//            return "-1";
//        }
//
//        // Get or initialize the round-robin position for msType
//        int msTypePos = loadBalancerPosition.getOrDefault(msType, 0);
//
//        // Iterate over all instances to find a valid list of devices
//        for (int i = 0; i < nameOfInstances.size(); i++) {
//            // Ensure the position is within bounds
//            msTypePos = (msTypePos + i) % nameOfInstances.size();
//            String msInstance = nameOfInstances.get(msTypePos);
//
//            // Get or initialize the instancePosition map for the current msInstance
//            Map<String, Integer> instancePositions = instancePositionMap.getOrDefault(msType, new HashMap<>());
//            int instancePos = instancePositions.getOrDefault(msInstance, 0);
//
//            // Find the list of devices for the selected msInstance
//            List<Map<String, List<Integer>>> msTypeList = serviceDiscoveryInfo.getServiceDiscoveryInfo().get(msType);
//            if (msTypeList == null || msTypeList.isEmpty()) {
//                continue; // Skip if no service discovery info found for this msType
//            }
//
//            List<Integer> deviceList = null;
//            for (Map<String, List<Integer>> msMap : msTypeList) {
//                if (msMap.containsKey(msInstance)) {
//                    deviceList = msMap.get(msInstance);
//                    break;
//                }
//            }
//
//            if (deviceList == null || deviceList.isEmpty()) {
//                continue; // Continue to the next instance if no devices found
//            }
//
//            // Ensure the instance position is within bounds
//            if (instancePos >= deviceList.size()) {
//                instancePos = 0;
//            }
//
//            // Get the device ID for the selected instance and position
//            int deviceId = deviceList.get(instancePos);
//
//            // Update the round-robin positions for the next request
//            instancePos = (instancePos + 1) % deviceList.size();
//            instancePositions.put(msInstance, instancePos);
//            instancePositionMap.put(msType, instancePositions);
//
//            loadBalancerPosition.put(msType, (msTypePos + 1) % nameOfInstances.size());
//
//            return msInstance+"*"+deviceId;
//        }
//        return "-1";
//    }
//}
