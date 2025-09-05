package dynamicOptimisation.components;

import com.sun.source.tree.DoWhileLoopTree;
import dynamicOptimisation.utils.EntityName.MicroserviceName;
import org.apache.commons.collections4.functors.WhileClosure;
import org.apache.commons.math3.util.Pair;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.application.AppEdge;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.entities.*;
import org.fog.placement.MicroservicePlacementLogic;
import org.fog.placement.PlacementLogicOutput;
import org.fog.utils.FogEvents;
import org.fog.utils.MicroservicePlacementConfig;
import org.fog.utils.ModuleLaunchConfig;
import dynamicOptimisation.NSGAII.PlacementAlgorithm;
import dynamicOptimisation.model.Microservice;
import dynamicOptimisation.utils.SimulationParameters;
import org.json.simple.JSONObject;

import java.util.*;


public class CloudFogMicroservicePlacementOptimise implements MicroservicePlacementLogic {

    public List<FogDevice> fogDevices; //fog devices considered by FON for placements of requests, should include all the
    // Citizen Fog devices
    List<MicroserviceFogDevice> cfList = new ArrayList<>();
    List<PlacementRequest> placementRequests; // requests to be processed, mainly the gateways to place the sensor
    // modules.
    protected Map<Integer, Map<String, Double>> resourceAvailability;
    private Map<String, Application> applicationInfo = new HashMap<>();
    private Map<String, String> moduleToApp = new HashMap<>();
    Map<Integer, Map<String, Integer>> mappedMicroservices = new HashMap<>();
    public Map<String, List<Integer>> microserviceToDeviceListMap = new HashMap<>();
    List<Milestone> milestoneList = new ArrayList<>();

    int fonID;

    protected Map<Integer, Double> currentRAMLoad;
    protected Map<Integer, Double> currentStorageLoad;
    protected Map<Integer, Double> currentCpuLoad;
    protected Map<Integer, Double> currentBWLoad;
    protected Map<Integer, List<String>> currentModuleMap = new HashMap<>();
    protected Map<Integer, Map<String, Double>> currentModuleLoadMap = new HashMap<>();
    protected Map<Integer, Map<String, Integer>> currentModuleInstanceNum = new HashMap<>();

    protected Map<PlacementRequest, Integer> prStatus = new HashMap<>();

    int failedAttempt = 0;
    public List<Microservice> failedToPlace = new ArrayList<>();


    public CloudFogMicroservicePlacementOptimise(int fonID) {
        setFONId(fonID);
    }

    public void setFONId(int id) {
        fonID = id;
    }

    public int getFonID() {
        return fonID;
    }

    @Override
    public PlacementLogicOutput run(List<FogDevice> fogDevices, Map<String, Application> applicationInfo, Map<Integer, Map<String, Double>> resourceAvailability, List<PlacementRequest> pr) {

        this.fogDevices = fogDevices; // only consists of current device
        this.placementRequests = pr;
        this.resourceAvailability = resourceAvailability;
        this.applicationInfo = applicationInfo;
        setCFListFrom(fogDevices);


        setCurrentRAMLoad(new HashMap<Integer, Double>());
        setCurrentStorageLoad(new HashMap<Integer, Double>());
        setCurrentCpuLoad(new HashMap<Integer, Double>());
        setCurrentBWLoad(new HashMap<Integer, Double>());
        setCurrentModuleMap(new HashMap<>());
        for (FogDevice dev : fogDevices) {
            getCurrentRAMLoad().put(dev.getId(), 0.0);
            getCurrentStorageLoad().put(dev.getId(), 0.0);
            getCurrentCpuLoad().put(dev.getId(), 0.0);
            getCurrentBWLoad().put(dev.getId(), 0.0);
            getCurrentModuleMap().put(dev.getId(), new ArrayList<>());
            currentModuleLoadMap.put(dev.getId(), new HashMap<String, Double>());
            currentModuleInstanceNum.put(dev.getId(), new HashMap<String, Integer>());
        }

        mapModules();
        PlacementLogicOutput placement = generatePlacementMap();
        updateResources(resourceAvailability);
        postProcessing();
        return placement;

    }

    private void setCFListFrom(List<FogDevice> fogDevices) {
        for (FogDevice fogDevice : fogDevices) {
            if (fogDevice.getName().contains("CF-")) {
                MicroserviceFogDevice microserviceFogDevice = (MicroserviceFogDevice) fogDevice;
                cfList.add(microserviceFogDevice);
            }
        }
    }

