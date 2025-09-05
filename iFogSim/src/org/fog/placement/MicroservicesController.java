package org.fog.placement;

import org.apache.commons.math3.util.Pair;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.fog.application.AppLoop;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.entities.*;
import org.fog.utils.*;
import dynamicOptimisation.utils.EntityName.MicroserviceName;
import dynamicOptimisation.utils.PlacementType;
import dynamicOptimisation.utils.SimulationParameters;

import java.util.*;

/**
 * Created by Samodha Pallewatta on 7/31/2020.
 */
public class MicroservicesController extends SimEntity {

    protected List<FogDevice> fogDevices;
    protected List<Sensor> sensors;
    protected Map<String, Application> applications = new HashMap<>();
    protected PlacementLogicFactory placementLogicFactory = new PlacementLogicFactory();
    protected Map<PlacementRequest, Integer> placementRequestDelayMap = new HashMap<>();
    protected int placementLogic;

    protected List<Integer> clustering_levels;

    /**
     * @param name
     * @param fogDevices
     * @param sensors
     * @param applications
     */
    public MicroservicesController(String name, List<FogDevice> fogDevices, List<Sensor> sensors, List<Application> applications, List<Integer> clusterLevels, Double clusterLatency, int placementLogic) {
        super(name);
        this.fogDevices = fogDevices;
        this.sensors = sensors;
        this.clustering_levels = clusterLevels;
        this.placementLogic = placementLogic;
        for (Application app : applications) {
            this.applications.put(app.getAppId(), app);
        }

        init();

    }

    public MicroservicesController(String name, List<FogDevice> fogDevices, List<Sensor> sensors, List<Application> applications, List<Integer> clusterLevels, Double clusterLatency, int placementLogic, Map<Integer, List<FogDevice>> monitored) {
        super(name);
        this.fogDevices = fogDevices;
        this.sensors = sensors;
        this.clustering_levels = clusterLevels;
        this.placementLogic = placementLogic;
        for (Application app : applications) {
            this.applications.put(app.getAppId(), app);
        }
        init(monitored);
    }

    protected void init() {
        connectWithLatencies();

        if (Config.ENABLE_STATIC_CLUSTERING && SimulationParameters.placementType != PlacementType.CLOUD_FOG_OPTIMISE) {
            for (Integer id : clustering_levels)
                createClusterConnections(id, fogDevices, Config.clusteringLatency);
        }
        printClusterConnections();

        initializeControllers(placementLogic);
        generateRoutingTable();
    }

    protected void init(Map<Integer, List<FogDevice>> monitored) {
        connectWithLatencies();

        if (Config.ENABLE_STATIC_CLUSTERING) {
            for (Integer id : clustering_levels)
                createClusterConnections(id, fogDevices, Config.clusteringLatency);
        }
        //printClusterConnections();

        initializeControllers(placementLogic, monitored);
        generateRoutingTable();
    }

    protected void initializeControllers(int placementLogic) {
        for (FogDevice device : fogDevices) {
            LoadBalancer loadBalancer = new RRLoadBalancer();
            MicroserviceFogDevice cdevice = (MicroserviceFogDevice) device;

            //responsible for placement decision making
            if (cdevice.getDeviceType().equals(MicroserviceFogDevice.FON) ){//|| cdevice.getDeviceType().equals
                // (MicroserviceFogDevice.CLOUD)) {
                List<FogDevice> monitoredDevices = getDevicesForFON(cdevice);//, fogDevices); // Lists all the devices
                // orchestrated
                // by cdevice. If it's parent is Cloud, cdevice is also included in the list.
                MicroservicePlacementLogic microservicePlacementLogic = placementLogicFactory.getPlacementLogic(placementLogic, cdevice.getId());
                cdevice.initializeController(loadBalancer, microservicePlacementLogic, getResourceInfo(monitoredDevices), applications, monitoredDevices);
            } else if (cdevice.getDeviceType().equals(MicroserviceFogDevice.FCN) || cdevice.getDeviceType().equals(MicroserviceFogDevice.CLIENT)) {
                cdevice.initializeController(loadBalancer);
            }
        }
    }

