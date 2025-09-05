package dynamicOptimisation.components;

import org.apache.commons.math3.util.Pair;
import org.fog.application.AppEdge;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.entities.*;
import org.fog.placement.MicroservicePlacementLogic;
import org.fog.placement.PlacementLogicOutput;
import org.fog.utils.ModuleLaunchConfig;
import dynamicOptimisation.model.Microservice;

import java.util.*;

public class CloudFogMicroservicePlacementNonOptimise implements MicroservicePlacementLogic {

    List<FogDevice> fogDevices; //fog devices considered by FON for placements of requests, should include all the
    // Citizen Fog devices
    List<PlacementRequest> placementRequests; // requests to be processed, mainly the gateways to place the sensor
    // modules.
    protected Map<Integer, Map<String, Double>> resourceAvailability;
    private Map<String, Application> applicationInfo = new HashMap<>();
    private Map<String, String> moduleToApp = new HashMap<>();
    Map<Integer, Map<String, Integer>> mappedMicroservices = new HashMap<>();

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


    public CloudFogMicroservicePlacementNonOptimise(int fonID) {
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

    private void mapModules() {

        Map<PlacementRequest, Integer> deviceToPlace = new HashMap<>();
        List<String> placedMicroservices = new ArrayList<>();
        Map<String, Boolean> microserviceToPlacementStatus = new HashMap<>();

        for (PlacementRequest placementRequest : placementRequests) {
            deviceToPlace.put(placementRequest, getDevice(placementRequest.getGatewayDeviceId()).getParentId());
            // already placed modules
            mappedMicroservices.put(placementRequest.getPlacementRequestId(), new HashMap<>(placementRequest.getPlacedMicroservices()));

            Milestone milestone = (Milestone) applicationInfo.get(placementRequest.getApplicationId());
//            if (microserviceToPlacementStatus == null){
//                microserviceToPlacementStatus = new HashMap<>();
//                for (Microservice microservice: milestone.microservices){
//                    microserviceToPlacementStatus.put(microservice.getName(), false);
//                }
//            }

            // Placing the special modules in the Cloud
            for (String microserviceName : milestone.getSpecialPlacementInfo().keySet()) {
                for (String deviceName : milestone.getSpecialPlacementInfo().get(microserviceName)) {

                    FogDevice device = getDeviceByName(deviceName);
                    int deviceId = device.getId();

                    //if (getModule(microservice, milestone).getMips() + getCurrentCpuLoad().get(deviceId) <=
                    // resourceAvailability.get(deviceId).get(ControllerComponent.CPU)) {
                    Microservice microservice = getModule(microserviceName, milestone);
                    if (deviceCanPlace(microservice, device)) {
                     //   Logger.debug("ModulePlacementEdgeward", "Placement of operator " + microservice + " on " +
                       //         "device " + device.getName() + " successful.");
                        getCurrentRAMLoad().put(device.getId(),
                                microservice.getRam() + getCurrentRAMLoad().get(device.getId()));
                        getCurrentStorageLoad().put(device.getId(),
                                microservice.getSize() + getCurrentStorageLoad().get(device.getId()));
                        getCurrentCpuLoad().put(device.getId(), microservice.getMips() + getCurrentCpuLoad().get(device.getId()));
                        getCurrentBWLoad().put(device.getId(),
                                microservice.getBw() + getCurrentBWLoad().get(device.getId()));

                        moduleToApp.put(microserviceName, milestone.getAppId());

                        if (!currentModuleMap.get(deviceId).contains(microserviceName))
                            currentModuleMap.get(deviceId).add(microserviceName);

                        if (!milestone.toPlaceInCFs.contains(microserviceName)) {
                            mappedMicroservices.get(placementRequest.getPlacementRequestId()).put(microserviceName, deviceId);
                        }
                        System.out.println("Placement of operator " + microservice + " on device " + device.getName() + " successful.");

//                        //currentModuleLoad
//                        if (!currentModuleLoadMap.get(deviceId).containsKey(microservice)) {
//                            currentModuleLoadMap.get(deviceId).put(microservice, getModule(microservice, milestone).getMips());
//                        } else
//                            currentModuleLoadMap.get(deviceId).put(microservice, getModule(microservice, milestone).getMips() + currentModuleLoadMap.get(deviceId).get(microservice));


                        //currentModuleInstance
                        if (!currentModuleInstanceNum.get(deviceId).containsKey(microserviceName)) {
                            currentModuleInstanceNum.get(deviceId).put(microserviceName, 1);
                            // mappedMicroservices.get(placementRequest.getPlacementRequestId()).put(microservice,
                            //        deviceId);
                        }
                        else
                            currentModuleInstanceNum.get(deviceId).put(microserviceName, currentModuleInstanceNum.get(deviceId).get(microserviceName) + 1);

                        break;
                    }

                }
            }



            // Placing the remaining modules in CFs
            for (String microserviceToPlaceInCF : milestone.toPlaceInCFs) {

                //String microserviceToPlaceInCF = microserviceToPlace.getName();

                Microservice microservice = getModule(microserviceToPlaceInCF, milestone);

                for (FogDevice fogDevice : fogDevices) {

//                    if (fogDevice.getName().contains("Edge_Gateway_Fog_Device")){
//                        return;
//                    }

                    if (!placedMicroservices.contains(microserviceToPlaceInCF) && fogDevice.getName().contains("CF")) {
                        if (Objects.equals(((MicroserviceFogDevice) fogDevice).getDeviceType(), MicroserviceFogDevice.FCN)) {
                            if (microservice != null) {
                                if (deviceCanPlace(microservice, fogDevice)) {

                                    moduleToApp.put(microservice.getName(), milestone.getAppId());
                                    if (!currentModuleMap.get(fogDevice.getId()).contains(microservice.getName()))
                                        currentModuleMap.get(fogDevice.getId()).add(microservice.getName());

                                    mappedMicroservices.get(placementRequest.getPlacementRequestId()).put(microservice.getName(),
                                            fogDevice.getId());
                                    System.out.println("Placement of operator " + microservice.getName() + " on device " + fogDevice.getName() + " successful.");
                                    placedMicroservices.add(microserviceToPlaceInCF);
                                    microserviceToPlacementStatus.put(microserviceToPlaceInCF, true);

                                    getCurrentRAMLoad().put(fogDevice.getId(),
                                            microservice.getRam() + getCurrentRAMLoad().get(fogDevice.getId()));
                                    getCurrentStorageLoad().put(fogDevice.getId(),
                                            microservice.getSize() + getCurrentStorageLoad().get(fogDevice.getId()));
                                    getCurrentCpuLoad().put(fogDevice.getId(), microservice.getMips() + getCurrentCpuLoad().get(fogDevice.getId()));
                                    getCurrentBWLoad().put(fogDevice.getId(),
                                            microservice.getBw() + getCurrentBWLoad().get(fogDevice.getId()));

                                    //                            if (!currentModuleLoadMap.get(fogDevice.getId()).containsKey(microservice.getName())) {
                                    //                                currentModuleLoadMap.get(fogDevice.getId()).put(microservice.getName(),
                                    //                                        microservice.getMips());
                                    //                            } else {
                                    //                                currentModuleLoadMap.get(fogDevice.getId()).put(microservice.getName(),
                                    //                                        microservice.getMips() + currentModuleLoadMap.get(fogDevice.getId()).get(microservice.getName()));
                                    //                            }

                                    if (!currentModuleInstanceNum.get(fogDevice.getId()).containsKey(microservice.getName())) {
                                        currentModuleInstanceNum.get(fogDevice.getId()).put(microservice.getName(), 1);
                                        // mappedMicroservices.get(placementRequest.getPlacementRequestId()).put(microservice,
                                        //        deviceId);
                                    }
                                    else {
                                        int currentInstance =
                                                currentModuleInstanceNum.get(fogDevice.getId()).get(microservice.getName());
                                        currentModuleInstanceNum.get(fogDevice.getId()).put(microservice.getName(),
                                                currentInstance++);
                                    }


                                } else {
                                    failedAttempt++;
                                    failedToPlace.add(microservice);
                                }
                            }
                        }

                    }

                    if (placedMicroservices.size() == milestone.toPlaceInCFs.size()){
                        placedMicroservices.clear();
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
            System.out.println("Microservice: " + microservice.getName() + " could not place in device: " +
                 fogDevice.getName() + " due to RAM unavailability");
            return false;
        }
        if (!isStorage) {
            System.out.println("Microservice: " + microservice.getName() + " could not place in device: " + fogDevice.getName() + " due to Storage unavailability");
            return false;
        }
        if (!isCPU) {
            System.out.println("Microservice: " + microservice.getName() + " could not place in device: " + fogDevice.getName() + " due to CPU unavailability");
            return false;
        }
        if (!iSBW) {
            System.out.println("Microservice: " + microservice.getName() + " could not place in device: " + fogDevice.getName() + " due to BW unavailability");
            return false;
        }

        return true;

//        return microservice.getRam() + getCurrentRAMLoad().get(fogDevice.getId()) <= resourceAvailability.get(fogDevice.getId()).get(ControllerComponent.RAM) &&
//                microservice.getSize() + getCurrentStorageLoad().get(fogDevice.getId()) <= resourceAvailability.get(fogDevice.getId()).get(ControllerComponent.STORAGE) &&
//                microservice.getMips() + getCurrentCpuLoad().get(fogDevice.getId()) <= resourceAvailability.get(fogDevice.getId()).get(ControllerComponent.CPU) &&
//                microservice.getBw() + getCurrentBWLoad().get(fogDevice.getId()) <= resourceAvailability.get(fogDevice.getId()).get(ControllerComponent.BW);
    }

    private PlacementLogicOutput generatePlacementMap() {

        Map<Integer, Map<Application, List<ModuleLaunchConfig>>> perDevice = new HashMap<>();
        Map<Integer, List<Pair<String, Integer>>> serviceDiscoveryInfo = new HashMap<>();
        Map<Integer, Map<String, Integer>> placement = new HashMap<>();

        // Here for each microservice in mappedMicroservices, we are checking whether is it in placement request list
        // or not. if it is not in the list, we will add that in the list and add them for the placement in the
        // placement list.
        // Map<Integer, Map<String, Integer>> placement = new HashMap<>();
        for (PlacementRequest placementRequest : placementRequests) {
            //placement should include newly placed ones
            for (String microservice : mappedMicroservices.get(placementRequest.getPlacementRequestId()).keySet()) {
                if (!placementRequest.getPlacedMicroservices().containsKey(microservice)) {
                    placementRequest.getPlacedMicroservices().put(microservice, mappedMicroservices.get(placementRequest.getPlacementRequestId()).get(microservice));
                }
            }

            System.out.println("\n\n");
            Application application = applicationInfo.get(placementRequest.getApplicationId());

            Map<String, Integer> placedMicroservices = placementRequest.getPlacedMicroservices();
            for (String microserviceName : placedMicroservices.keySet()) {
                int deviceID = placedMicroservices.get(microserviceName);

                //service discovery info propagation
                List<Integer> clientDevices = getClientServiceNodeIds(application, microserviceName, placementRequest.getPlacedMicroservices());
                System.out.println("For the Microservice: " + microserviceName + " Placed on device: " + deviceID + " " +
                        "Found client devices: " + Arrays.toString(clientDevices.toArray()));
//                if (microserviceName.equals("Site_Anomaly_Detection_Module")){
//                    System.out.println("Site_Anomaly_Detection_Module");
//                }
                for (int clientDevice : clientDevices) {

                    // Actually what we are doing here, is if I am the destination module M-2, placed in D-2 and my
                    // source (client modules) modules are M-0, and M-1 running in D-0, and D-1 respectively, I
                    // am saving myself and my device Id for each of D-0 and D-1.
                    // That is Discovery(D1) = {(M-2, D-2)}. If M-2 was also placed in D-4,
                    // Discovery(D1) = {(M-2, D-2), (M-2, D-4)}. Or If, M-1 running in D-1 was source for another
                    // Module, say M-3 running in D-1, Discovery(D1) = {(M-2, D-2), (M-2, D-2), (M-3, D-1)}

                    if (serviceDiscoveryInfo.containsKey(clientDevice))
                        serviceDiscoveryInfo.get(clientDevice).add(new Pair<>(microserviceName, deviceID));
                    else {
                        List<Pair<String, Integer>> s = new ArrayList<>();
                        s.add(new Pair<>(microserviceName, deviceID));
                        serviceDiscoveryInfo.put(clientDevice, s);
                    }
                }


            }

            // all prs get placed in this placement algorithm
            prStatus.put(placementRequest, -1);
            System.out.println("\n\n");

            //todo module is created new here check if this is needed
            for (int deviceId : currentModuleInstanceNum.keySet()) {
                for (String microservice : currentModuleInstanceNum.get(deviceId).keySet()) {
                    //Application application = applicationInfo.get(moduleToApp.get(microservice));
                    AppModule appModule = new AppModule(application.getModuleByName(microservice));
                    ModuleLaunchConfig moduleLaunchConfig = new ModuleLaunchConfig(appModule, currentModuleInstanceNum.get(deviceId).get(microservice));
                    if (perDevice.keySet().contains(deviceId)) {
                        if (perDevice.get(deviceId).containsKey(application)) {
                            perDevice.get(deviceId).get(application).add(moduleLaunchConfig);
                        } else {
                            List<ModuleLaunchConfig> l = new ArrayList<>();
                            l.add(moduleLaunchConfig);
                            perDevice.get(deviceId).put(application, l);
                        }
                    } else {
                        List<ModuleLaunchConfig> l = new ArrayList<>();
                        l.add(moduleLaunchConfig);
                        HashMap<Application, List<ModuleLaunchConfig>> m = new HashMap<>();
                        m.put(application, l);
                        perDevice.put(deviceId, m);
                    }
                }
            }


        }

        return new PlacementLogicOutput(perDevice, serviceDiscoveryInfo, prStatus);
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

    private FogDevice getDeviceByName(String deviceName) {
        for (FogDevice f : fogDevices) {
            if (f.getName().equals(deviceName))
                return f;
        }
        return null;
    }

    private Microservice getModule(String moduleName, Milestone milestone) {
        for (AppModule appModule : milestone.getModules()) {
            if (appModule.getName().equals(moduleName))
                return (Microservice) appModule;
        }
        return null;
    }

    private FogDevice getDevice(int deviceId) {
        for (FogDevice fogDevice : fogDevices) {
            if (fogDevice.getId() == deviceId)
                return fogDevice;
        }
        return null;
    }


}