    private void mapModules() {

        //Milestone milestone = (Milestone) applicationInfo.get(placementRequests.get(0).getApplicationId());

        for (String appId : applicationInfo.keySet()) {
            milestoneList.add((Milestone) applicationInfo.get(appId));
        }


        for (Milestone milestone : milestoneList) {
            /*
                Add the special placement info
                Map<String, List<String>> specialPlacementInfo -> Microservice to device name list.
             */
//            for (String msName : milestone.getSpecialPlacementInfo().keySet()) {
//                List<String> placedDeviceNameList = milestone.getSpecialPlacementInfo().get(msName);
//                for (String deviceName : placedDeviceNameList) {
//                    int deviceId = getDeviceByName(deviceName).getId();
//                    if (!microserviceToDeviceListMap.keySet().contains(msName)
//                            || microserviceToDeviceListMap.get(msName) == null) {
//                        List<Integer> deviceList = new ArrayList<>();
//                        deviceList.add(deviceId);
//                        microserviceToDeviceListMap.put(msName, deviceList);
//                    } else {
//                        List<Integer> deviceList = microserviceToDeviceListMap.get(msName);
//                        if (!deviceList.contains(deviceId)) {
//                            deviceList.add(deviceId);
//                            microserviceToDeviceListMap.put(msName, deviceList);
//                        }
//                    }
//                }
//                moduleToApp.put(msName, milestone.getAppId());
//            }


            for (String msName : milestone.getSpecialPlacementInfo().keySet()) {
                Microservice microservice = getModule(msName, milestone);
                List<String> placedDeviceNameList = milestone.getSpecialPlacementInfo().get(msName);
                for (String deviceName : placedDeviceNameList) {


                    FogDevice fogDevice = getDeviceByName(deviceName);


                    do {
                        int deviceId = fogDevice.getId();
                        if (!currentModuleInstanceNum.get(deviceId).containsKey(msName)) {
                            currentModuleInstanceNum.get(deviceId).put(msName, 1);
                        } else {
                            currentModuleInstanceNum.get(deviceId).put(msName, currentModuleInstanceNum.get(deviceId).get(msName) + 1);
                        }

                        if (!microserviceToDeviceListMap.containsKey(msName)
                                || microserviceToDeviceListMap.get(msName) == null) {
                            List<Integer> deviceList = new ArrayList<>();
                            deviceList.add(deviceId);
                            microserviceToDeviceListMap.put(msName, deviceList);
                        } else {
                            List<Integer> deviceList = microserviceToDeviceListMap.get(msName);
                            if (!deviceList.contains(deviceId)) {
                                deviceList.add(deviceId);
                                microserviceToDeviceListMap.put(msName, deviceList);
                            }
                        }

                        getCurrentRAMLoad().put(fogDevice.getId(),
                                microservice.getRam() + getCurrentRAMLoad().get(fogDevice.getId()));
                        getCurrentStorageLoad().put(fogDevice.getId(),
                                microservice.getSize() + getCurrentStorageLoad().get(fogDevice.getId()));
                        getCurrentCpuLoad().put(fogDevice.getId(), microservice.getMips() + getCurrentCpuLoad().get(fogDevice.getId()));
                        getCurrentBWLoad().put(fogDevice.getId(),
                                microservice.getBw() + getCurrentBWLoad().get(fogDevice.getId()));

                        if (!Objects.equals(deviceName, "cloud")) {
                            System.out.println("Microservice: " + microservice.getName() + " placed in device: " + fogDevice.getName());
                        }
                    } while  (deviceCanPlace(microservice, fogDevice));


                }
                moduleToApp.put(msName, milestone.getAppId());
            }

//            for (String msName : milestone.getSpecialPlacementInfo().keySet()) {
//                List<String> placedDeviceNameList = milestone.getSpecialPlacementInfo().get(msName);
//                for (String deviceName : placedDeviceNameList) {
//                    int deviceId = getDeviceByName(deviceName).getId();
//                    if (!microserviceToDeviceListMap.keySet().contains(msName)
//                            || microserviceToDeviceListMap.get(msName) == null) {
//                        List<Integer> deviceList = new ArrayList<>();
//                        deviceList.add(deviceId);
//                        microserviceToDeviceListMap.put(msName, deviceList);
//                    } else {
//                        List<Integer> deviceList = microserviceToDeviceListMap.get(msName);
//                        if (!deviceList.contains(deviceId)) {
//                            deviceList.add(deviceId);
//                            microserviceToDeviceListMap.put(msName, deviceList);
//                        }
//                    }
//                }
//                moduleToApp.put(msName, milestone.getAppId());
//            }


            // Mapping between the CFs and MS to place In CF. NSGA-II will be applied here.

            List<Microservice> microserviceList = new ArrayList<>();
            for (String msName : milestone.toPlaceInCFs) {
                Microservice microservice = getModule(msName, milestone);
                microserviceList.add(microservice);
            }

            System.out.println("\n\n========NSGA-II Start=====================");

            PlacementAlgorithm placementAlgorithm = new PlacementAlgorithm();
            placementAlgorithm.PlacementMapper(this, milestone);
            placementAlgorithm.setDeviceList(cfList);
            placementAlgorithm.setMicroserviceList(microserviceList);
            placementAlgorithm.initiateTheAlgo();

            System.out.println(SimulationParameters.finalParetoFront.get(0).toString());

            System.out.println("========NSGA-II End=====================\n\n");

            System.out.println("\n\n========Vertical Placement Start=====================");

            Map<String, Integer> microserviceToDeviceMapping =
                    SimulationParameters.finalParetoFront.get(0).microserviceToDeviceMapping;

            for (String msName: microserviceToDeviceMapping.keySet()){

                Microservice microservice = getModule(msName, milestone);
                int deviceId = microserviceToDeviceMapping.get(msName);
                MicroserviceFogDevice fogDevice = (MicroserviceFogDevice) getDevice(deviceId);
                if (deviceCanPlace(microservice, fogDevice)) {
                    if (!microserviceToDeviceListMap.containsKey(msName)
                            || microserviceToDeviceListMap.get(msName) == null) {
                        List<Integer> deviceList = new ArrayList<>();
                        deviceList.add(deviceId);
                        microserviceToDeviceListMap.put(msName, deviceList);
                    } else {
                        List<Integer> deviceList = microserviceToDeviceListMap.get(msName);
                        if (!deviceList.contains(deviceId)) {
                            deviceList.add(deviceId);
                            microserviceToDeviceListMap.put(msName, deviceList);
                        }
                    }

                    getCurrentRAMLoad().put(fogDevice.getId(),
                            microservice.getRam() + getCurrentRAMLoad().get(fogDevice.getId()));
                    getCurrentStorageLoad().put(fogDevice.getId(),
                            microservice.getSize() + getCurrentStorageLoad().get(fogDevice.getId()));
                    getCurrentCpuLoad().put(fogDevice.getId(), microservice.getMips() + getCurrentCpuLoad().get(fogDevice.getId()));
                    getCurrentBWLoad().put(fogDevice.getId(),
                            microservice.getBw() + getCurrentBWLoad().get(fogDevice.getId()));
                    moduleToApp.put(msName, milestone.getAppId());

                    System.out.println("Microservice: " + microservice.getName() + " placed in device: " + fogDevice.getName());
                }
            }

            System.out.println("\n\n========Vertical Placement End=====================");

            System.out.println("\n\n========Horizontal Scaling Start=====================");

//            for (MicroserviceFogDevice fogDevice: cfList){
//                int deviceId = fogDevice.getId();
//                for (String msName : milestone.toPlaceInCFs) {
//                    Microservice microservice = getModule(msName, milestone);
//                    if (deviceCanPlace(microservice, fogDevice)) {
//                        if (!microserviceToDeviceListMap.containsKey(msName)
//                                || microserviceToDeviceListMap.get(msName) == null) {
//                            List<Integer> deviceList = new ArrayList<>();
//                            deviceList.add(deviceId);
//                            microserviceToDeviceListMap.put(msName, deviceList);
//                        } else {
//                            List<Integer> deviceList = microserviceToDeviceListMap.get(msName);
//                            if (!deviceList.contains(deviceId)) {
//                                deviceList.add(deviceId);
//                                microserviceToDeviceListMap.put(msName, deviceList);
//                            }
//                        }
//
//                        getCurrentRAMLoad().put(fogDevice.getId(),
//                                microservice.getRam() + getCurrentRAMLoad().get(fogDevice.getId()));
//                        getCurrentStorageLoad().put(fogDevice.getId(),
//                                microservice.getSize() + getCurrentStorageLoad().get(fogDevice.getId()));
//                        getCurrentCpuLoad().put(fogDevice.getId(), microservice.getMips() + getCurrentCpuLoad().get(fogDevice.getId()));
//                        getCurrentBWLoad().put(fogDevice.getId(),
//                                microservice.getBw() + getCurrentBWLoad().get(fogDevice.getId()));
//                        moduleToApp.put(msName, milestone.getAppId());
//                        System.out.println("Microservice: " + microservice.getName() + " placed in device: " + fogDevice.getName());
//
//                        //break;
//                    }
//                }
//
//            }


//                    Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-3 for 2 times. 1
//                    Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-4 for 3 times. 3
//                    Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-0 for 20 times. 16
//                    Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-1 for 23 times. 18
//                    Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-2 for 25 times. 20

            //scaleUpMicroservice("Site_Anomaly_Detection_Microservice_1", 2, milestone);
            //scaleUpMicroservice("Resource_Event_Analysis_Microservice_1", 3, milestone);
            //scaleUpMicroservice("Resource_Interface_Microservice_1", 3, milestone);
          //  scaleUpMicroservice("Monitoring_Work_Order_Microservice_1", 4, milestone);
          //  scaleUpMicroservice(MicroserviceName.NOTIFICATION_MODULE_1, 4, milestone);


            List<String> notPlacedMS = new ArrayList<>(milestone.toPlaceInCFs); // Create a copy of the list to avoid modifying the original during iteration

            for (MicroserviceFogDevice fogDevice : cfList) {
                int deviceId = fogDevice.getId();
                Iterator<String> iterator = notPlacedMS.iterator(); // Use an iterator to safely remove elements
                while (iterator.hasNext()) {
                    String msName = iterator.next();
                        Microservice microservice = getModule(msName, milestone);
                    if (deviceCanPlace(microservice, fogDevice)) {
                        if (!microserviceToDeviceListMap.containsKey(msName) || microserviceToDeviceListMap.get(msName) == null) {
                            List<Integer> deviceList = new ArrayList<>();
                            deviceList.add(deviceId);
                            microserviceToDeviceListMap.put(msName, deviceList);
                        } else {
                            List<Integer> deviceList = microserviceToDeviceListMap.get(msName);
                            if (!deviceList.contains(deviceId)) {
                                deviceList.add(deviceId);
                                microserviceToDeviceListMap.put(msName, deviceList);
                            }
                        }

                        getCurrentRAMLoad().put(fogDevice.getId(), microservice.getRam() + getCurrentRAMLoad().get(fogDevice.getId()));
                        getCurrentStorageLoad().put(fogDevice.getId(), microservice.getSize() + getCurrentStorageLoad().get(fogDevice.getId()));
                        getCurrentCpuLoad().put(fogDevice.getId(), microservice.getMips() + getCurrentCpuLoad().get(fogDevice.getId()));
                        getCurrentBWLoad().put(fogDevice.getId(), microservice.getBw() + getCurrentBWLoad().get(fogDevice.getId()));
                        moduleToApp.put(msName, milestone.getAppId());
                        System.out.println("Microservice: " + microservice.getName() + " placed in device: " + fogDevice.getName());

                        iterator.remove(); // Safely remove the element using the iterator
                        if (notPlacedMS.isEmpty()) {
                            notPlacedMS = new ArrayList<>(milestone.toPlaceInCFs); // Reset the list if it's empty
                            iterator = notPlacedMS.iterator();
                        }
                    }
                }
                 //Create a separate list for items to be removed
                List<String> itemsToRemove = new ArrayList<>();
                for (String msName : notPlacedMS) {
                    if (msName.endsWith("_2")) {
                        itemsToRemove.add(msName);
                    }
                }

 //Remove items with "_2" if their removal doesn't leave the list empty
                if (notPlacedMS.size() - itemsToRemove.size() > 0) {
                    notPlacedMS.removeAll(itemsToRemove);
                }
            }





//            for (String msName : milestone.toPlaceInCFs) {
//                Microservice microservice = getModule(msName, milestone);
//                for (MicroserviceFogDevice fogDevice: cfList){
//                    int deviceId = fogDevice.getId();
//                    if (deviceCanPlace(microservice, fogDevice)) {
//                        if (!microserviceToDeviceListMap.containsKey(msName)
//                                || microserviceToDeviceListMap.get(msName) == null) {
//                            List<Integer> deviceList = new ArrayList<>();
//                            deviceList.add(deviceId);
//                            microserviceToDeviceListMap.put(msName, deviceList);
//                        } else {
//                            List<Integer> deviceList = microserviceToDeviceListMap.get(msName);
//                            if (!deviceList.contains(deviceId)) {
//                                deviceList.add(deviceId);
//                                microserviceToDeviceListMap.put(msName, deviceList);
//                            }
//                        }
//
//                        getCurrentRAMLoad().put(fogDevice.getId(),
//                                microservice.getRam() + getCurrentRAMLoad().get(fogDevice.getId()));
//                        getCurrentStorageLoad().put(fogDevice.getId(),
//                                microservice.getSize() + getCurrentStorageLoad().get(fogDevice.getId()));
//                        getCurrentCpuLoad().put(fogDevice.getId(), microservice.getMips() + getCurrentCpuLoad().get(fogDevice.getId()));
//                        getCurrentBWLoad().put(fogDevice.getId(),
//                                microservice.getBw() + getCurrentBWLoad().get(fogDevice.getId()));
//                        moduleToApp.put(msName, milestone.getAppId());
//                        System.out.println("Microservice: " + microservice.getName() + " placed in device: " + fogDevice.getName());
//
//                        break;
//                    }
//
//                }
//            }


            System.out.println("\n\n========Horizontal Scaling End=====================");


//            List<String> tempToPlaceInCFs = new ArrayList<>();//milestone.toPlaceInCFs;
//
//            for (MicroserviceFogDevice fogDevice : cfList) {
//                List<String> consideredCF = new ArrayList<>();
//                if (tempToPlaceInCFs.isEmpty()) {
//                    tempToPlaceInCFs.addAll(milestone.toPlaceInCFs);
//                }
//                for (String msName : tempToPlaceInCFs) {
//                    Microservice microservice = getModule(msName, milestone);
//                    if (deviceCanPlace(microservice, fogDevice)) {
//                        int deviceId = fogDevice.getId();
//                        if (!microserviceToDeviceListMap.keySet().contains(msName)
//                                || microserviceToDeviceListMap.get(msName) == null) {
//                            List<Integer> deviceList = new ArrayList<>();
//                            deviceList.add(deviceId);
//                            microserviceToDeviceListMap.put(msName, deviceList);
//                        } else {
//                            List<Integer> deviceList = microserviceToDeviceListMap.get(msName);
//                            if (!deviceList.contains(deviceId)) {
//                                deviceList.add(deviceId);
//                                microserviceToDeviceListMap.put(msName, deviceList);
//                            }
//                        }
//
////                        if (!currentModuleMap.get(deviceId).contains(msName))
////                            currentModuleMap.get(deviceId).add(msName);
////
////                        if (!currentModuleInstanceNum.get(deviceId).containsKey(msName)) {
////                            currentModuleInstanceNum.get(deviceId).put(msName, 1);
////                        } else {
////                            currentModuleInstanceNum.get(deviceId).put(msName, currentModuleInstanceNum.get(deviceId).get(msName) + 1);
////                        }
//
//                        consideredCF.add(msName);
//                        getCurrentRAMLoad().put(fogDevice.getId(),
//                                microservice.getRam() + getCurrentRAMLoad().get(fogDevice.getId()));
//                        getCurrentStorageLoad().put(fogDevice.getId(),
//                                microservice.getSize() + getCurrentStorageLoad().get(fogDevice.getId()));
//                        getCurrentCpuLoad().put(fogDevice.getId(), microservice.getMips() + getCurrentCpuLoad().get(fogDevice.getId()));
//                        getCurrentBWLoad().put(fogDevice.getId(),
//                                microservice.getBw() + getCurrentBWLoad().get(fogDevice.getId()));
//                    }
//                    moduleToApp.put(msName, milestone.getAppId());
//                }
//
//                for (String consideredMsName : consideredCF) {
//                    tempToPlaceInCFs.remove(consideredMsName);
//                }
//
//            }




        }

        // System.out.println(currentModuleInstanceNum);

        for (String msName : microserviceToDeviceListMap.keySet()) {
            List<Integer> deviceList = microserviceToDeviceListMap.get(msName);
            for (int deviceId : deviceList) {
//                Map<String, Integer> placedMicroservices = new HashMap<>();
//                placedMicroservices.put(msName, deviceId);
                Milestone milestone = milestoneList.get(0);
                moduleToApp.put(msName, milestone.getAppId());
                if (!currentModuleMap.get(deviceId).contains(msName))
                    currentModuleMap.get(deviceId).add(msName);
                if (!currentModuleInstanceNum.get(deviceId).containsKey(msName)) {
                    currentModuleInstanceNum.get(deviceId).put(msName, 1);
                }
                else {
                   // if ((msName.contains("Resource_Event") || msName.contains("Resource_Interface")) && (currentModuleInstanceNum.get(deviceId).get(msName) < 2)) {
                        currentModuleInstanceNum.get(deviceId).put(msName, currentModuleInstanceNum.get(deviceId).get(msName) + 1);
                   // }
                }
            }
        }


    }