    private List<FogDevice> getDevicesForFON(MicroserviceFogDevice cdevice, List<FogDevice> fogDevices) {

        List<FogDevice> monitoredDevices = new ArrayList<>();

        for (FogDevice fogDevice: fogDevices){
            MicroserviceFogDevice msDevice = (MicroserviceFogDevice) fogDevice;
            if (msDevice.getDeviceType().equals(MicroserviceFogDevice.FCN) || msDevice.getDeviceType().equals(MicroserviceFogDevice.CLIENT)){
                msDevice.setParentId(cdevice.getId());
                monitoredDevices.add(msDevice);
            }
        }

        return monitoredDevices;
    }

    protected void initializeControllers(int placementLogic, Map<Integer, List<FogDevice>> monitored) {
        for (FogDevice device : fogDevices) {
            LoadBalancer loadBalancer = new RRLoadBalancer();
            MicroserviceFogDevice cdevice = (MicroserviceFogDevice) device;

            //responsible for placement decision making
            if (cdevice.getDeviceType().equals(MicroserviceFogDevice.FON) || cdevice.getDeviceType().equals(MicroserviceFogDevice.CLOUD)) {
                List<FogDevice> monitoredDevices = monitored.get(cdevice.getFonId());
                MicroservicePlacementLogic microservicePlacementLogic = placementLogicFactory.getPlacementLogic(placementLogic, cdevice.getId());
                // Following line of code orchestrates the devices monitored by the Orchestration device of cdevice.
                // This cdevice is also orchestrated here.
                // We will have to integrate our own Load balancer and placement login.
                cdevice.initializeController(loadBalancer, microservicePlacementLogic, getResourceInfo(monitoredDevices), applications, monitoredDevices);
            } else if (cdevice.getDeviceType().equals(MicroserviceFogDevice.FCN) || cdevice.getDeviceType().equals(MicroserviceFogDevice.CLIENT)) {
                cdevice.initializeController(loadBalancer);
            }
        }
    }

    protected FogDevice getFogDeviceById(int id) {
        for (FogDevice f : fogDevices) {
            if (f.getId() == id)
                return f;
        }
        return null;
    }

    protected void generateRoutingTable() {
        Map<Integer, Map<Integer, Integer>> routing = ShortestPathRoutingGenerator.generateRoutingTable(fogDevices);

        for (FogDevice f : fogDevices) {
            ((MicroserviceFogDevice) f).addRoutingTable(routing.get(f.getId()));
        }

    }

    // Starting point of the simulation which includes the placement policy as well.
    public void startEntity() {
        if (MicroservicePlacementConfig.SIMULATION_MODE == "STATIC")
            initiatePlacementRequestProcessing();
        if (MicroservicePlacementConfig.SIMULATION_MODE == "DYNAMIC")
            initiatePlacementRequestProcessingDynamic();

        if (MicroservicePlacementConfig.ENABLE_RESOURCE_DATA_SHARING) {
            shareResourceDataAmongClusterNodes();
        }

        send(getId(), Config.RESOURCE_MANAGE_INTERVAL, FogEvents.CONTROLLER_RESOURCE_MANAGE);

        send(getId(), Config.MAX_SIMULATION_TIME, FogEvents.STOP_SIMULATION);
    }

    protected void shareResourceDataAmongClusterNodes() {
        for (FogDevice f : fogDevices) {
            if (((MicroserviceFogDevice) f).getIsInCluster()) {
                for (int deviceId : ((MicroserviceFogDevice) f).getClusterMembers()) {
                    Pair<Integer, Map<String, Double>> resources = new Pair<>(f.getId(), ((MicroserviceFogDevice) f).getResourceAvailabilityOfDevice());
                    sendNow(deviceId, FogEvents.UPDATE_RESOURCE_INFO, resources);
                }
            }
        }
    }

