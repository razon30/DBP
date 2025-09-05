package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.util.Pair;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.entities.Actuator;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.MicroserviceFogDevice;
import org.fog.entities.PlacementRequest;
import org.fog.entities.Sensor;
import org.fog.mobilitydata.DataParser;
import org.fog.mobilitydata.RandomMobilityGenerator;
import org.fog.placement.LocationHandler;
import org.fog.placement.MicroservicesMobilityClusteringController;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.TimeKeeper;
import org.fog.utils.distribution.DeterministicDistribution;
import org.json.simple.parser.ParseException;

public class MicroserviceApp_RandomMobility_Clustering {
    static List<FogDevice> fogDevices = new ArrayList();
    static List<Sensor> sensors = new ArrayList();
    static List<Actuator> actuators = new ArrayList();
    static Map<Integer, Integer> userMobilityPattern = new HashMap();
    static LocationHandler locator;
    static double SENSOR_TRANSMISSION_TIME = 10.0;
    static int numberOfMobileUser = 1;
    static boolean randomMobility_generator = false;
    static boolean renewDataset = false;
    static List<Integer> clusteringLevels = new ArrayList();
    static List<Application> applications = new ArrayList();
    static List<Pair<Double, Double>> qosValues = new ArrayList();

    public MicroserviceApp_RandomMobility_Clustering() {
    }