    private void scaleUpMicroservice(String msName, int instanceLimit, Milestone milestone) {

        Microservice microservice = getModule(msName, milestone);
        int currentInstanceNumber = 0;

        // Outer loop to keep track of how many instances have been placed
        while (currentInstanceNumber < instanceLimit) {
            boolean instancePlaced = false;
            int devicesCheckedWithoutPlacement = 0;

            for (MicroserviceFogDevice fogDevice : cfList) {
                int deviceId = fogDevice.getId();

                if (deviceCanPlace(microservice, fogDevice)) {
                    if (!microserviceToDeviceListMap.containsKey(msName) || microserviceToDeviceListMap.get(msName) == null) {
                        List<Integer> deviceList = new ArrayList<>();
                        deviceList.add(deviceId);
                        microserviceToDeviceListMap.put(msName, deviceList);
                    } else {
                        List<Integer> deviceList = microserviceToDeviceListMap.get(msName);
                        if (!deviceList.contains(deviceId)) {
                            deviceList.add(deviceId);
                            microserviceToDeviceListMap.put(msName, deviceList);
                        }
                    }

                    getCurrentRAMLoad().put(fogDevice.getId(), microservice.getRam() + getCurrentRAMLoad().get(fogDevice.getId()));
                    getCurrentStorageLoad().put(fogDevice.getId(), microservice.getSize() + getCurrentStorageLoad().get(fogDevice.getId()));
                    getCurrentCpuLoad().put(fogDevice.getId(), microservice.getMips() + getCurrentCpuLoad().get(fogDevice.getId()));
                    getCurrentBWLoad().put(fogDevice.getId(), microservice.getBw() + getCurrentBWLoad().get(fogDevice.getId()));
                    moduleToApp.put(msName, milestone.getAppId());
                    System.out.println("Microservice: " + microservice.getName() + " placed in device: " + fogDevice.getName());

                    currentInstanceNumber++;
                    instancePlaced = true;

                    // Break inner loop if the instance limit is reached
                    if (currentInstanceNumber >= instanceLimit) {
                        break;
                    }
                } else {
                    // If the device couldn't place the microservice, increment the counter
                    devicesCheckedWithoutPlacement++;
                }

                // If all devices have been checked without placing an instance, break the loop
                if (devicesCheckedWithoutPlacement >= cfList.size()) {
                    System.out.println("No available resources left to place the microservice: " + msName);
                    break;
                }
            }

            // Break outer loop if the instance limit is reached or no placement occurred in this iteration
            if (currentInstanceNumber >= instanceLimit || !instancePlaced) {
                break;
            }
        }
    }