    protected void initiatePlacementRequestProcessingDynamic() {
        for (PlacementRequest p : placementRequestDelayMap.keySet()) {
            processPlacedModules(p);
            if (placementRequestDelayMap.get(p) == 0) {
                sendNow(p.getGatewayDeviceId(), FogEvents.TRANSMIT_PR, p);
            } else
                send(p.getGatewayDeviceId(), placementRequestDelayMap.get(p), FogEvents.TRANSMIT_PR, p);
        }
        if (MicroservicePlacementConfig.PR_PROCESSING_MODE == MicroservicePlacementConfig.PERIODIC) {
            for (FogDevice f : fogDevices) {
                if (((MicroserviceFogDevice) f).getDeviceType() == MicroserviceFogDevice.FON) {
                    sendNow(f.getId(), FogEvents.PROCESS_PRS);
                }
            }
        }
    }

    protected void initiatePlacementRequestProcessing() {
        for (PlacementRequest p : placementRequestDelayMap.keySet()) {
            processPlacedModules(p);
            int fonId = ((MicroserviceFogDevice) getFogDeviceById(p.getGatewayDeviceId())).getFonId();
            if (placementRequestDelayMap.get(p) == 0) {
                sendNow(fonId, FogEvents.RECEIVE_PR, p);
            } else
                send(getId(), placementRequestDelayMap.get(p), FogEvents.TRANSMIT_PR, p);
        }
        if (MicroservicePlacementConfig.PR_PROCESSING_MODE == MicroservicePlacementConfig.PERIODIC) {
            for (FogDevice f : fogDevices) {
                if (((MicroserviceFogDevice) f).getDeviceType() == MicroserviceFogDevice.FON || ((MicroserviceFogDevice) f).getDeviceType() == MicroserviceFogDevice.CLOUD) { // as the
                    // orchestration node is cloud and all the microservices are placed in cloud
                    sendNow(f.getId(), FogEvents.PROCESS_PRS);
                }
            }
        }
    }