    public static void main(String[] args) {
        try {
            Log.disable();
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;
            CloudSim.init(num_user, calendar, trace_flag);
            FogBroker broker = new FogBroker("broker");
            Application microservicesApplication = createApplication("example", broker.getId());
            applications.add(microservicesApplication);
            DataParser dataObject = new DataParser();
            locator = new LocationHandler(dataObject);
            String datasetReference = ".\\dataset\\usersLocation-melbCBD_";
            if (randomMobility_generator) {
                datasetReference = ".\\dataset\\random_usersLocation-melbCBD_";
                createRandomMobilityDatasets(1, datasetReference, renewDataset);
            }

            createMobileUser(broker.getId(), ((Application)applications.get(0)).getAppId(), datasetReference);
            createFogDevices(broker.getId(), ((Application)applications.get(0)).getAppId());
            List<Application> appList = new ArrayList();
            Iterator var9 = applications.iterator();

            while(var9.hasNext()) {
                Application application = (Application)var9.next();
                appList.add(application);
            }

            List<Integer> clusterLevelIdentifier = new ArrayList();
            clusterLevelIdentifier.add(2);
            int placementAlgo = 2;
            MicroservicesMobilityClusteringController microservicesController = new MicroservicesMobilityClusteringController("controller", fogDevices, sensors, appList, clusterLevelIdentifier, 2.0, placementAlgo, locator);
            List<PlacementRequest> placementRequests = new ArrayList();
            Iterator var13 = sensors.iterator();

            while(var13.hasNext()) {
                Sensor s = (Sensor)var13.next();
                Map<String, Integer> placedMicroservicesMap = new HashMap();
                placedMicroservicesMap.put("clientModule", s.getGatewayDeviceId());
                PlacementRequest p = new PlacementRequest(s.getAppId(), s.getId(), s.getGatewayDeviceId(), placedMicroservicesMap);
                placementRequests.add(p);
            }

            microservicesController.submitPlacementRequests(placementRequests, 0);
            TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());
            CloudSim.startSimulation();
            CloudSim.stopSimulation();
            Log.printLine("VRGame finished!");
        } catch (Exception var17) {
            var17.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }

    }

    private static void createRandomMobilityDatasets(int mobilityModel, String datasetReference, boolean renewDataset) throws IOException, ParseException {
        RandomMobilityGenerator randMobilityGenerator = new RandomMobilityGenerator();

        for(int i = 0; i < numberOfMobileUser; ++i) {
            randMobilityGenerator.createRandomData(mobilityModel, i + 1, datasetReference, renewDataset);
        }

    }

    private static void createFogDevices(int userId, String appId) throws NumberFormatException, IOException {
        locator.parseResourceInfo();
        if (locator.getLevelWiseResources(locator.getLevelID("Cloud")).size() == 1) {
            FogDevice cloud = createFogDevice("cloud", 44800L, 40000, 100L, 10000L, 0.01, 1648.0, 1332.0, "cloud");
            cloud.setParentId(-1);
            locator.linkDataWithInstance(cloud.getId(), (String)locator.getLevelWiseResources(locator.getLevelID("Cloud")).get(0));
            cloud.setLevel(0);
            fogDevices.add(cloud);

            int i;
            MicroserviceFogDevice gateway;
            for(i = 0; i < locator.getLevelWiseResources(locator.getLevelID("Proxy")).size(); ++i) {
                gateway = createFogDevice("proxy-server_" + i, 2800L, 4000, 10000L, 10000L, 0.0, 107.339, 83.4333, "fon");
                locator.linkDataWithInstance(gateway.getId(), (String)locator.getLevelWiseResources(locator.getLevelID("Proxy")).get(i));
                gateway.setParentId(cloud.getId());
                gateway.setUplinkLatency(100.0);
                gateway.setLevel(1);
                fogDevices.add(gateway);
            }

            for(i = 0; i < locator.getLevelWiseResources(locator.getLevelID("Gateway")).size(); ++i) {
                gateway = createFogDevice("gateway_" + i, 2800L, 4000, 10000L, 10000L, 0.0, 107.339, 83.4333, "fcn");
                locator.linkDataWithInstance(gateway.getId(), (String)locator.getLevelWiseResources(locator.getLevelID("Gateway")).get(i));
                gateway.setParentId(locator.determineParent(gateway.getId(), -1.0));
                gateway.setUplinkLatency(4.0);
                gateway.setLevel(2);
                fogDevices.add(gateway);
            }
        }

    }

    private static void createMobileUser(int userId, String appId, String datasetReference) throws IOException {
        for(int id = 1; id <= numberOfMobileUser; ++id) {
            userMobilityPattern.put(id, 1);
        }

        locator.parseUserInfo(userMobilityPattern, datasetReference);
        List<String> mobileUserDataIds = locator.getMobileUserDataId();

        for(int i = 0; i < numberOfMobileUser; ++i) {
            FogDevice mobile = addMobile("mobile_" + i, userId, appId, -1);
            mobile.setUplinkLatency(2.0);
            locator.linkDataWithInstance(mobile.getId(), (String)mobileUserDataIds.get(i));
            mobile.setLevel(3);
            fogDevices.add(mobile);
        }

    }

    private static MicroserviceFogDevice createFogDevice(String nodeName, long mips, int ram, long upBw, long downBw, double ratePerMips, double busyPower, double idlePower, String deviceType) {
        List<Pe> peList = new ArrayList();
        peList.add(new Pe(0, new PeProvisionerOverbooking((double)mips)));
        int hostId = FogUtils.generateEntityId();
        long storage = 1000000L;
        int bw = 10000;
        PowerHost host = new PowerHost(hostId, new RamProvisionerSimple(ram), new BwProvisionerOverbooking((long)bw), storage, peList, new StreamOperatorScheduler(peList), new FogLinearPowerModel(busyPower, idlePower));
        List<Host> hostList = new ArrayList();
        hostList.add(host);
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;
        LinkedList<Storage> storageList = new LinkedList();
        FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(arch, os, vmm, host, time_zone, cost, costPerMem, costPerStorage, costPerBw);
        MicroserviceFogDevice fogdevice = null;

        try {
            fogdevice = new MicroserviceFogDevice(nodeName, characteristics, new AppModuleAllocationPolicy(hostList), storageList, 10.0, (double)upBw, (double)downBw, 1250000.0, 0.0, ratePerMips, deviceType);
        } catch (Exception var39) {
            var39.printStackTrace();
        }

        return fogdevice;
    }

    private static FogDevice addMobile(String name, int userId, String appId, int parentId) {
        FogDevice mobile = createFogDevice(name, 500L, 20, 1000L, 270L, 0.0, 87.53, 82.44, "client");
        mobile.setParentId(parentId);
        Sensor mobileSensor = new Sensor("sensor-" + name, "M-SENSOR", userId, appId, new DeterministicDistribution(SENSOR_TRANSMISSION_TIME));
        mobileSensor.setApp((Application)applications.get(0));
        sensors.add(mobileSensor);
        Actuator mobileDisplay = new Actuator("actuator-" + name, userId, appId, "M-DISPLAY");
        actuators.add(mobileDisplay);
        mobileSensor.setGatewayDeviceId(mobile.getId());
        mobileSensor.setLatency(6.0);
        mobileDisplay.setGatewayDeviceId(mobile.getId());
        mobileDisplay.setLatency(1.0);
        mobileDisplay.setApp((Application)applications.get(0));
        return mobile;
    }

    private static Application createApplication(String appId, int userId) {
        Application application = Application.createApplication(appId, userId);
        application.addAppModule("clientModule", 10);
        application.addAppModule("processingModule", 10);
        application.addAppModule("storageModule", 10);
        if (SENSOR_TRANSMISSION_TIME == 5.1) {
            application.addAppEdge("M-SENSOR", "clientModule", 2000.0, 500.0, "M-SENSOR", 1, 1);
        } else {
            application.addAppEdge("M-SENSOR", "clientModule", 3000.0, 500.0, "M-SENSOR", 1, 1);
        }

        application.addAppEdge("clientModule", "processingModule", 3500.0, 500.0, "RAW_DATA", 1, 3);
        application.addAppEdge("processingModule", "storageModule", 1000.0, 1000.0, "PROCESSED_DATA", 1, 3);
        application.addAppEdge("processingModule", "clientModule", 14.0, 500.0, "ACTION_COMMAND", 2, 3);
        application.addAppEdge("clientModule", "M-DISPLAY", 1000.0, 500.0, "ACTUATION_SIGNAL", 2, 2);
        application.addTupleMapping("clientModule", "M-SENSOR", "RAW_DATA", new FractionalSelectivity(1.0));
        application.addTupleMapping("processingModule", "RAW_DATA", "PROCESSED_DATA", new FractionalSelectivity(1.0));
        application.addTupleMapping("processingModule", "RAW_DATA", "ACTION_COMMAND", new FractionalSelectivity(1.0));
        application.addTupleMapping("clientModule", "ACTION_COMMAND", "ACTUATION_SIGNAL", new FractionalSelectivity(1.0));
        application.setSpecialPlacementInfo("storageModule", "cloud");
        final AppLoop loop1 = new AppLoop(new ArrayList<String>() {
            {
                this.add("M-SENSOR");
                this.add("clientModule");
                this.add("processingModule");
                this.add("clientModule");
                this.add("M-DISPLAY");
            }
        });
        List<AppLoop> loops = new ArrayList<AppLoop>() {
            {
                this.add(loop1);
            }
        };
        application.setLoops(loops);
        return application;
    }
}
