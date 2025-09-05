package org.fog.entities;

import org.fog.application.Application;
import org.fog.placement.MicroservicePlacementLogic;
import org.fog.placement.PlacementLogicOutput;

import java.util.*;

/**
 * Created by Samodha Pallewatta on 8/29/2019.
 */
public class ControllerComponent {

    protected LoadBalancer loadBalancer;
    protected MicroservicePlacementLogic microservicePlacementLogic = null;
    protected ServiceDiscovery serviceDiscoveryInfo;

    protected int deviceId;

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }


    // Resource Availability Info
    /**
     * Resource Identifiers
     */
    public static final String RAM = "ram";
    public static final String CPU = "cpu";
    public static final String STORAGE = "storage";
    public static final String BW = "bandwidth";

    /**
     * DeviceID,<ResourceIdentifier,AvailableResourceAmount>
     */
    protected Map<Integer, Map<String, Double>> resourceAvailability = new HashMap<>();


    //Application Info
    private Map<String, Application> applicationInfo = new HashMap<>();

    //FOg Architecture Info
    private List<FogDevice> fogDeviceList;


    /**
     * For FON
     *
     * @param loadBalancer
     * @param mPlacement
     */
    public ControllerComponent(Integer deviceId, LoadBalancer loadBalancer, MicroservicePlacementLogic mPlacement,
                               Map<Integer, Map<String, Double>> resourceAvailability, Map<String, Application> applicationInfo, List<FogDevice> fogDevices) {
        this.fogDeviceList = fogDevices;
        setFogDeviceList(fogDevices);
        this.loadBalancer = loadBalancer;
        this.applicationInfo = applicationInfo;
        this.microservicePlacementLogic = mPlacement;
        this.resourceAvailability = resourceAvailability;
        setDeviceId(deviceId);
        serviceDiscoveryInfo = new ServiceDiscovery(deviceId);

    }

    /**
     * For FCN
     *
     * @param loadBalancer
     */
    public ControllerComponent(Integer deviceId, LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
        setDeviceId(deviceId);
        serviceDiscoveryInfo = new ServiceDiscovery(deviceId);
    }

    /**
     * 1. execute placement logic -> returns the placement mapping.
     * 2. deploy on devices.
     * 3. update service discovery.
     */
    public PlacementLogicOutput executeApplicationPlacementLogic(List<PlacementRequest> placementRequests) {
        if (microservicePlacementLogic != null) {
            PlacementLogicOutput placement = microservicePlacementLogic.run(fogDeviceList, applicationInfo, resourceAvailability, placementRequests);
            return placement;
        }

        return null;
    }

    public void addServiceDiscoveryInfo(String microserviceName, Integer deviceID) {
        if (microserviceName.equals("Resource_Event_Analysis_Microservice_2"))
        {
            System.out.println("Found");
        }
        this.serviceDiscoveryInfo.addServiceDIscoveryInfo(microserviceName, deviceID);
        //System.out.println("Service Discovery Info ADDED (device:" + this.deviceId + ") for microservice :" +
         // microserviceName + " , destDevice : " + deviceID);
    }

    public int getDestinationDeviceId(String destModuleName) {
        return loadBalancer.getDeviceId(destModuleName, serviceDiscoveryInfo);
    }

    public Application getApplicationPerId(String appID) {
        return applicationInfo.get(appID);
    }

    public void setApplicationInfo(Map<String, Application> applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public Double getAvailableResource(int deviceID, String resourceIdentifier) {
        if (resourceAvailability.containsKey(deviceID))
            return resourceAvailability.get(deviceID).get(resourceIdentifier);
        else
            return null;
    }

    public void updateResources(int device, String resourceIdentifier, double remainingResourceAmount) {
        if (resourceAvailability.containsKey(device))
            resourceAvailability.get(device).put(resourceIdentifier, remainingResourceAmount);
        else {
            Map<String, Double> resources = new HashMap<>();
            resources.put(resourceIdentifier, remainingResourceAmount);
            resourceAvailability.put(device, resources);
        }
    }

    public void updateResourceInfo(int deviceId, Map<String, Double> resources) {
        resourceAvailability.put(deviceId, resources);
    }

    public void removeServiceDiscoveryInfo(String microserviceName, Integer deviceID) {
        this.serviceDiscoveryInfo.removeServiceDIscoveryInfo(microserviceName, deviceID);
    }

    public void removeMonitoredDevice(FogDevice fogDevice) {
        this.fogDeviceList.remove(fogDevice);
    }

    public void addMonitoredDevice(FogDevice fogDevice) {
        this.fogDeviceList.add(fogDevice);
    }

    public List<FogDevice> getFogDeviceList() {
        return fogDeviceList;
    }

    public void setFogDeviceList(List<FogDevice> fogDeviceList) {
        this.fogDeviceList = fogDeviceList;
    }

    public MicroserviceFogDevice getMicroserviceFogDevice(int deviceId){
        MicroserviceFogDevice microserviceFogDevice = null;
        for(FogDevice fogDevice: getFogDeviceList()){
            if (fogDevice.getId() == deviceId){
                microserviceFogDevice = (MicroserviceFogDevice) fogDevice;
            }
        }
        return microserviceFogDevice;
    }

    public int getCloudId(){
        for(FogDevice fogDevice: getFogDeviceList()){
            MicroserviceFogDevice   microserviceFogDevice = (MicroserviceFogDevice) fogDevice;
            if (microserviceFogDevice.getDeviceType().equals(MicroserviceFogDevice.CLOUD)){
                return microserviceFogDevice.getId();
            }
        }
        return -1;
    }


    class ServiceDiscovery {
        protected Map<String, List<Integer>> serviceDiscoveryInfo = new HashMap<>();
        int deviceId ;

        public ServiceDiscovery(Integer deviceId) {
            this.deviceId =deviceId;
        }

        public void addServiceDIscoveryInfo(String microservice, Integer device) {
            if (serviceDiscoveryInfo.containsKey(microservice)) {
                List<Integer> deviceList = serviceDiscoveryInfo.get(microservice);
                deviceList.add(device);
                serviceDiscoveryInfo.put(microservice, deviceList);
            } else {
                List<Integer> deviceList = new ArrayList<>();
                deviceList.add(device);
                serviceDiscoveryInfo.put(microservice, deviceList);
            }
        }

        public Map<String, List<Integer>> getServiceDiscoveryInfo() {
            return serviceDiscoveryInfo;
        }

        public void removeServiceDIscoveryInfo(String microserviceName, Integer deviceID) {
            if (serviceDiscoveryInfo.containsKey(microserviceName) && serviceDiscoveryInfo.get(microserviceName).contains(new Integer(deviceID))) {
                System.out.println("Service Discovery Info REMOVED (device:" + this.deviceId + ") for microservice :" + microserviceName + " , destDevice : " + deviceID);
                serviceDiscoveryInfo.get(microserviceName).remove(new Integer(deviceID));
                if (serviceDiscoveryInfo.get(microserviceName).size() == 0)
                    serviceDiscoveryInfo.remove(microserviceName);
            }
        }



//    class ServiceDiscovery {
//        protected Map<String, List<Map<String, List<Integer>>>> serviceDiscoveryInfo = new HashMap<>(); //Map<MsTypeName,List<Map<MsName, List<DeviceId>>>>
//        int deviceId;
//
//        public ServiceDiscovery(Integer deviceId) {
//            this.deviceId = deviceId;
//        }
//
//    //    public void addServiceDIscoveryInfo(String microservice, Integer device) {
//    //        if (serviceDiscoveryInfo.containsKey(microservice)) {
//    //            List<Integer> deviceList = serviceDiscoveryInfo.get(microservice);
//    //            deviceList.add(device);
//    //            serviceDiscoveryInfo.put(microservice, deviceList);
//    //        } else {
//    //            List<Integer> deviceList = new ArrayList<>();
//    //            deviceList.add(device);
//    //            serviceDiscoveryInfo.put(microservice, deviceList);
//    //        }
//    //    }
//
//    //    public void addServiceDIscoveryInfo(String microservice, Integer device) {
//    //
//    //        String msType = microservice.substring(0, microservice.length() - 2);
//    //
//    //        if (serviceDiscoveryInfo.containsKey(msType)) {
//    //            List<Map<String, List<Integer>>> listOfInstanceToDeviceList = serviceDiscoveryInfo.get(msType);
//    //
//    //            for (Map<String, List<Integer>> instanceToDeviceList: listOfInstanceToDeviceList) {
//    //                if (instanceToDeviceList.containsKey(microservice)){
//    //                    List<Integer> deviceList = instanceToDeviceList.get(microservice);
//    //                    deviceList.add(device);
//    //                    instanceToDeviceList.put(microservice, deviceList);
//    //                }
//    //            }
//    //
//    //            // Update listOfInstanceToDeviceList
//    //            // update serviceDiscoveryInfo
//    //
//    //        } else {
//    //            List<Integer> deviceList = new ArrayList<>();
//    //            deviceList.add(device);
//    //            Map<String, List<Integer>> instanceToDeviceList = new HashMap<>();
//    //            instanceToDeviceList.put(microservice, deviceList);
//    //            serviceDiscoveryInfo.put(msType, instanceToDeviceList);
//    //        }
//    //    }
//
//        public void addServiceDIscoveryInfo(String msName, int deviceId) {
//
//            if (msName.equals("Resource_Event_Analysis_Microservice_2"))
//            {
//                System.out.println("Found");
//            }
//
//            // Derive msType from msName by removing the last two characters
//            String msType = msName.substring(0, msName.length() - 2);
//
//
//
//            // Get the list of maps for the msType, or create a new one if it doesn't exist
//            List<Map<String, List<Integer>>> msTypeList = serviceDiscoveryInfo.getOrDefault(msType, new ArrayList<>());
//
//            // Flag to check if the msName was found
//            boolean msNameFound = false;
//
//            // Iterate through the list of maps
//            for (Map<String, List<Integer>> msMap : msTypeList) {
//                if (msMap.containsKey(msName)) {
//                    // If msName is found, add the device ID to the list
//                    List<Integer> deviceList = msMap.get(msName);
//                    if (!deviceList.contains(deviceId)) {
//                        deviceList.add(deviceId);
//                    }
//                    msNameFound = true;
//                    break;
//                }
//            }
//
//            // If msName was not found, create a new entry
//            if (!msNameFound) {
//                Map<String, List<Integer>> newMsMap = new HashMap<>();
//                List<Integer> newDeviceList = new ArrayList<>();
//                newDeviceList.add(deviceId);
//                newMsMap.put(msName, newDeviceList);
//                msTypeList.add(newMsMap);
//            }
//
//            // Put the updated list back into the serviceDiscoveryInfo map
//            serviceDiscoveryInfo.put(msType, msTypeList);
//        }
//
//
//        public Map<String, List<Map<String, List<Integer>>>> getServiceDiscoveryInfo() {
//            return serviceDiscoveryInfo;
//        }
//
//        //    public void removeServiceDIscoveryInfo(String microserviceName, int deviceID) {
//        //
//        //        String msType = microserviceName.substring(0, microserviceName.length() - 2);
//        //
//        //        if (serviceDiscoveryInfo.containsKey(msType) && serviceDiscoveryInfo.get(msType).get(microserviceName).contains(deviceID)) {
//        //            System.out.println("Service Discovery Info REMOVED (device:" + this.deviceId + ") for microservice :" + microserviceName + " , destDevice : " + deviceID);
//        //            serviceDiscoveryInfo.get(msType).get(microserviceName).remove(deviceID);
//        //            if (serviceDiscoveryInfo.get(msType).get(microserviceName).size() == 0)
//        //                serviceDiscoveryInfo.get(msType).remove(microserviceName);
//        //        }
//        //    }
//
//
//        public void removeServiceDIscoveryInfo(String microserviceName, int deviceID) {
//            // Derive msType from microserviceName by removing the last two characters
//            String msType = microserviceName.substring(0, microserviceName.length() - 2);
//
//            // Get the list of maps for the msType
//            List<Map<String, List<Integer>>> msTypeList = serviceDiscoveryInfo.get(msType);
//
//            // If no such msType exists, return early
//            if (msTypeList == null) {
//                System.out.println("No such microservice type found.");
//                return;
//            }
//
//            // Iterator for safely removing elements from the list
//            Iterator<Map<String, List<Integer>>> msTypeIterator = msTypeList.iterator();
//
//            while (msTypeIterator.hasNext()) {
//                Map<String, List<Integer>> msMap = msTypeIterator.next();
//
//                // Check if the current map contains the microserviceName
//                if (msMap.containsKey(microserviceName)) {
//                    List<Integer> deviceList = msMap.get(microserviceName);
//
//                    // Remove the deviceID if it exists in the list
//                    deviceList.remove((Integer) deviceID); // Cast to Integer to avoid ambiguity with remove(int index)
//
//                    // If the list is empty after removal, remove the entry for microserviceName
//                    if (deviceList.isEmpty()) {
//                        msMap.remove(microserviceName);
//                    }
//
//                    // If the map is empty after removing microserviceName, remove the map from msTypeList
//                    if (msMap.isEmpty()) {
//                        msTypeIterator.remove();
//                    }
//
//                    break; // Microservice name found and processed, break out of the loop
//                }
//            }
//
//            // If the msTypeList is empty after the removal, remove the msType from the main map
//            if (msTypeList.isEmpty()) {
//                serviceDiscoveryInfo.remove(msType);
//            }
//        }
//
//
//    //    public void removeServiceDIscoveryInfo(String microserviceName, Integer deviceID) {
//    //        String msType = microserviceName.substring(0, microserviceName.length() - 2);
//    //        if (serviceDiscoveryInfo.containsKey(msType) && serviceDiscoveryInfo.get(msType).contains(new Integer(deviceID))) {
//    //            System.out.println("Service Discovery Info REMOVED (device:" + this.deviceId + ") for microservice :" + msType + " , destDevice : " + deviceID);
//    //            serviceDiscoveryInfo.get(msType).remove(new Integer(deviceID));
//    //            if (serviceDiscoveryInfo.get(msType).size() == 0)
//    //                serviceDiscoveryInfo.remove(msType);
//    //        }
//    //    }
//    }
}
}









