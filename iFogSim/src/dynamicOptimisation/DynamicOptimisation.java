package dynamicOptimisation;

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
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.entities.*;
import org.fog.placement.PlacementLogicFactory;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.TimeKeeper;
import org.fog.utils.distribution.DeterministicDistribution;
import dynamicOptimisation.components.MasterFogDevice;
import dynamicOptimisation.components.Milestone;
import dynamicOptimisation.components.MilestoneController;
import dynamicOptimisation.model.Microservice;
import dynamicOptimisation.utils.EntityName.DeviceNames;
import dynamicOptimisation.utils.EntityName.EventName;
import dynamicOptimisation.utils.EntityName.IoTDeviceName;
import dynamicOptimisation.utils.EntityName.MicroserviceName;
import dynamicOptimisation.utils.PlacementType;
import dynamicOptimisation.utils.SimulationParameters;
import dynamicOptimisation.utils.UtilFactory;

import java.util.*;


/*
BP Microservice placed: Only in the Cloud
Fog Orchestration: Only in the Cloud
Sensor Application Modules: At the edge (To match the current state and assuming that our demo paper is established
for managing sensor events.)
LoadBalancer: RoundRobin in the Cloud
Placement Logic: Distributed for gateway but primarily BP is in the Cloud.
 */

/*
BP Microservice placed: Across Fog and Cloud
Fog Orchestration: On Fog devices
Sensor Application Modules: At the edge (To match the current state and assuming that our demo paper is established
for managing sensor events.)
LoadBalancer: RoundRobin
Placement Logic: Distributed but not optimised.
 */

/*
BP Microservice placed: Across Fog and Cloud
Fog Orchestration: On Fog devices
Sensor Application Modules: At the edge (To match the current state and assuming that our demo paper is established
for managing sensor events.)
LoadBalancer: Custom algorithm based on timeliness requirements and resource availability.
Placement Logic: Distributed and optimised.
 */


/*
SImulation parameter selection
We model the fog environment according to the architecture presented in section 3.2. Network parameters of the
fog environment include bandwidth and latency among
different devices of the fog architecture. We extract these
values from previous studies on network performance of
edge networks following novel communication technologies
as follows: WLAN communication (150Mbps, 2ms) based on
WiFi-6 [32] and 5G [33], LAN connections (1Gbps, 0.5ms)
based on gigabit Ethernet technology [34], and fog-cloud
connections with WAN (30ms, 100Mbps) [11]. Fog device
resources are defined using three parameters: CPU (1500-
3000 MIPS), RAM (2-8 GB) and storage (32-256 GB) [35],
[36]. These values represent resource availability of heterogeneous fog devices such as RaspberryPi, Dell PowerEdge,
Jetson Nano, etc. The cost of the resources is modelled
following the price model of AWS Fargate and extended to
the fog layer with an increase factor of 1.2-1.5 as proposed
in [37]

Source: https://ieeexplore.ieee.org/stamp/stamp.jsp?arnumber=10509822
 */


public class DynamicOptimisation {

    static List<FogDevice> fogDevices;
    static List<Sensor> sensors;
    static List<Actuator> actuators;
    //application
    static List<Milestone> milestones;
    static List<Pair<Double, Double>> qosValues;
    static List<Microservice> microservicesToPlace;

    public static void main(String[] args) {
        for (int i = 0; i < SimulationParameters.numberOfSimulation; i++) {
            initVariables();
            startTheSimulation();
        }
    }

    private static void initVariables() {

       fogDevices = new ArrayList<FogDevice>();
       sensors = new ArrayList<Sensor>();
       actuators = new ArrayList<Actuator>();
        //application
        milestones = new ArrayList<>();
        qosValues = new ArrayList<>();
        microservicesToPlace = new ArrayList<>();

        //static boolean CLOUD = true;
    }