    private PlacementLogicOutput generatePlacementMap() {

        Map<Integer, Map<Application, List<ModuleLaunchConfig>>> perDevice = new HashMap<>();
        Map<Integer, List<Pair<String, Integer>>> serviceDiscoveryInfo = new HashMap<>();
        Milestone milestone = milestoneList.get(0);

        for (int deviceId : currentModuleInstanceNum.keySet()) {
            for (String microservice : currentModuleInstanceNum.get(deviceId).keySet()) {
                AppModule appModule = new AppModule(milestone.getModuleByName(microservice));
                ModuleLaunchConfig moduleLaunchConfig = new ModuleLaunchConfig(appModule, currentModuleInstanceNum.get(deviceId).get(microservice));
                if (perDevice.keySet().contains(deviceId)) {
                    if (perDevice.get(deviceId).containsKey(milestone)) {
                        perDevice.get(deviceId).get(milestone).add(moduleLaunchConfig);
                    } else {
                        List<ModuleLaunchConfig> l = new ArrayList<>();
                        l.add(moduleLaunchConfig);
                        perDevice.get(deviceId).put(milestone, l);
                    }
                } else {
                    List<ModuleLaunchConfig> l = new ArrayList<>();
                    l.add(moduleLaunchConfig);
                    HashMap<Application, List<ModuleLaunchConfig>> m = new HashMap<>();
                    m.put(milestone, l);
                    perDevice.put(deviceId, m);
                }
            }
        }


        for (String msName : microserviceToDeviceListMap.keySet()) {
            List<Integer> placedDeviceList = microserviceToDeviceListMap.get(msName);
            for (int deviceID : placedDeviceList) {

//                System.out.println("------------Primary service discovery Start-------------------");
//
//                if (msName.equals("Resource_Event_Analysis_Microservice_2"))
//                {
//                    System.out.println("Found");
//                }


                if (serviceDiscoveryInfo.containsKey(deviceID))
                    serviceDiscoveryInfo.get(deviceID).add(new Pair<>(msName, deviceID));
                else {
                    List<Pair<String, Integer>> s = new ArrayList<>();
                    s.add(new Pair<>(msName, deviceID));
                    serviceDiscoveryInfo.put(deviceID, s);
                }

              //  System.out.println("------------Primary service discovery End-------------------");

                List<Integer> clientDevices = getClientServiceNodeIds(milestone, msName);
//                System.out.println("For the Microservice: " + msName + " Placed on device: " + deviceID + " " +
//                        "Found client devices: " + Arrays.toString(clientDevices.toArray()));
                for (int clientDevice : clientDevices) {

                    // Actually what we are doing here, is if I am the destination module M-2, placed in D-2 and my
                    // source (client modules) modules are M-0, and M-1 running in D-0, and D-1 respectively, I
                    // am saving myself and my device Id for each of D-0 and D-1.
                    // That is Discovery(D1) = {(M-2, D-2)}. If M-2 was also placed in D-4,
                    // Discovery(D1) = {(M-2, D-2), (M-2, D-4)}. Or If, M-1 running in D-1 was source for another
                    // Module, say M-3 running in D-1, Discovery(D1) = {(M-2, D-2), (M-2, D-2), (M-3, D-1)}

                    if (serviceDiscoveryInfo.containsKey(clientDevice))
                        serviceDiscoveryInfo.get(clientDevice).add(new Pair<>(msName, deviceID));
                    else {
                        List<Pair<String, Integer>> s = new ArrayList<>();
                        s.add(new Pair<>(msName, deviceID));
                        serviceDiscoveryInfo.put(clientDevice, s);
                    }


                }
            }
        }

        return new PlacementLogicOutput(perDevice, serviceDiscoveryInfo, prStatus);
    }