    protected void processPlacedModules(PlacementRequest p) {
        for (String placed : p.getPlacedMicroservices().keySet()) {
            int deviceId = p.getPlacedMicroservices().get(placed);
            Application application = applications.get(p.getApplicationId());
            sendNow(deviceId, FogEvents.ACTIVE_APP_UPDATE, application);
            sendNow(deviceId, FogEvents.APP_SUBMIT, application);
            if (placed == MicroserviceName.SENSOR_MODULE_1){
                sendNow(deviceId, FogEvents.LAUNCH_MODULE,
                        new AppModule(application.getModuleByName(MicroserviceName.SENSOR_MODULE_1)));
            }

        }
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case FogEvents.TRANSMIT_PR:
                transmitPr(ev);
            case FogEvents.CONTROLLER_RESOURCE_MANAGE:
                manageResources();
                break;
            case FogEvents.STOP_SIMULATION:
                CloudSim.stopSimulation();
                printTimeDetails();
                printPowerDetails();
                printCostDetails();
                printNetworkUsageDetails();
//                System.out.println("Tuple generated: "+ SimulationParameters.tupleGenerated);
//                saveAllDataToExcel();
                printQoSDetails();
                System.exit(0);
                break;
        }

    }

    private void transmitPr(SimEvent ev) {
        PlacementRequest placementRequest = (PlacementRequest) ev.getData();
        int fonId = ((MicroserviceFogDevice) getFogDeviceById(placementRequest.getGatewayDeviceId())).getFonId();
        sendNow(fonId, FogEvents.RECEIVE_PR, placementRequest);
    }


    protected void printQoSDetails() {
        System.out.println("=========================================");
        System.out.println("APPLICATION QOS SATISFACTION");
        System.out.println("=========================================");
        double success = 0;
        double total = 0;
        for (Integer loopId : TimeKeeper.getInstance().getLoopIdToLatencyQoSSuccessCount().keySet()) {
            success += TimeKeeper.getInstance().getLoopIdToLatencyQoSSuccessCount().get(loopId);
            total += TimeKeeper.getInstance().getLoopIdToCurrentNum().get(loopId);
        }

        double successPercentage = success / total * 100;
        System.out.println("Makespan" + " ---> " + successPercentage);
    }

    protected void printCostDetails() {
        System.out.println("Cost of execution in cloud = " + getCloud().getTotalCost());
    }

    @Override
    public void shutdownEntity() {
    }

    protected void manageResources() {
        send(getId(), Config.RESOURCE_MANAGE_INTERVAL, FogEvents.CONTROLLER_RESOURCE_MANAGE);
    }

    protected void printNetworkUsageDetails() {
        System.out.println("Total network usage = " + NetworkUsageMonitor.getNetworkUsage() / Config.MAX_SIMULATION_TIME);
    }

    protected FogDevice getCloud() {
        for (FogDevice dev : fogDevices)
            if (dev.getName().equals("cloud"))
                return dev;
        return null;
    }

    protected void printPowerDetails() {
        StringBuilder energyInfo = new StringBuilder();

        double availRAM = 0;
        double availStorage = 0;
        double availCPU = 0;
        double availBW = 0;

        for (FogDevice fogDevice : fogDevices) {

            if (fogDevice.getName().contains("CF")){

            MicroserviceFogDevice microserviceFogDevice = (MicroserviceFogDevice) fogDevice;

            energyInfo.append("\n---------");
            energyInfo.append("Device Name: "+ fogDevice.getName()+"---------\n");

            String energyPerDevice = "Energy Consumed = " + fogDevice.getEnergyConsumption() + "\n";
            String RAMPerDevice =
                   "Available RAM = " +  + microserviceFogDevice.getControllerComponent().getAvailableResource(fogDevice.getId(), ControllerComponent.RAM)+
                    "\n";
            String StoragePerDevice =
                    "Available Storage = " +  + microserviceFogDevice.getControllerComponent().getAvailableResource(fogDevice.getId(), ControllerComponent.STORAGE)+
                            "\n";
            String BWPerDevice =
                   "Available BW = " +  + microserviceFogDevice.getControllerComponent().getAvailableResource(fogDevice.getId(), ControllerComponent.BW)+
                            "\n";
            String CPUPerDevice =
                   "Available CPU = " +  + microserviceFogDevice.getControllerComponent().getAvailableResource(fogDevice.getId(), ControllerComponent.CPU)+
                            "\n";

            energyInfo.append(energyPerDevice);
            energyInfo.append(RAMPerDevice);
            energyInfo.append(StoragePerDevice);
            energyInfo.append(BWPerDevice);
            energyInfo.append(CPUPerDevice);
            //energyInfo.append("---------\n");


                availRAM += microserviceFogDevice.getControllerComponent().getAvailableResource(fogDevice.getId(),
                        ControllerComponent.RAM);
                availStorage += microserviceFogDevice.getControllerComponent().getAvailableResource(fogDevice.getId(),
                        ControllerComponent.STORAGE);
                availCPU += microserviceFogDevice.getControllerComponent().getAvailableResource(fogDevice.getId(),
                        ControllerComponent.CPU);
                availBW += microserviceFogDevice.getControllerComponent().getAvailableResource(fogDevice.getId(),
                        ControllerComponent.BW);
            }

        }
        energyInfo.append("\n---------Total available resources--------------");
        energyInfo.append("\nTotal available RAM "+ availRAM);
        energyInfo.append("\nTotal available Storage "+availStorage);
        energyInfo.append("\nTotal available CPU "+availCPU);
        energyInfo.append("\nTotal available BW "+availBW);

        System.out.println(energyInfo.toString());
    }

    protected String getStringForLoopId(int loopId) {
        for (String appId : applications.keySet()) {
            Application app = applications.get(appId);
            for (AppLoop loop : app.getLoops()) {
                if (loop.getLoopId() == loopId)
                    return loop.getModules().toString();
            }
        }
        return null;
    }

    protected void printTimeDetails() {
        System.out.println("=========================================");
        System.out.println("============== RESULTS ==================");
        System.out.println("=========================================");
        System.out.println("EXECUTION TIME : " + (Calendar.getInstance().getTimeInMillis() - TimeKeeper.getInstance().getSimulationStartTime()));
        System.out.println("=========================================");
        System.out.println("APPLICATION LOOP DELAYS");
        System.out.println("=========================================");
        for (Integer loopId : TimeKeeper.getInstance().getLoopIdToTupleIds().keySet()) {
			/*double average = 0, count = 0;
			for(int tupleId : TimeKeeper.getInstance().getLoopIdToTupleIds().get(loopId)){
				Double startTime = 	TimeKeeper.getInstance().getEmitTimes().get(tupleId);
				Double endTime = 	TimeKeeper.getInstance().getEndTimes().get(tupleId);
				if(startTime == null || endTime == null)
					break;
				average += endTime-startTime;
				count += 1;
			}
			System.out.println(getStringForLoopId(loopId) + " ---> "+(average/count));*/
            System.out.println(getStringForLoopId(loopId) + " ---> " + TimeKeeper.getInstance().getLoopIdToCurrentAverage().get(loopId));
        }
        System.out.println("=========================================");
        System.out.println("TUPLE CPU EXECUTION DELAY");
        System.out.println("=========================================");

        for (String tupleType : TimeKeeper.getInstance().getTupleTypeToAverageCpuTime().keySet()) {
            System.out.println(tupleType + " ---> " + TimeKeeper.getInstance().getTupleTypeToAverageCpuTime().get(tupleType));
        }

        System.out.println("=========================================");
    }

    protected Map<Integer, Map<String, Double>> getResourceInfo(List<FogDevice> fogDevices) {
        Map<Integer, Map<String, Double>> resources = new HashMap<>();
        for (FogDevice device : fogDevices) {
            Map<String, Double> perDevice = new HashMap<>();
            perDevice.put(ControllerComponent.CPU, (double) device.getHost().getTotalMips());
            perDevice.put(ControllerComponent.RAM, (double) device.getHost().getRam());
            perDevice.put(ControllerComponent.STORAGE, (double) device.getHost().getStorage());
            perDevice.put(ControllerComponent.BW, (double) device.getHost().getBw());
            resources.put(device.getId(), perDevice);
        }
        return resources;
    }


    public void submitPlacementRequests(List<PlacementRequest> placementRequests, int delay) {
        for (PlacementRequest p : placementRequests) {
            placementRequestDelayMap.put(p, delay);
        }
    }

    // Tracking the child devices for each device and latency to connect with them.
    protected void connectWithLatencies() {

        for (FogDevice fogDevice : fogDevices) {
            if (fogDevice.getParentId() >= 0) {
                FogDevice parent = (FogDevice) CloudSim.getEntity(fogDevice.getParentId());
                if (parent == null)
                    continue;
                double latency = fogDevice.getUplinkLatency();
                parent.getChildToLatencyMap().put(fogDevice.getId(), latency);
                parent.getChildrenIds().add(fogDevice.getId());
            }
//            else if (fogDevice.getParentId() == -1){ // Checking for my static optimisation as data will come from
//                // Cloud as well.
//
//            }
        }
    }

    protected List<FogDevice> getDevicesForFON(FogDevice f) {
        List<FogDevice> fogDevices = new ArrayList<>();
        fogDevices.add(f);
        ((MicroserviceFogDevice) f).setFonID(f.getId());
        List<FogDevice> connected = new ArrayList<>();
        connected.add(f);
        boolean changed = true;
        while (changed) {
            changed = false;
            List<FogDevice> rootNodes = new ArrayList<>();
            for (FogDevice d : connected)
                rootNodes.add(d);
            for (FogDevice rootD : rootNodes) {
                for (int child : rootD.getChildrenIds()) {
                    FogDevice device = getFogDeviceById(child);
                    connected.add(device);
                    if (!fogDevices.contains(device)) {
                        fogDevices.add(device);
                        ((MicroserviceFogDevice) device).setFonID(f.getId());
                        changed = true;
                    }
                }
                for (int cluster : ((MicroserviceFogDevice) rootD).getClusterMembers()) {
                    FogDevice device = getFogDeviceById(cluster);
                    connected.add(device);
                    if (!fogDevices.contains(device)) {
                        fogDevices.add(device);
                        ((MicroserviceFogDevice) device).setFonID(f.getId());
                        changed = true;
                    }
                }
                connected.remove(rootD);

            }
        }
        int parentId = f.getParentId();
        if (parentId != -1) {
            MicroserviceFogDevice fogDevice = (MicroserviceFogDevice) getFogDeviceById(parentId);
            if (fogDevice.getDeviceType().equals(MicroserviceFogDevice.CLOUD))
                fogDevices.add(fogDevice);
        }

        return fogDevices;
    }




    protected static void createClusterConnections(int levelIdentifier, List<FogDevice> fogDevices, Double clusterLatency) {

        // Tracking Fog devices by their levels and mapping them with their parents (listing all the children together
        // for a particular parent)
        Map<Integer, List<FogDevice>> fogDevicesByParent = new HashMap<>();
        for (FogDevice fogDevice : fogDevices) {
            if (fogDevice.getLevel() == levelIdentifier) {
                if (fogDevicesByParent.containsKey(fogDevice.getParentId())) {
                    fogDevicesByParent.get(fogDevice.getParentId()).add(fogDevice);
                } else {
                    List<FogDevice> sameParentList = new ArrayList<>();
                    sameParentList.add(fogDevice);
                    fogDevicesByParent.put(fogDevice.getParentId(), sameParentList);
                }
            }
        }

        // For a particular Fog device, Tracking latency to each of its cluster members
        for (int parentId : fogDevicesByParent.keySet()) {
            List<Integer> clusterNodeIds = new ArrayList<>();
            for (FogDevice fogdevice : fogDevicesByParent.get(parentId)) {
                clusterNodeIds.add(fogdevice.getId());
            }
            for (FogDevice fogDevice : fogDevicesByParent.get(parentId)) {
                List<Integer> clusterNodeIdsTemp = new ArrayList<>(clusterNodeIds);
                clusterNodeIds.remove((Object) fogDevice.getId());
                ((MicroserviceFogDevice) fogDevice).setClusterMembers(clusterNodeIds);
                Map<Integer, Double> latencyMap = new HashMap<>();
                for (int id : clusterNodeIds) {
                    latencyMap.put(id, clusterLatency); // Cluster latency is the latency between devices in the same
                    // cluster.
                }
                ((MicroserviceFogDevice) fogDevice).setClusterMembersToLatencyMap(latencyMap); // For a particular Fog device, Tracking latency to each of its cluster members
                ((MicroserviceFogDevice) fogDevice).setIsInCluster(true);
                clusterNodeIds = clusterNodeIdsTemp;

            }
        }
    }

    protected void printClusterConnections() {
        StringBuilder clusterString = new StringBuilder();
       // clusterString.append("Cluster formation : ");
        // <ParentNode,ClusterNodes> Assuming than clusters are formed among nodes with same parent
        HashMap<String, List<MicroserviceFogDevice>> clusters = new HashMap<>();
        for (FogDevice f : fogDevices) {
            MicroserviceFogDevice cDevice = (MicroserviceFogDevice) f;
            if (cDevice.getIsInCluster()) {
                FogDevice parent = getFogDeviceById(cDevice.getParentId());
                if (clusters.containsKey(parent.getName()))
                    clusters.get(parent.getName()).add(cDevice);
                else
                    clusters.put(parent.getName(), new ArrayList<>(Arrays.asList(cDevice)));
            }
        }
        for (String parent : clusters.keySet()) {
            List<MicroserviceFogDevice> clusterNodes = clusters.get(parent);
            clusterString.append("Parent node : " + parent + " -> cluster Nodes : ");
            for (MicroserviceFogDevice device : clusterNodes) {
                int count = device.getClusterMembers().size();
                clusterString.append(device.getName() + ", ");
                for (Integer deviceId : device.getClusterMembers()) {
                    if (!clusterNodes.contains(getFogDeviceById(deviceId))) {
                        Logger.error("Cluster formation Error", "Error : " + getFogDeviceById(deviceId).getName() + " is added as a cluster node of " + device.getName());
                    }
                }
                if (count + 1 != clusterNodes.size())
                    Logger.error("Cluster formation Error", "Error : number of cluster nodes does not match");
            }

            clusterString.append("\n");
        }
      //  System.out.println(clusterString);
    }

}
