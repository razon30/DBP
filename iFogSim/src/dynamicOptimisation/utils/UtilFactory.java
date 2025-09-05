package dynamicOptimisation.utils;

import org.cloudbus.cloudsim.UtilizationModelFull;
import org.fog.application.AppEdge;
import org.fog.entities.MicroserviceFogDevice;
import org.fog.entities.Tuple;
import org.fog.utils.FogUtils;
import org.fog.utils.TimeKeeper;
import dynamicOptimisation.NSGAII.Individual;
import dynamicOptimisation.components.Milestone;
import dynamicOptimisation.utils.EntityName.EventName;
import dynamicOptimisation.utils.EntityName.IoTDeviceName;
import dynamicOptimisation.utils.EntityName.MicroserviceName;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UtilFactory {

    public static Map<Integer, MicroserviceFogDevice> deviceIdToDeviceMap = new HashMap<>();
    public static Map<String, ArrayList<String>> microserviceToProcessingTuple = getServiceToTupleMap();
    public static Map<String, ArrayList<String>> microserviceToInternalServiceMap = getMicroserviceToInternalServiceMap();

    public static Map<String, ArrayList<String>> microserviceTypeToInstance = new HashMap<>();
    public static Map<String, Integer> microserviceTypeToInstanceNumber = new HashMap<>();

    public static int numberOfRequestReceivedByActuator = 0;

    private static Map<String, ArrayList<String>> getServiceToTupleMap() {

        Map<String, ArrayList<String>> serviceToTupleMap = new HashMap<>();

        ArrayList<String> anomalyDetection = new ArrayList<>();
//        anomalyDetection.add(IoTDeviceName.V_SENSOR);
//        anomalyDetection.add(EventName.PROCESSED_V_SENSOR);
        anomalyDetection.add(EventName.FIRE_INCIDENT_DETECTION);
        serviceToTupleMap.put(MicroserviceName.ANOMALY_DETECTION_MODULE_1, anomalyDetection);

        ArrayList<String> resourceEventAnalysis = new ArrayList<>();
        resourceEventAnalysis.add(EventName.RESOURCE_ANALYSIS);
        resourceEventAnalysis.add(EventName.REQUEST_SENSOR_DATA);
//        resourceEventAnalysis.add(IoTDeviceName.T_SENSOR);
//        resourceEventAnalysis.add(EventName.PROCESSED_T_SENSOR);
        resourceEventAnalysis.add(EventName.RESOURCE_REPORT);
        serviceToTupleMap.put(MicroserviceName.RESOURCE_ANALYSIS_MODULE_1, resourceEventAnalysis);

        ArrayList<String> workOrderActioning = new ArrayList<>();
        workOrderActioning.add(EventName.WORK_ORDER);
        serviceToTupleMap.put(MicroserviceName.MONITORING_WOA_MODULE_1, workOrderActioning);

        ArrayList<String> actionPlanExecution = new ArrayList<>();
        actionPlanExecution.add(EventName.ACTION_PLAN);
        serviceToTupleMap.put(MicroserviceName.ACTION_PLAN_MODULE_1, actionPlanExecution);

        ArrayList<String> prodScheduleUpdate = new ArrayList<>();
        prodScheduleUpdate.add(EventName.PROD_SCHEDULE);
        serviceToTupleMap.put(MicroserviceName.PROD_SCHEDULE_MODULE_1, prodScheduleUpdate);

        return serviceToTupleMap;
    }

    private static Map<String, ArrayList<String>> getMicroserviceToInternalServiceMap() {

        Map<String, ArrayList<String>> microserviceToInternatServiceMap = new HashMap<>();

        ArrayList<String> anomalyDetection = new ArrayList<>();
        anomalyDetection.add(IoTDeviceName.V_SENSOR);
        anomalyDetection.add(MicroserviceName.SENSOR_MODULE_1);
        microserviceToInternatServiceMap.put(MicroserviceName.ANOMALY_DETECTION_MODULE_1, anomalyDetection);

        ArrayList<String> resourceEventAnalysis = new ArrayList<>();
        resourceEventAnalysis.add(MicroserviceName.RESOURCE_INTERFACE_MODULE_1);
        resourceEventAnalysis.add(MicroserviceName.RESOURCE_INTERFACE_MODULE_2);
        resourceEventAnalysis.add(MicroserviceName.RESOURCE_ANALYSIS_MODULE_2);
        resourceEventAnalysis.add(MicroserviceName.SENSOR_MODULE_1);
        resourceEventAnalysis.add(IoTDeviceName.SDA_ACTUATION);
        resourceEventAnalysis.add(IoTDeviceName.T_SENSOR);
        resourceEventAnalysis.add(MicroserviceName.SENSOR_MODULE_1);
        microserviceToInternatServiceMap.put(MicroserviceName.RESOURCE_ANALYSIS_MODULE_1, resourceEventAnalysis);

        ArrayList<String> workOrderActioning = new ArrayList<>();
        workOrderActioning.add(MicroserviceName.NOTIFICATION_MODULE_1);
        microserviceToInternatServiceMap.put(MicroserviceName.MONITORING_WOA_MODULE_1, workOrderActioning);

        ArrayList<String> actionPlanExecution = new ArrayList<>();
        microserviceToInternatServiceMap.put(MicroserviceName.ACTION_PLAN_MODULE_1, actionPlanExecution);

        ArrayList<String> prodScheduleUpdate = new ArrayList<>();
        prodScheduleUpdate.add(MicroserviceName.SENSOR_MODULE_1);
        prodScheduleUpdate.add(IoTDeviceName.MONITOR_ACTUATION);
        microserviceToInternatServiceMap.put(MicroserviceName.PROD_SCHEDULE_MODULE_1, prodScheduleUpdate);

        return microserviceToInternatServiceMap;
    }


    public static double getValue(double min, double max) {
        Random rn = new Random();
        return min + (max - min) * rn.nextDouble();
    }

    public static Long getValue(Long min, Long max) {
        Random rn = new Random();
        return min + (max - min) * rn.nextLong();
    }

    public static int getValue(int min, int max) {
        // Random rn = new Random();
        return (int) (min + (Math.random() * (max - min)));// min + (max - min) * rn.nextInt();
    }

    public static void print(Object object) {
        System.out.println(object);
    }

    public static double getValue(double min) {
        Random rn = new Random();
        return rn.nextDouble() * 10 + min;
    }

    public static int getValue(int min) {
        Random rn = new Random();
        return rn.nextInt() * 10 + min;
    }

    public static double getFormattedDouble(double number) {

        // Create a DecimalFormat instance with the pattern "#.##" to format the number with two decimal points
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        // Format the originalValue to two decimal points
        String formattedValue = decimalFormat.format(number);

        // Parse the formatted value back to a double
        return Double.parseDouble(formattedValue);

    }

    public static void setServiceInWaiting(String microserviceName, String deviceName, int tupleId){

        Map<String, Map<Integer, Integer>> deviceCount = new HashMap<>();
        if (!SimulationParameters.serviceWaitingAtDeviceWithCount.keySet().contains(microserviceName)){
            Map<Integer, Integer> tupleCount = new HashMap<>();
            tupleCount.put(tupleId, 1);
            deviceCount.put(deviceName, tupleCount);
        }else {
            deviceCount =
                    SimulationParameters.serviceWaitingAtDeviceWithCount.get(microserviceName);
            if (!deviceCount.keySet().contains(deviceName)){
                Map<Integer, Integer> tupleCount = new HashMap<>();
                tupleCount.put(tupleId, 1);
                deviceCount.put(deviceName, tupleCount);
            }else {

                Map<Integer, Integer> tupleCount = deviceCount.get(deviceName);

                if (!tupleCount.keySet().contains(tupleId)){
                    tupleCount.put(tupleId, 1);
                    deviceCount.put(deviceName, tupleCount);
                }else {

                    int counter = tupleCount.get(tupleId);
                    tupleCount.put(tupleId, counter+1);
                    deviceCount.put(deviceName, tupleCount);
                }
            }
        }
        SimulationParameters.serviceWaitingAtDeviceWithCount.put(microserviceName, deviceCount);

    }

    public static Tuple getTuple(Milestone milestone, int deviceId) {
        AppEdge _edge = null;
        for (AppEdge edge : milestone.getEdges()) {
            if (edge.getSource().equals(EventName.PROCESSED_V_SENSOR))
                _edge = edge;
        }
        long cpuLength = (long) _edge.getTupleCpuLength();
        long nwLength = (long) _edge.getTupleNwLength();

        Tuple tuple = new Tuple(milestone.getAppId(), FogUtils.generateTupleId(), Tuple.UP, cpuLength, 1, nwLength, 3,
                new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
        tuple.setUserId(milestone.getUserId());
        tuple.setTupleType(_edge.getTupleType());

        tuple.setDestModuleName(_edge.getDestination());
        tuple.setSrcModuleName(_edge.getSource());
       // Logger.debug(getName(), "Sending tuple with tupleId = " + tuple.getCloudletId());

        tuple.setDestinationDeviceId(deviceId);

        int actualTupleId =
                TimeKeeper.getInstance().getUniqueId();//milestone.sensorList.get(sensorNumber).updateTimings
        // (milestone.sensorList.get(sensorNumber).getSensorName(), tuple.getDestModuleName());
        tuple.setActualTupleId(actualTupleId);
        tuple.setSourceModuleId(milestone.sensorList.get(0).getId());

        return  tuple;
    }

    public static double getGaussianStandardDeviation(Individual individual) {
        int sum = 0;
        for (int deviceId: individual.microserviceToDeviceMapping.values()){
            sum += deviceId;
        }
        return sum / 5.0;
    }

    public static void addMicroserviceToMilestone(String microservice, Milestone milestone) {
        int mips = 300 * UtilFactory.getValue(1, 3);
        int ram = 128 * UtilFactory.getValue(1, 3);
        int bw = 300 * UtilFactory.getValue(1, 5);
        int size = 200 * UtilFactory.getValue(1, 3);

        milestone.addMicroservice(microservice, ram, mips, size, bw);
    }
}