    private void performTempPlacement(Map<Integer, Map<Application, List<ModuleLaunchConfig>>> perDevice, MicroserviceFogDevice fonDevice) {

        int fogDeviceCount = 0;
        for (int deviceID : perDevice.keySet()) {
            MicroserviceFogDevice f = (MicroserviceFogDevice) CloudSim.getEntity(deviceID);
            if (!f.getDeviceType().equals(MicroserviceFogDevice.CLOUD))
                fogDeviceCount++;
            for (Application app : perDevice.get(deviceID).keySet()) {
                if (MicroservicePlacementConfig.SIMULATION_MODE == "STATIC") {
                    //ACTIVE_APP_UPDATE
                    fonDevice.sendNow(deviceID, FogEvents.ACTIVE_APP_UPDATE, app);
                    //APP_SUBMIT
                    fonDevice.sendNow(deviceID, FogEvents.APP_SUBMIT, app);
                    for (ModuleLaunchConfig moduleLaunchConfig : perDevice.get(deviceID).get(app)) {
                        String microserviceName = moduleLaunchConfig.getModule().getName();
                        //LAUNCH_MODULE
                        fonDevice.sendNow(deviceID, FogEvents.LAUNCH_MODULE,
                                new AppModule(app.getModuleByName(microserviceName)));
                        fonDevice.sendNow(deviceID, FogEvents.LAUNCH_MODULE_INSTANCE, moduleLaunchConfig);
                    }
                }
            }
        }

    }


