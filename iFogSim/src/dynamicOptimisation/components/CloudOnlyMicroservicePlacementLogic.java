package dynamicOptimisation.components;

import org.apache.commons.math3.util.Pair;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.entities.ControllerComponent;
import org.fog.entities.FogDevice;
import org.fog.entities.PlacementRequest;
import org.fog.placement.MicroservicePlacementLogic;
import org.fog.placement.PlacementLogicOutput;
import org.fog.utils.Logger;
import org.fog.utils.ModuleLaunchConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudOnlyMicroservicePlacementLogic implements MicroservicePlacementLogic {

    List<FogDevice> fogDevices; //fog devices considered by FON for placements of requests
    List<PlacementRequest> placementRequests; // requests to be processed
    protected Map<Integer, Map<String, Double>> resourceAvailability;
    private Map<String, Application> applicationInfo = new HashMap<>();
    private Map<String, String> moduleToApp = new HashMap<>();
    Map<Integer, Map<String, Integer>> mappedMicroservices = new HashMap<>();

    int fonID;

    protected Map<Integer, Double> currentCpuLoad;
    protected Map<Integer, List<String>> currentModuleMap = new HashMap<>();
    protected Map<Integer, Map<String, Double>> currentModuleLoadMap = new HashMap<>();
    protected Map<Integer, Map<String, Integer>> currentModuleInstanceNum = new HashMap<>();

    protected Map<PlacementRequest, Integer> prStatus = new HashMap<>();


    public CloudOnlyMicroservicePlacementLogic(int fonID) {
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

        setCurrentCpuLoad(new HashMap<Integer, Double>());
        setCurrentModuleMap(new HashMap<>());
        for (FogDevice dev : fogDevices) {
            getCurrentCpuLoad().put(dev.getId(), 0.0);
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
        //initiate with the  parent of the client device for this
        for (PlacementRequest placementRequest : placementRequests) {
            deviceToPlace.put(placementRequest, getDevice(placementRequest.getGatewayDeviceId()).getParentId());

            // already placed modules
            mappedMicroservices.put(placementRequest.getPlacementRequestId(), new HashMap<>(placementRequest.getPlacedMicroservices()));

            //special modules  - predefined cloud placements
            Application app = applicationInfo.get(placementRequest.getApplicationId());
            for (String microservice : app.getSpecialPlacementInfo().keySet()) {
                for (String deviceName : app.getSpecialPlacementInfo().get(microservice)) {
                    FogDevice device = getDeviceByName(deviceName);
                    int deviceId = device.getId();

                    if (getModule(microservice, app).getMips() + getCurrentCpuLoad().get(deviceId) <= resourceAvailability.get(deviceId).get(ControllerComponent.CPU)) {
                        Logger.debug("ModulePlacementEdgeward", "Placement of operator " + microservice + " on device " + device.getName() + " successful.");
                        getCurrentCpuLoad().put(deviceId, getModule(microservice, app).getMips() + getCurrentCpuLoad().get(deviceId));
                        System.out.println("Placement of operator " + microservice + " on device " + device.getName() + " successful.");

                        moduleToApp.put(microservice, app.getAppId());

                        if (!currentModuleMap.get(deviceId).contains(microservice))
                            currentModuleMap.get(deviceId).add(microservice);

                        mappedMicroservices.get(placementRequest.getPlacementRequestId()).put(microservice, deviceId);

                        //currentModuleLoad
                        if (!currentModuleLoadMap.get(deviceId).containsKey(microservice)) {
                            currentModuleLoadMap.get(deviceId).put(microservice, getModule(microservice, app).getMips());
                        }
                        else
                            currentModuleLoadMap.get(deviceId).put(microservice, getModule(microservice, app).getMips() + currentModuleLoadMap.get(deviceId).get(microservice));


                        //currentModuleInstance
                        if (!currentModuleInstanceNum.get(deviceId).containsKey(microservice)) {
                            currentModuleInstanceNum.get(deviceId).put(microservice, 1);
                            // mappedMicroservices.get(placementRequest.getPlacementRequestId()).put(microservice,
                            //        deviceId);
                        }
                        else
                            currentModuleInstanceNum.get(deviceId).put(microservice, currentModuleInstanceNum.get(deviceId).get(microservice) + 1);

                        break;
                    }
                }
            }
        }

    }


    private PlacementLogicOutput generatePlacementMap() {

        Map<Integer, Map<Application, List<ModuleLaunchConfig>>> perDevice = new HashMap<>();
        Map<Integer, List<Pair<String, Integer>>> serviceDiscoveryInfo = new HashMap<>();


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
        }

        for (PlacementRequest placementRequest : placementRequests) {
            Application application = applicationInfo.get(placementRequest.getApplicationId());
            //int orchestrationDeviceId = getDeviceByName(DeviceNames.Cloud).getId();

            // Placing sensor modules to the gateway devices
            for (String microservice : placementRequest.getPlacedMicroservices().keySet()) {
                for (AppModule appModule : application.getModules()) {
                    if (appModule.getName().equals(microservice)) {
                        int deviceId = placementRequest.getPlacedMicroservices().get(microservice);
                        if (serviceDiscoveryInfo.containsKey(placementRequest.getGatewayDeviceId()))
                            serviceDiscoveryInfo.get(placementRequest.getGatewayDeviceId()).add(new Pair<>(microservice, deviceId));
                        else {
                            List<Pair<String, Integer>> s = new ArrayList<>();
                            s.add(new Pair<>(microservice, deviceId));
                            serviceDiscoveryInfo.put(placementRequest.getGatewayDeviceId(), s);
                        }

                    } else if (application.getSpecialPlacementInfo().containsKey(appModule.getName())) {
                        // Placing rest of the devices in the Cloud
                        if (serviceDiscoveryInfo.containsKey(placementRequest.getGatewayDeviceId()))
                            serviceDiscoveryInfo.get(getFonID()).add(new Pair<>(microservice, getFonID()));
                        else {
                            List<Pair<String, Integer>> s = new ArrayList<>();
                            s.add(new Pair<>(microservice, getFonID()));
                            serviceDiscoveryInfo.put(getFonID(), s);
                        }

                    }
                }
            }
            // all prs get placed in this placement algorithm
            prStatus.put(placementRequest, -1);
            //  placement.put(placementRequest.getPlacementRequestId(), placementRequest.getPlacedMicroservices());
        }

        for (int deviceId : currentModuleInstanceNum.keySet()) {
            for (String microservice : currentModuleInstanceNum.get(deviceId).keySet()) {
                Application application = applicationInfo.get(moduleToApp.get(microservice));
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

        return new PlacementLogicOutput(perDevice, serviceDiscoveryInfo, prStatus);
    }


    @Override
    public void updateResources(Map<Integer, Map<String, Double>> resourceAvailability) {
        for (int deviceId : currentModuleInstanceNum.keySet()) {
            Map<String, Integer> moduleCount = currentModuleInstanceNum.get(deviceId);
            for (String moduleName : moduleCount.keySet()) {
                Application app = applicationInfo.get(moduleToApp.get(moduleName));
                AppModule module = app.getModuleByName(moduleName);
                double mips = resourceAvailability.get(deviceId).get(ControllerComponent.CPU) - (module.getMips() * moduleCount.get(moduleName));
                resourceAvailability.get(deviceId).put(ControllerComponent.CPU, mips);
            }
        }
    }

    @Override
    public void postProcessing() {

    }

    public void setCurrentCpuLoad(Map<Integer, Double> currentCpuLoad) {
        this.currentCpuLoad = currentCpuLoad;
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

    public Map<Integer, Double> getCurrentCpuLoad() {
        return currentCpuLoad;
    }

    private AppModule getModule(String moduleName, Application app) {
        for (AppModule appModule : app.getModules()) {
            if (appModule.getName().equals(moduleName))
                return appModule;
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