    private static void startTheSimulation() {
        try {
            Log.disable();
            int num_user = 1; // number of cloud users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false; // mean trace events
            CloudSim.init(num_user, calendar, trace_flag);

            FogBroker broker = new FogBroker("broker");

            Milestone faultDetectionMilestone = createMilestone("HazardousDetectionApp", broker.getId());
            faultDetectionMilestone.setUserId(broker.getId());
            milestones.add(faultDetectionMilestone);

            createFogDevices(broker.getId(), faultDetectionMilestone);

            // A single cluster where the Cloud is monitoring all gateway devices.
            List<Integer> clusterLevelIdentifier = new ArrayList<>();
            clusterLevelIdentifier.add(2);

            /**
             * Central controller for performing preprocessing functions
             */
            int placementAlgo = 0;
            if (SimulationParameters.placementType == PlacementType.CLOUD_ONLY){
                placementAlgo = PlacementLogicFactory.CLOUD_ONLY_MICROSERVICES_PLACEMENT;
            }else if (SimulationParameters.placementType == PlacementType.CLOUD_FOG_NON_OPTIMISE){
                placementAlgo = PlacementLogicFactory.CLOUD_FOG_MICROSERVICES_PLACEMENT;
            }else if (SimulationParameters.placementType == PlacementType.CLOUD_FOG_OPTIMISE){
                placementAlgo = PlacementLogicFactory.CLOUD_FOG_MICROSERVICES_PLACEMENT_OPTIMISE;
            }

            MilestoneController milestoneController = MilestoneController.createController("controller", fogDevices, sensors,
                    milestones, clusterLevelIdentifier, SimulationParameters.clusterLatency, placementAlgo);
            milestoneController.setMilestones(milestones);

            // generate placement requests for gateway devices.
            // Identifying the devices that are connected with the sensors as a gateway device.
            List<PlacementRequest> placementRequests = new ArrayList<>();
            for (Sensor s : sensors) {
                Map<String, Integer> placedMicroservicesMap = new HashMap<>();
                placedMicroservicesMap.put(MicroserviceName.SENSOR_MODULE_1, s.getGatewayDeviceId());
                PlacementRequest p = new PlacementRequest(s.getAppId(), s.getId(), s.getGatewayDeviceId(), placedMicroservicesMap);
                placementRequests.add(p);
            }

            milestoneController.submitPlacementRequests(placementRequests, 0);

            TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());

            CloudSim.startSimulation();

            CloudSim.stopSimulation();



        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }

    }


    private static void createFogDevices(int userId, Milestone faultDetectionMilestone) {
        MicroserviceFogDevice cloud = createFogDevice(DeviceNames.Cloud, 80000000, 49152000, 2 * 1000 * 1000 * 1000, 100000, 10000,
                12500000, 0,
                0.01,
                16 * 103,
                16 * 83.25,
                MicroserviceFogDevice.FON); // creates the fog device Cloud at the apex of the hierarchy with level=0
        cloud.setParentId(-1);
      //  cloud.setDownLinkLatency(150);
        if (SimulationParameters.placementType == PlacementType.CLOUD_FOG_OPTIMISE){
            cloud.setDeviceType(MicroserviceFogDevice.CLOUD);
        }
        fogDevices.add(cloud);

        long mips = 1024L * UtilFactory.getValue(2, 4);
        int rams = 1024 * UtilFactory.getValue(2, 8);
        long upBW = 1024 * 15;//Utils.getValue(1024 * 6, 1024 * 8);
        long dwnBW = 1024 * 5;//Utils.getValue(1024 * 6, 1024 * 8);
        long storage = 1024L * UtilFactory.getValue(32, 256); // 1000000; // host storage
        int bw = 1024 * UtilFactory.getValue(3, 4);
        double staticPower = 4; // in watts similar as Raspberry Pi 4.0
        double maxPower = 6; // in watts similar as Raspberry Pi 4.0


        MicroserviceFogDevice gatewayDevice = createFogDevice(DeviceNames.EdgeGateway, mips, rams, storage, bw,
                upBW,
                dwnBW, 1,
                0.01,
                maxPower,
                staticPower,
                MicroserviceFogDevice.FCN); // creates the fog device Cloud at the apex of the hierarchy with level=0
        gatewayDevice.setParentId(cloud.getId());
     //   if (SimulationParameters.placementType == PlacementType.CLOUD_ONLY){
            gatewayDevice.setUplinkLatency(SimulationParameters.gatewayDelayToCloud);
//        }else {
//            gatewayDevice.setUplinkLatency(5);
//        }

        if (SimulationParameters.placementType == PlacementType.CLOUD_FOG_OPTIMISE){
            gatewayDevice.setDeviceType(MicroserviceFogDevice.FON);
        }

        fogDevices.add(gatewayDevice);

//        MasterFogDevice masterFog = createFogDevice(DeviceNames.MasterFog, mips, rams, storage, bw,
//                upBW,
//                dwnBW, 1,
//                0.01,
//                maxPower,
//                staticPower,
//                MicroserviceFogDevice.FON); // creates the fog device Cloud at the apex of the hierarchy with level=0
//        masterFog.setParentId(gatewayDevice.getId());
//        masterFog.setUplinkLatency(5);
//        if (SimulationParameters.placementType == PlacementType.CLOUD_FOG_OPTIMISE) {
//            fogDevices.add(masterFog);
//        }

        for (int i =0; i < SimulationParameters.numberOfCF; i++){
            MicroserviceFogDevice citizenFog = createCitizenFogs(i, gatewayDevice.getId(), userId, faultDetectionMilestone);
//            if (SimulationParameters.placementType == PlacementType.CLOUD_FOG_OPTIMISE) {
//                masterFog.getCitizenFogDevice().add(citizenFog);
//            }
        }

//        if (SimulationParameters.placementType == PlacementType.CLOUD_FOG_OPTIMISE) {
//            for (Microservice microservice: milestones.get(0).microservices){
//                if (milestones.get(0).toPlaceInCFs.contains(microservice.getName())){
//                    masterFog.getMicroservices().add(microservice);
//                }
//            }
//        }

//        if (SimulationParameters.placementType == PlacementType.CLOUD_FOG_OPTIMISE) {
//            for (int i = 0; i < SimulationParameters.numberOfSenSorDevice; i++) {
//                createSensorDevices(i, 0, masterFog.getId(), masterFog.getId(), userId, faultDetectionMilestone);
//            }
//        }

    }

    private static MicroserviceFogDevice createCitizenFogs(int cfIndex, int gwId, int userId, Milestone faultDetectionMilestone) {

        String name = "CF-"+gwId+"-"+cfIndex;
        long mips = 1024L * UtilFactory.getValue(2, 4);
        int rams = 1024 * UtilFactory.getValue(2, 8);
        long upBW = 1024 * 15;//Utils.getValue(1024 * 6, 1024 * 8);
        long dwnBW = 1024 * 5;//Utils.getValue(1024 * 6, 1024 * 8);
        long storage = 1024L * UtilFactory.getValue(32, 256); // 1000000; // host storage
        int bw = 1024 * UtilFactory.getValue(3, 4);
        double staticPower = 4; // in watts similar as Raspberry Pi 4.0
        double maxPower = 6; // in watts similar as Raspberry Pi 4.0

        MicroserviceFogDevice citizenFog = createFogDevice(name, mips, rams,storage, bw,
                upBW, dwnBW, 2, 0, maxPower, staticPower, MicroserviceFogDevice.FCN);
        citizenFog.setParentId(gwId);

        System.out.println( "CF-"+gwId+"-"+cfIndex+ ": "+ rams);

        citizenFog.setUplinkLatency(5); // latency of connection from citizenFog to the Gateway is 5 ms to match
        // with Rajkumar Buyya's paper
        fogDevices.add(citizenFog);

      //  if (SimulationParameters.placementType != PlacementType.CLOUD_FOG_OPTIMISE) {
            for (int i = 0; i < SimulationParameters.numberOfSenSorDevice; i++) {
                createSensorDevices(i, cfIndex, gwId, citizenFog.getId(), userId, faultDetectionMilestone);
            }
       // }
        return citizenFog;

    }

    private static void createSensorDevices(int sdId, int cfIndex, int gwId, int cfId, int userId, Milestone faultDetectionMilestone) {

        long mips = 1024L * UtilFactory.getValue(2, 4);
        int rams = 1024 * UtilFactory.getValue(2, 8);
        long upBW = 1024 * 15;//Utils.getValue(1024 * 6, 1024 * 8);
        long dwnBW = 1024 * 5;//Utils.getValue(1024 * 6, 1024 * 8);
        long storage = 1024L * UtilFactory.getValue(32, 256); // 1000000; // host storage
        int bw = 1024 * UtilFactory.getValue(3, 4);
        double staticPower = 4; // in watts similar as Raspberry Pi 4.0
        double maxPower = 6; // in watts similar as Raspberry Pi 4.0

        String name = "SD-"+gwId+"-"+cfIndex+"-"+sdId;

        FogDevice IoTGateway = createFogDevice(name, mips, rams,storage, bw,
                upBW, dwnBW, 1, 0, maxPower, staticPower, MicroserviceFogDevice.CLIENT);
        if (SimulationParameters.placementType == PlacementType.CLOUD_ONLY){
            IoTGateway.setParentId(gwId);
        }else {
            IoTGateway.setParentId(cfId);
        }
        // Recommending the Sensor Module in the gateway intermediate device
        faultDetectionMilestone.setSpecialPlacementInfo(MicroserviceName.SENSOR_MODULE_1, name);

        IoTGateway.setUplinkLatency(4); // latency of connection from citizenFog to the MasterFog is 4 ms to match
        // with Rajkumar Buyya's paper
        fogDevices.add(IoTGateway);


        Application application = milestones.get(0);
        double visThroughput = SimulationParameters.maxTupleNumber;//20;
        Sensor visualSensor = new Sensor(IoTDeviceName.V_SENSOR+"-"+gwId+"-"+cfIndex+"-"+sdId, IoTDeviceName.V_SENSOR, userId,
                application.getAppId(),
                new DeterministicDistribution(1000 / (visThroughput / 9 * 10))); // Setting deterministic
        // distribution by assuming that sensor is producing events on a fixed interval time.
        visualSensor.setApp(application);
        visualSensor.setGatewayDeviceId(IoTGateway.getId());
        visualSensor.setLatency(5.0);  // latency of connection between Visual sensors and the gateway is 5 ms
        visualSensor.setInitialEmitter(true);
        sensors.add(visualSensor);
        milestones.get(0).sensorList.add(visualSensor);

        double throughput = SimulationParameters.maxTupleNumber;//200;
        Sensor tempSensor = new Sensor(IoTDeviceName.T_SENSOR+"-"+gwId+"-"+cfIndex+"-"+sdId, IoTDeviceName.T_SENSOR, userId,
                application.getAppId(),
                new DeterministicDistribution(1000 / (throughput / 9 * 10))); // Setting deterministic
        // distribution by assuming that sensor is producing events on a fixed interval time.
        tempSensor.setApp(application);
        tempSensor.setGatewayDeviceId(IoTGateway.getId());
        tempSensor.setLatency(5.0);  // latency of connection between TEMPERATURE sensors and the gateway is 3 ms
        tempSensor.setInitialEmitter(false);
        sensors.add(tempSensor);
        milestones.get(0).sensorList.add(tempSensor);

//            Sensor pressureSensor = new Sensor(IoTDeviceName.P_SENSOR+"-"+i, IoTDeviceName.P_SENSOR, userId,
//                    application.getAppId(),
//                    new DeterministicDistribution(1000 / (throughput / 9 * 10))); // Setting deterministic
//            // distribution by assuming that sensor is producing events on a fixed interval time.
//            pressureSensor.setApp(application);
//            sensors.add(pressureSensor);
//            pressureSensor.setGatewayDeviceId(gateway.getId());
//            pressureSensor.setLatency(5.0);  // latency of connection between PRESSURE sensors and the gateway is 5 ms


        Actuator SDA = new Actuator(IoTDeviceName.SDA_ACTUATION+"-"+gwId+"-"+cfIndex+"-"+sdId, userId, application.getAppId(), IoTDeviceName.SDA_ACTUATION);
        SDA.setGatewayDeviceId(IoTGateway.getId());
        SDA.setLatency(1.0);  // latency of connection between SDA actuator and the parent Smartphone is 1 ms
        SDA.setApp(application);
        SDA.getActuatingObjects().add(tempSensor);
        actuators.add(SDA);
        milestones.get(0).actuatorList.add(SDA);


        Actuator monitor = new Actuator(IoTDeviceName.MONITOR_ACTUATION+"-"+gwId+"-"+cfIndex+"-"+sdId, userId, application.getAppId(),
                IoTDeviceName.MONITOR_ACTUATION);
        monitor.setGatewayDeviceId(IoTGateway.getId());
        monitor.setLatency(1.0);  // latency of connection between Display actuator and the parent Smartphone is
        // 1 ms
        monitor.setApp(application);
        actuators.add(monitor);
        milestones.get(0).actuatorList.add(monitor);

    }


    private static Milestone createMilestone(String appId, int userId) {
        Milestone milestone = (Milestone) Milestone.createMilestone(appId, userId);
        milestone.setRequiredMilestoneThroughput(UtilFactory.getValue(5, 20));

        // Vertices / appModules / microservices / activities
        String[] microservices = {
                MicroserviceName.SENSOR_MODULE_1,
                MicroserviceName.ANOMALY_DETECTION_MODULE_1,
                MicroserviceName.RESOURCE_ANALYSIS_MODULE_1,
                MicroserviceName.RESOURCE_INTERFACE_MODULE_1,
                MicroserviceName.NOTIFICATION_MODULE_1,
                MicroserviceName.MONITORING_WOA_MODULE_1,
                MicroserviceName.GATEWAY_MODULE_1,
                MicroserviceName.ACTION_PLAN_MODULE_1,
                MicroserviceName.PROD_SCHEDULE_MODULE_1
        };



        for (String microservice : microservices) {
            UtilFactory.addMicroserviceToMilestone(microservice, milestone);
        }
        // Connecting the appModules / microservices / activities in the milestone

        // Loop 1: From Sensor to Actuator via Resource Interface
        milestone.addAppEdge(IoTDeviceName.V_SENSOR, MicroserviceName.SENSOR_MODULE_1, 200, 500, IoTDeviceName.V_SENSOR, Tuple.UP,
                AppEdge.SENSOR);

        if (SimulationParameters.placementType == PlacementType.CLOUD_ONLY){
            milestone.addAppEdge(MicroserviceName.SENSOR_MODULE_1, MicroserviceName.GATEWAY_MODULE_1, 150, 450,
                    EventName.PROCESSED_V_SENSOR, Tuple.UP,
                    AppEdge.MODULE);
            milestone.addAppEdge(MicroserviceName.GATEWAY_MODULE_1, MicroserviceName.ANOMALY_DETECTION_MODULE_1, 150, 450,
                    EventName.PROCESSED_V_SENSOR, Tuple.UP,
                    AppEdge.MODULE);
        }else {
            milestone.addAppEdge(MicroserviceName.SENSOR_MODULE_1, MicroserviceName.ANOMALY_DETECTION_MODULE_1, 150, 450,
                    EventName.PROCESSED_V_SENSOR, Tuple.UP,
                    AppEdge.MODULE);
        }
        milestone.addAppEdge(MicroserviceName.ANOMALY_DETECTION_MODULE_1, MicroserviceName.RESOURCE_ANALYSIS_MODULE_1, 100, 500,
                EventName.FIRE_INCIDENT_DETECTION, Tuple.UP,
                AppEdge.MODULE);
        milestone.addAppEdge(MicroserviceName.RESOURCE_ANALYSIS_MODULE_1, MicroserviceName.RESOURCE_INTERFACE_MODULE_1, 100, 500,
                EventName.RESOURCE_ANALYSIS, Tuple.UP,
                AppEdge.MODULE);
//        milestone.addAppEdge(MicroserviceName.ANOMALY_DETECTION_MODULE_1, MicroserviceName.RESOURCE_ANALYSIS_MODULE_2,
//                100, 500,
//                EventName.FIRE_INCIDENT_DETECTION, Tuple.UP,
//                AppEdge.MODULE);
//        milestone.addAppEdge(MicroserviceName.RESOURCE_ANALYSIS_MODULE_2,
//                MicroserviceName.RESOURCE_INTERFACE_MODULE_2, 100, 500,
//                EventName.RESOURCE_ANALYSIS, Tuple.UP,
//                AppEdge.MODULE);

        if (SimulationParameters.placementType == PlacementType.CLOUD_ONLY){
            milestone.addAppEdge(MicroserviceName.RESOURCE_INTERFACE_MODULE_1,MicroserviceName.GATEWAY_MODULE_1, 50, 200,
                    EventName.REQUEST_SENSOR_DATA, Tuple.DOWN,
                    AppEdge.MODULE);
//            milestone.addAppEdge(MicroserviceName.RESOURCE_INTERFACE_MODULE_2,MicroserviceName.GATEWAY_MODULE_1, 50, 200,
//                    EventName.REQUEST_SENSOR_DATA, Tuple.DOWN,
//                    AppEdge.MODULE);

            milestone.addAppEdge(MicroserviceName.GATEWAY_MODULE_1,MicroserviceName.SENSOR_MODULE_1, 50, 200,
                    EventName.REQUEST_SENSOR_DATA, Tuple.DOWN,
                    AppEdge.MODULE);

        }else {
            milestone.addAppEdge(MicroserviceName.RESOURCE_INTERFACE_MODULE_1, MicroserviceName.SENSOR_MODULE_1, 50, 200,
                    EventName.REQUEST_SENSOR_DATA, Tuple.DOWN,
                    AppEdge.MODULE);
//            milestone.addAppEdge(MicroserviceName.RESOURCE_INTERFACE_MODULE_2, MicroserviceName.SENSOR_MODULE_1, 50, 200,
//                    EventName.REQUEST_SENSOR_DATA, Tuple.DOWN,
//                    AppEdge.MODULE);
        }
        milestone.addAppEdge(MicroserviceName.SENSOR_MODULE_1,  IoTDeviceName.SDA_ACTUATION, 50, 200,
                EventName.REQUEST_SENSOR_DATA,
                Tuple.ACTUATOR, AppEdge.ACTUATOR);
        milestone.addAppEdge(IoTDeviceName.SDA_ACTUATION,  IoTDeviceName.T_SENSOR, 50, 200,
                EventName.REQUEST_SENSOR_DATA,
                Tuple.DOWN, AppEdge.SENSOR);

        milestone.addAppEdge(IoTDeviceName.T_SENSOR, MicroserviceName.SENSOR_MODULE_1, 200, 500, IoTDeviceName.T_SENSOR, Tuple.UP,
                AppEdge.SENSOR);
        if (SimulationParameters.placementType == PlacementType.CLOUD_ONLY){
            milestone.addAppEdge(MicroserviceName.SENSOR_MODULE_1, MicroserviceName.GATEWAY_MODULE_1, 100, 400,
                    EventName.PROCESSED_T_SENSOR, Tuple.UP,
                    AppEdge.MODULE);

            milestone.addAppEdge(MicroserviceName.GATEWAY_MODULE_1, MicroserviceName.RESOURCE_INTERFACE_MODULE_1, 100, 400,
                    EventName.PROCESSED_T_SENSOR, Tuple.UP,
                    AppEdge.MODULE);
//            milestone.addAppEdge(MicroserviceName.GATEWAY_MODULE_1, MicroserviceName.RESOURCE_INTERFACE_MODULE_2, 100,
//                    400,
//                    EventName.PROCESSED_T_SENSOR, Tuple.UP,
//                    AppEdge.MODULE);

        }else {
            milestone.addAppEdge(MicroserviceName.SENSOR_MODULE_1, MicroserviceName.RESOURCE_INTERFACE_MODULE_1, 100, 400,
                    EventName.PROCESSED_T_SENSOR, Tuple.UP,
                    AppEdge.MODULE);
//            milestone.addAppEdge(MicroserviceName.SENSOR_MODULE_1, MicroserviceName.RESOURCE_INTERFACE_MODULE_2, 100, 400,
//                    EventName.PROCESSED_T_SENSOR, Tuple.UP,
//                    AppEdge.MODULE);
        }
        milestone.addAppEdge(MicroserviceName.RESOURCE_INTERFACE_MODULE_1, MicroserviceName.RESOURCE_ANALYSIS_MODULE_1, 75, 400,
                EventName.PROCESSED_T_SENSOR, Tuple.UP,
                AppEdge.MODULE);
        milestone.addAppEdge(MicroserviceName.RESOURCE_ANALYSIS_MODULE_1, MicroserviceName.NOTIFICATION_MODULE_1, 300, 500,
                EventName.RESOURCE_REPORT, Tuple.UP,
                AppEdge.MODULE);
//        milestone.addAppEdge(MicroserviceName.RESOURCE_INTERFACE_MODULE_2,
//                MicroserviceName.RESOURCE_ANALYSIS_MODULE_2, 75, 400,
//                EventName.PROCESSED_T_SENSOR, Tuple.UP,
//                AppEdge.MODULE);
//        milestone.addAppEdge(MicroserviceName.RESOURCE_ANALYSIS_MODULE_2, MicroserviceName.NOTIFICATION_MODULE_1, 300,
//                500,
//                EventName.RESOURCE_REPORT, Tuple.UP,
//                AppEdge.MODULE);
        milestone.addAppEdge(MicroserviceName.NOTIFICATION_MODULE_1, MicroserviceName.MONITORING_WOA_MODULE_1, 300, 500,
                EventName.RESOURCE_REPORT, Tuple.UP,
                AppEdge.MODULE);
        if (SimulationParameters.placementType == PlacementType.CLOUD_ONLY){
            milestone.addAppEdge(MicroserviceName.MONITORING_WOA_MODULE_1, MicroserviceName.ACTION_PLAN_MODULE_1, 400, 600,
                    EventName.WORK_ORDER, Tuple.UP,
                    AppEdge.MODULE);
        }else {
            milestone.addAppEdge(MicroserviceName.MONITORING_WOA_MODULE_1, MicroserviceName.GATEWAY_MODULE_1, 400, 600,
                    EventName.WORK_ORDER, Tuple.UP,
                    AppEdge.MODULE);
            milestone.addAppEdge(MicroserviceName.GATEWAY_MODULE_1, MicroserviceName.ACTION_PLAN_MODULE_1, 400, 600,
                    EventName.WORK_ORDER, Tuple.UP,
                    AppEdge.MODULE);
        }
        milestone.addAppEdge(MicroserviceName.ACTION_PLAN_MODULE_1, MicroserviceName.PROD_SCHEDULE_MODULE_1, 400, 600,
                EventName.ACTION_PLAN, Tuple.UP,
                AppEdge.MODULE);
        milestone.addAppEdge(MicroserviceName.PROD_SCHEDULE_MODULE_1, MicroserviceName.GATEWAY_MODULE_1, 400, 600,
                EventName.PROD_SCHEDULE, Tuple.DOWN,
                AppEdge.MODULE);
        milestone.addAppEdge(MicroserviceName.GATEWAY_MODULE_1, MicroserviceName.SENSOR_MODULE_1, 400, 600,
                EventName.PROD_SCHEDULE, Tuple.DOWN,
                AppEdge.MODULE);
        milestone.addAppEdge(MicroserviceName.SENSOR_MODULE_1, IoTDeviceName.MONITOR_ACTUATION, 400, 600,
                EventName.PROD_SCHEDULE,
                Tuple.ACTUATOR, AppEdge.ACTUATOR);

//        milestone.addAppEdge(MicroserviceName.SENSOR_MODULE_1,  IoTDeviceName.SDA_ACTUATION, 50, 200,
//                EventName.REQUEST_SENSOR_DATA,
//                Tuple.ACTUATOR, AppEdge.ACTUATOR);


        // input-output of each microservice with the event generation rate
        milestone.addTupleMapping(MicroserviceName.SENSOR_MODULE_1, IoTDeviceName.V_SENSOR, EventName.PROCESSED_V_SENSOR,
                new FractionalSelectivity(1));
        milestone.addTupleMapping(MicroserviceName.SENSOR_MODULE_1, IoTDeviceName.T_SENSOR, EventName.PROCESSED_T_SENSOR,
                new FractionalSelectivity(1));
        milestone.addTupleMapping(MicroserviceName.SENSOR_MODULE_1, EventName.REQUEST_SENSOR_DATA, EventName.REQUEST_SENSOR_DATA, new FractionalSelectivity(1));
        milestone.addTupleMapping(MicroserviceName.SENSOR_MODULE_1, EventName.PROD_SCHEDULE, EventName.PROD_SCHEDULE, new FractionalSelectivity(1));

        if (SimulationParameters.placementType == PlacementType.CLOUD_ONLY) {
            milestone.addTupleMapping(MicroserviceName.GATEWAY_MODULE_1, EventName.PROCESSED_V_SENSOR, EventName.PROCESSED_V_SENSOR,
                    new FractionalSelectivity(1));
            milestone.addTupleMapping(MicroserviceName.GATEWAY_MODULE_1, EventName.REQUEST_SENSOR_DATA, EventName.REQUEST_SENSOR_DATA,
                    new FractionalSelectivity(1));
            milestone.addTupleMapping(MicroserviceName.GATEWAY_MODULE_1, EventName.PROCESSED_T_SENSOR, EventName.PROCESSED_T_SENSOR,
                    new FractionalSelectivity(1));
        }
        milestone.addTupleMapping(MicroserviceName.GATEWAY_MODULE_1, EventName.WORK_ORDER, EventName.WORK_ORDER,
                new FractionalSelectivity(1));
        milestone.addTupleMapping(MicroserviceName.GATEWAY_MODULE_1, EventName.PROD_SCHEDULE, EventName.PROD_SCHEDULE,
                new FractionalSelectivity(1));

        milestone.addTupleMapping(MicroserviceName.ANOMALY_DETECTION_MODULE_1, EventName.PROCESSED_V_SENSOR, EventName.FIRE_INCIDENT_DETECTION, new FractionalSelectivity(1));

        milestone.addTupleMapping(MicroserviceName.RESOURCE_INTERFACE_MODULE_1, EventName.RESOURCE_ANALYSIS, EventName.REQUEST_SENSOR_DATA, new FractionalSelectivity(1));
        milestone.addTupleMapping(MicroserviceName.RESOURCE_INTERFACE_MODULE_1, EventName.PROCESSED_T_SENSOR, EventName.PROCESSED_T_SENSOR, new FractionalSelectivity(1));
//        milestone.addTupleMapping(MicroserviceName.RESOURCE_INTERFACE_MODULE_2, EventName.RESOURCE_ANALYSIS,
//                EventName.REQUEST_SENSOR_DATA, new FractionalSelectivity(1));
//        milestone.addTupleMapping(MicroserviceName.RESOURCE_INTERFACE_MODULE_2, EventName.PROCESSED_T_SENSOR,
//                EventName.PROCESSED_T_SENSOR, new FractionalSelectivity(1));

        milestone.addTupleMapping(MicroserviceName.RESOURCE_ANALYSIS_MODULE_1, EventName.FIRE_INCIDENT_DETECTION, EventName.RESOURCE_ANALYSIS, new FractionalSelectivity(1));
        milestone.addTupleMapping(MicroserviceName.RESOURCE_ANALYSIS_MODULE_1, EventName.PROCESSED_T_SENSOR, EventName.RESOURCE_REPORT, new FractionalSelectivity(1));
//        milestone.addTupleMapping(MicroserviceName.RESOURCE_ANALYSIS_MODULE_2, EventName.FIRE_INCIDENT_DETECTION,
//                EventName.RESOURCE_ANALYSIS, new FractionalSelectivity(1));
//        milestone.addTupleMapping(MicroserviceName.RESOURCE_ANALYSIS_MODULE_2, EventName.PROCESSED_T_SENSOR,
//                EventName.RESOURCE_REPORT, new FractionalSelectivity(1));

        milestone.addTupleMapping(MicroserviceName.NOTIFICATION_MODULE_1, EventName.RESOURCE_REPORT, EventName.RESOURCE_REPORT,
                new FractionalSelectivity(1));
        milestone.addTupleMapping(MicroserviceName.MONITORING_WOA_MODULE_1, EventName.RESOURCE_REPORT, EventName.WORK_ORDER,
                new FractionalSelectivity(1));
        milestone.addTupleMapping(MicroserviceName.ACTION_PLAN_MODULE_1, EventName.WORK_ORDER, EventName.ACTION_PLAN,
                new FractionalSelectivity(1));
        milestone.addTupleMapping(MicroserviceName.PROD_SCHEDULE_MODULE_1, EventName.ACTION_PLAN, EventName.PROD_SCHEDULE,
                new FractionalSelectivity(1));


        // Loops:
        // Loop 1: For the Fog
        final AppLoop loop1 = new AppLoop(new ArrayList<>() {{
            add(IoTDeviceName.V_SENSOR);
            add(MicroserviceName.SENSOR_MODULE_1);
            add(MicroserviceName.ANOMALY_DETECTION_MODULE_1);
            add(MicroserviceName.RESOURCE_ANALYSIS_MODULE_1);
            add(MicroserviceName.RESOURCE_INTERFACE_MODULE_1);
            add(MicroserviceName.SENSOR_MODULE_1);
            add(IoTDeviceName.SDA_ACTUATION);
            add(IoTDeviceName.T_SENSOR);
            add(MicroserviceName.SENSOR_MODULE_1);
            add(MicroserviceName.RESOURCE_INTERFACE_MODULE_1);
            add(MicroserviceName.RESOURCE_ANALYSIS_MODULE_1);
            add(MicroserviceName.NOTIFICATION_MODULE_1);
            add(MicroserviceName.MONITORING_WOA_MODULE_1);
            add(MicroserviceName.GATEWAY_MODULE_1);
            add(MicroserviceName.ACTION_PLAN_MODULE_1);
            add(MicroserviceName.PROD_SCHEDULE_MODULE_1);
            add(MicroserviceName.GATEWAY_MODULE_1);
            add(MicroserviceName.SENSOR_MODULE_1);
            add(IoTDeviceName.MONITOR_ACTUATION);
        }});
//        final AppLoop loop2 = new AppLoop(new ArrayList<>() {{
//            add(IoTDeviceName.V_SENSOR);
//            add(MicroserviceName.SENSOR_MODULE_1);
//            add(MicroserviceName.ANOMALY_DETECTION_MODULE_1);
//            add(MicroserviceName.RESOURCE_ANALYSIS_MODULE_2);
//            add(MicroserviceName.RESOURCE_INTERFACE_MODULE_2);
//            add(MicroserviceName.SENSOR_MODULE_1);
//            add(IoTDeviceName.SDA_ACTUATION);
//            add(IoTDeviceName.T_SENSOR);
//            add(MicroserviceName.SENSOR_MODULE_1);
//            add(MicroserviceName.RESOURCE_INTERFACE_MODULE_2);
//            add(MicroserviceName.RESOURCE_ANALYSIS_MODULE_2);
//            add(MicroserviceName.NOTIFICATION_MODULE_1);
//            add(MicroserviceName.MONITORING_WOA_MODULE_1);
//            add(MicroserviceName.GATEWAY_MODULE_1);
//            add(MicroserviceName.ACTION_PLAN_MODULE_1);
//            add(MicroserviceName.PROD_SCHEDULE_MODULE_1);
//            add(MicroserviceName.GATEWAY_MODULE_1);
//            add(MicroserviceName.SENSOR_MODULE_1);
//            add(IoTDeviceName.MONITOR_ACTUATION);
//        }});

        // Loop 2: Cloud only
        final AppLoop loop3 = new AppLoop(new ArrayList<>() {{
            add(IoTDeviceName.V_SENSOR);
            add(MicroserviceName.SENSOR_MODULE_1);
            add(MicroserviceName.GATEWAY_MODULE_1);
            add(MicroserviceName.ANOMALY_DETECTION_MODULE_1);
            add(MicroserviceName.RESOURCE_ANALYSIS_MODULE_1);
            add(MicroserviceName.RESOURCE_INTERFACE_MODULE_1);
            add(MicroserviceName.GATEWAY_MODULE_1);
            add(MicroserviceName.SENSOR_MODULE_1);
            add(IoTDeviceName.SDA_ACTUATION);
            add(IoTDeviceName.T_SENSOR);
            add(MicroserviceName.SENSOR_MODULE_1);
            add(MicroserviceName.GATEWAY_MODULE_1);
            add(MicroserviceName.RESOURCE_INTERFACE_MODULE_1);
            add(MicroserviceName.RESOURCE_ANALYSIS_MODULE_1);
            add(MicroserviceName.NOTIFICATION_MODULE_1);
            add(MicroserviceName.MONITORING_WOA_MODULE_1);
            add(MicroserviceName.ACTION_PLAN_MODULE_1);
            add(MicroserviceName.PROD_SCHEDULE_MODULE_1);
            add(MicroserviceName.GATEWAY_MODULE_1);
            add(MicroserviceName.SENSOR_MODULE_1);
            add(IoTDeviceName.MONITOR_ACTUATION);
        }});

//        final AppLoop loop4 = new AppLoop(new ArrayList<>() {{
//            add(IoTDeviceName.V_SENSOR);
//            add(MicroserviceName.SENSOR_MODULE_1);
//            add(MicroserviceName.GATEWAY_MODULE_1);
//            add(MicroserviceName.ANOMALY_DETECTION_MODULE_1);
//            add(MicroserviceName.RESOURCE_ANALYSIS_MODULE_2);
//            add(MicroserviceName.RESOURCE_INTERFACE_MODULE_2);
//            add(MicroserviceName.GATEWAY_MODULE_1);
//            add(MicroserviceName.SENSOR_MODULE_1);
//            add(IoTDeviceName.SDA_ACTUATION);
//            add(IoTDeviceName.T_SENSOR);
//            add(MicroserviceName.SENSOR_MODULE_1);
//            add(MicroserviceName.GATEWAY_MODULE_1);
//            add(MicroserviceName.RESOURCE_INTERFACE_MODULE_2);
//            add(MicroserviceName.RESOURCE_ANALYSIS_MODULE_2);
//            add(MicroserviceName.NOTIFICATION_MODULE_1);
//            add(MicroserviceName.MONITORING_WOA_MODULE_1);
//            add(MicroserviceName.ACTION_PLAN_MODULE_1);
//            add(MicroserviceName.PROD_SCHEDULE_MODULE_1);
//            add(MicroserviceName.GATEWAY_MODULE_1);
//            add(MicroserviceName.SENSOR_MODULE_1);
//            add(IoTDeviceName.MONITOR_ACTUATION);
//        }});

        // Loop 2
//        final AppLoop loop2 = new AppLoop(new ArrayList<>() {{
//            add(IoTDeviceName.T_SENSOR);
//            add(MicroserviceName.SENSOR_MODULE);
//            add(MicroserviceName.RESOURCE_INTERFACE_MODULE);
//            add(MicroserviceName.RESOURCE_ANALYSIS_MODULE);
//            add(MicroserviceName.NOTIFICATION_MODULE);
//            add(MicroserviceName.MONITORING_WOA_MODULE);
//            add(MicroserviceName.ACTION_PLAN_MODULE);
//            add(MicroserviceName.PROD_SCHEDULE_MODULE);
//            add(MicroserviceName.SENSOR_MODULE);
//            add(IoTDeviceName.MONITOR_ACTUATION);
//        }});

        List<AppLoop> loops = new ArrayList<AppLoop>();
        if (SimulationParameters.placementType == PlacementType.CLOUD_ONLY){
            loops.add(loop3);
          //  loops.add(loop4);
            milestone.setLoopId(loop3.getLoopId());
          //  milestone.setLoopId(loop4.getLoopId());
        }else {
            loops.add(loop1);
          //  loops.add(loop2);
            milestone.setLoopId(loop1.getLoopId());
          //  milestone.setLoopId(loop2.getLoopId());
        }
        milestone.setLoops(loops);


        // Recommending the gateway Module in the gateway intermediate device
        milestone.setSpecialPlacementInfo(MicroserviceName.GATEWAY_MODULE_1, DeviceNames.EdgeGateway);

        //  if (CLOUD) {




        // By default, Cloud should have all the microservices
        milestone.setSpecialPlacementInfo(MicroserviceName.ACTION_PLAN_MODULE_1, DeviceNames.Cloud);
        milestone.setSpecialPlacementInfo(MicroserviceName.PROD_SCHEDULE_MODULE_1, DeviceNames.Cloud);
        //   }


        // Microservices that need to be placed in the CF
        if (SimulationParameters.placementType == PlacementType.CLOUD_ONLY){
            milestone.setSpecialPlacementInfo(MicroserviceName.ANOMALY_DETECTION_MODULE_1, DeviceNames.Cloud);
            milestone.setSpecialPlacementInfo(MicroserviceName.RESOURCE_ANALYSIS_MODULE_1, DeviceNames.Cloud);
            milestone.setSpecialPlacementInfo(MicroserviceName.RESOURCE_INTERFACE_MODULE_1, DeviceNames.Cloud);
//            milestone.setSpecialPlacementInfo(MicroserviceName.RESOURCE_ANALYSIS_MODULE_2, DeviceNames.Cloud);
//            milestone.setSpecialPlacementInfo(MicroserviceName.RESOURCE_INTERFACE_MODULE_2, DeviceNames.Cloud);
            milestone.setSpecialPlacementInfo(MicroserviceName.NOTIFICATION_MODULE_1, DeviceNames.Cloud);
            milestone.setSpecialPlacementInfo(MicroserviceName.MONITORING_WOA_MODULE_1, DeviceNames.Cloud);
        }else {
            milestone.toPlaceInCFs.add(MicroserviceName.ANOMALY_DETECTION_MODULE_1);
            milestone.toPlaceInCFs.add(MicroserviceName.RESOURCE_ANALYSIS_MODULE_1);
            milestone.toPlaceInCFs.add(MicroserviceName.RESOURCE_INTERFACE_MODULE_1);
//            milestone.toPlaceInCFs.add(MicroserviceName.RESOURCE_ANALYSIS_MODULE_2);
//            milestone.toPlaceInCFs.add(MicroserviceName.RESOURCE_INTERFACE_MODULE_2);
            milestone.toPlaceInCFs.add(MicroserviceName.NOTIFICATION_MODULE_1);
            milestone.toPlaceInCFs.add(MicroserviceName.MONITORING_WOA_MODULE_1);
        }
//        milestone.toPlaceInCFs.add(MicroserviceName.ACTION_PLAN_MODULE);
//        milestone.toPlaceInCFs.add(MicroserviceName.PROD_SCHEDULE_MODULE);

        milestone.createDAG();
        return milestone;
    }

    private static MasterFogDevice createFogDevice(String nodeName, long mips,
                                                         int ram, long storage, long bw, long upBw, long downBw,
                                                         int level,
                                                         double ratePerMips, double busyPower, double idlePower, String deviceType) {

        List<Pe> peList = new ArrayList<Pe>();

        // 3. Create PEs and add these into a list.
        peList.add(new Pe(0, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating

        int hostId = FogUtils.generateEntityId();

        PowerHost host = new PowerHost(
                hostId,
                new RamProvisionerSimple(ram),
                new BwProvisionerOverbooking(bw),
                storage,
                peList,
                new StreamOperatorScheduler(peList),
                new FogLinearPowerModel(busyPower, idlePower)
        );

        List<Host> hostList = new ArrayList<Host>();
        hostList.add(host);

        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
        // devices by now

        FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
                arch, os, vmm, host, time_zone, cost, costPerMem,
                costPerStorage, costPerBw);

        MasterFogDevice fogdevice = null;
        try {
            fogdevice = new MasterFogDevice(nodeName, characteristics,
                    new AppModuleAllocationPolicy(hostList), storageList, 10, upBw, downBw, 1250000, 0, ratePerMips, deviceType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fogdevice.setLevel(level);
        return fogdevice;
    }


}