    private boolean deviceCanPlace(Microservice microservice, FogDevice fogDevice) {


        boolean isRAM = microservice.getRam() + getCurrentRAMLoad().get(fogDevice.getId()) <= resourceAvailability.get(fogDevice.getId()).get(ControllerComponent.RAM);
        boolean isStorage =
                microservice.getSize() + getCurrentStorageLoad().get(fogDevice.getId()) <= resourceAvailability.get(fogDevice.getId()).get(ControllerComponent.STORAGE);
        boolean isCPU =
                microservice.getMips() + getCurrentCpuLoad().get(fogDevice.getId()) <= resourceAvailability.get(fogDevice.getId()).get(ControllerComponent.CPU);
        boolean iSBW =
                microservice.getBw() + getCurrentBWLoad().get(fogDevice.getId()) <= resourceAvailability.get(fogDevice.getId()).get(ControllerComponent.BW);

        if (!isRAM) {
             //System.out.println("Microservice: " + microservice.getName() + " could not place in device: " +
            // fogDevice.getName() + " due to RAM unavailability");
            return false;
        }
        if (!isStorage) {
            // System.out.println("Microservice: " + microservice.getName() + " could not place in device: " +
            // fogDevice.getName() + " due to Storage unavailability");
            return false;
        }
        if (!isCPU) {
             //  System.out.println("Microservice: " + microservice.getName() + " could not place in device: " +
            //  fogDevice.getName() + " due to CPU unavailability");
            return false;
        }
        if (!iSBW) {
           //  System.out.println("Microservice: " + microservice.getName() + " could not place in device: " +
            //  fogDevice.getName() + " due to BW unavailability");
            return false;
        }

        return true;

//        return microservice.getRam() + getCurrentRAMLoad().get(fogDevice.getId()) <= resourceAvailability.get(fogDevice.getId()).get(ControllerComponent.RAM) &&
//                microservice.getSize() + getCurrentStorageLoad().get(fogDevice.getId()) <= resourceAvailability.get(fogDevice.getId()).get(ControllerComponent.STORAGE) &&
//                microservice.getMips() + getCurrentCpuLoad().get(fogDevice.getId()) <= resourceAvailability.get(fogDevice.getId()).get(ControllerComponent.CPU) &&
//                microservice.getBw() + getCurrentBWLoad().get(fogDevice.getId()) <= resourceAvailability.get(fogDevice.getId()).get(ControllerComponent.BW);
    }

