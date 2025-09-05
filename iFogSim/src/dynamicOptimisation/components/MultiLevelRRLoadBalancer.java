package dynamicOptimisation.components;


import dynamicOptimisation.utils.UtilFactory;
import org.fog.entities.ControllerComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//public class MultiLevelRRLoadBalancer {
//    // Maps to track the round-robin positions for msTypes and msInstances
//    protected Map<String, Integer> loadBalancerPosition = new HashMap<>(); // For msType
//    protected Map<String, Map<String, Integer>> instancePositionMap = new HashMap<>(); // For msInstance within each msType
//
//    public int getDeviceId(String microservice, ControllerComponent.ServiceDiscovery serviceDiscoveryInfo) {
//        // Derive msType from microservice name by removing the last two characters
//        String msType = microservice.substring(0, microservice.length() - 2);
//
//        // Get the list of microservice instances for the msType
//        ArrayList<String> nameOfInstances = UtilFactory.microserviceToInternalServiceMap.get(msType);
//
//        if (nameOfInstances == null || nameOfInstances.isEmpty()) {
//            System.out.println("No instances found for msType: " + msType);
//            return -1;
//        }
//
//        // Get or initialize the round-robin position for msType
//        int msTypePos = loadBalancerPosition.getOrDefault(msType, 0);
//
//        // Ensure the position is within bounds
//        if (msTypePos >= nameOfInstances.size()) {
//            msTypePos = 0;
//        }
//
//        // Select the current msInstance
//        String msInstance = nameOfInstances.get(msTypePos);
//
//        // Get or initialize the instancePosition map for the current msInstance
//        Map<String, Integer> instancePositions = instancePositionMap.getOrDefault(msType, new HashMap<>());
//        int instancePos = instancePositions.getOrDefault(msInstance, 0);
//
//        // Find the list of devices for the selected msInstance
//        List<Map<String, List<Integer>>> msTypeList = serviceDiscoveryInfo.getServiceDiscoveryInfo().get(msType);
//        if (msTypeList == null || msTypeList.isEmpty()) {
//            System.out.println("No service discovery info found for msType: " + msType);
//            return -1;
//        }
//
//        List<Integer> deviceList = null;
//        for (Map<String, List<Integer>> msMap : msTypeList) {
//            if (msMap.containsKey(msInstance)) {
//                deviceList = msMap.get(msInstance);
//                break;
//            }
//        }
//
//        if (deviceList == null || deviceList.isEmpty()) {
//            System.out.println("No devices found for msInstance: " + msInstance);
//            return -1;
//        }
//
//        // Ensure the instance position is within bounds
//        if (instancePos >= deviceList.size()) {
//            instancePos = 0;
//        }
//
//        // Get the device ID for the selected instance and position
//        int deviceId = deviceList.get(instancePos);
//
//        // Update the round-robin positions for the next request
//        instancePos = (instancePos + 1) % deviceList.size();
//        instancePositions.put(msInstance, instancePos);
//        instancePositionMap.put(msType, instancePositions);
//
//        msTypePos = (msTypePos + 1) % nameOfInstances.size();
//        loadBalancerPosition.put(msType, msTypePos);
//
//        return deviceId;
//    }
//}