    // Not literally only the client is returned, but the ids of the devices where the sources of this microservice
    // is placed.
    public List<Integer> getClientServiceNodeIds(Application application, String
            microservice, Map<String, Integer> placed) {
        List<String> clientServices = getClientServices(application, microservice);
        List<Integer> nodeIDs = new LinkedList<>();
        for (String clientService : clientServices) {
            if (placed.get(clientService) != null)
                nodeIDs.add(placed.get(clientService));
        }
        return nodeIDs;
    }

    private List<Integer> getClientServiceNodeIds(Milestone milestone, String msName) {

        List<String> clientServices = getClientServices(milestone, msName);
        List<Integer> nodeIDs = new LinkedList<>();
        for (String clientService : clientServices) {
            List<Integer> placedDevices = microserviceToDeviceListMap.get(clientService);
            if (placedDevices != null) {
                nodeIDs.addAll(placedDevices);
            }
            //nodeIDs.add(placed.get(clientService));
        }
        return nodeIDs;

    }

    //Not literally the client of the microservice, actually the list of sources for which this microservice is the
    // destination
    public List<String> getClientServices(Application application, String microservice) {
        List<String> clientServices = new LinkedList<>();

        for (AppEdge edge : application.getEdges()) {
            if (edge.getDestination().equals(microservice) && edge.getDirection() == Tuple.UP)
                clientServices.add(edge.getSource());
        }

        return clientServices;
    }

    @Override
    public void updateResources(Map<Integer, Map<String, Double>> resourceAvailability) {
        for (int deviceId : currentModuleInstanceNum.keySet()) {
            Map<String, Integer> moduleCount = currentModuleInstanceNum.get(deviceId);
            for (String moduleName : moduleCount.keySet()) {
                Application app = applicationInfo.get(moduleToApp.get(moduleName));
                AppModule module = app.getModuleByName(moduleName);
                double availableRam =
                        resourceAvailability.get(deviceId).get(ControllerComponent.RAM) - (module.getRam() * moduleCount.get(moduleName));
                double availableStorage =
                        resourceAvailability.get(deviceId).get(ControllerComponent.STORAGE) - (module.getSize() * moduleCount.get(moduleName));
                double availableMips =
                        resourceAvailability.get(deviceId).get(ControllerComponent.CPU) - (module.getMips() * moduleCount.get(moduleName));
                double availableBw =
                        resourceAvailability.get(deviceId).get(ControllerComponent.BW) - (module.getBw() * moduleCount.get(moduleName));
                resourceAvailability.get(deviceId).put(ControllerComponent.RAM, availableRam);
                resourceAvailability.get(deviceId).put(ControllerComponent.STORAGE, availableStorage);
                resourceAvailability.get(deviceId).put(ControllerComponent.CPU, availableMips);
                resourceAvailability.get(deviceId).put(ControllerComponent.BW, availableBw);
            }
        }
    }

    @Override
    public void postProcessing() {

    }

    public Map<Integer, Double> getCurrentRAMLoad() {
        return currentRAMLoad;
    }

    public void setCurrentRAMLoad(Map<Integer, Double> currentRAMLoad) {
        this.currentRAMLoad = currentRAMLoad;
    }

    public Map<Integer, Double> getCurrentStorageLoad() {
        return currentStorageLoad;
    }

    public void setCurrentStorageLoad(Map<Integer, Double> currentStorageLoad) {
        this.currentStorageLoad = currentStorageLoad;
    }

    public Map<Integer, Double> getCurrentCpuLoad() {
        return currentCpuLoad;
    }

    public void setCurrentCpuLoad(Map<Integer, Double> currentCpuLoad) {
        this.currentCpuLoad = currentCpuLoad;
    }

    public Map<Integer, Double> getCurrentBWLoad() {
        return currentBWLoad;
    }

    public void setCurrentBWLoad(Map<Integer, Double> currentBWLoad) {
        this.currentBWLoad = currentBWLoad;
    }

    public Map<Integer, List<String>> getCurrentModuleMap() {
        return currentModuleMap;
    }

    public void setCurrentModuleMap(Map<Integer, List<String>> currentModuleMap) {
        this.currentModuleMap = currentModuleMap;
    }

    public FogDevice getDeviceByName(String deviceName) {
        for (FogDevice f : fogDevices) {
            if (f.getName().equals(deviceName))
                return f;
        }
        return null;
    }

    public Microservice getModule(String moduleName, Milestone milestone) {
        for (AppModule appModule : milestone.getModules()) {
            if (appModule.getName().equals(moduleName))
                return (Microservice) appModule;
        }
        return null;
    }

    public FogDevice getDevice(int deviceId) {
        for (FogDevice fogDevice : fogDevices) {
            if (fogDevice.getId() == deviceId)
                return fogDevice;
        }
        return null;
    }


    private class NodeVm {
        public NodeVm(FogDevice fogdevice, Vm vm) {

        }
    }

    private class TupleTime {
        public TupleTime(Tuple tuple, double execTime) {
        }
    }
}
