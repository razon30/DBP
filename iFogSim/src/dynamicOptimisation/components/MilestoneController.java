package dynamicOptimisation.components;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.entities.*;
import org.fog.placement.MicroservicePlacementLogic;
import org.fog.placement.MicroservicesController;
import org.fog.utils.*;
import dynamicOptimisation.model.Microservice;
import dynamicOptimisation.utils.PlacementType;
import dynamicOptimisation.utils.SimulationParameters;
import dynamicOptimisation.utils.UtilFactory;

import java.io.*;
import java.util.*;

public class MilestoneController extends MicroservicesController {

    List<Milestone> milestones = new ArrayList<>();

    public static MilestoneController createController(String name, List<FogDevice> fogDevices, List<Sensor> sensors,
                                                       List<Milestone> milestones,
                                                       List<Integer> clusterLevels, Double clusterLatency, int placementLogic) {

        return new MilestoneController(name, fogDevices, sensors, new ArrayList<>(milestones), clusterLevels, clusterLatency, placementLogic);

    }

    public MilestoneController(String name, List<FogDevice> fogDevices, List<Sensor> sensors, List<Application> applications, List<Integer> clusterLevels, Double clusterLatency, int placementLogic) {
        super(name, fogDevices, sensors, applications, clusterLevels, clusterLatency, placementLogic);
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }

    protected void initializeControllers(int placementLogic) {
        for (FogDevice device : fogDevices) {
            LoadBalancer loadBalancer = new RRLoadBalancer();
            MicroserviceFogDevice cdevice = (MicroserviceFogDevice) device;

            //responsible for placement decision making
            if (cdevice.getDeviceType().equals(MicroserviceFogDevice.FON) || cdevice.getDeviceType().equals(MicroserviceFogDevice.CLOUD)) {
                List<FogDevice> monitoredDevices = getDevicesForFON(cdevice);//, fogDevices); // Lists all the devices
                // orchestrated
                // by cdevice. If it's parent is Cloud, cdevice is also included in the list.
                MicroservicePlacementLogic microservicePlacementLogic = placementLogicFactory.getPlacementLogic(placementLogic, cdevice.getId());
                cdevice.initializeController(loadBalancer, microservicePlacementLogic, getResourceInfo(monitoredDevices), applications, monitoredDevices);
            } else if (cdevice.getDeviceType().equals(MicroserviceFogDevice.FCN) || cdevice.getDeviceType().equals(MicroserviceFogDevice.CLIENT)) {
                cdevice.initializeController(loadBalancer, applications, fogDevices);
            }
        }
    }

    protected void initializeControllers(int placementLogic, Map<Integer, List<FogDevice>> monitored) {
        for (FogDevice device : fogDevices) {
            LoadBalancer loadBalancer = new RRLoadBalancer();
            MicroserviceFogDevice cdevice = (MicroserviceFogDevice) device;

            //responsible for placement decision making
            if (cdevice.getDeviceType().equals(MicroserviceFogDevice.FON)) { //|| cdevice.getDeviceType().equals
                // (MicroserviceFogDevice.CLOUD)) {
                List<FogDevice> monitoredDevices = monitored.get(cdevice.getFonId());
                MicroservicePlacementLogic microservicePlacementLogic = placementLogicFactory.getPlacementLogic(placementLogic, cdevice.getId());
                // Following line of code orchestrates the devices monitored by the Orchestration device of cdevice.
                // This cdevice is also orchestrated here.
                // We will have to integrate our own Load balancer and placement login.
                cdevice.initializeController(loadBalancer, microservicePlacementLogic, getResourceInfo(monitoredDevices), applications, monitoredDevices);
            } else if (cdevice.getDeviceType().equals(MicroserviceFogDevice.FCN) || cdevice.getDeviceType().equals(MicroserviceFogDevice.CLIENT)) {
                cdevice.initializeController(loadBalancer, applications, fogDevices);
            }
        }
    }


    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case FogEvents.STOP_SIMULATION:
                CloudSim.stopSimulation();
                printTimeDetails();
                printPowerDetails();
                printCostDetails();
                printNetworkUsageDetails();
                printWaitingList();

                System.out.println("=========================================");
                System.out.println("============== "+SimulationParameters.numberOfTUpleDelegatedToCloud.size()+" " +
                        "==================");
                System.out.println("=========================================");

                saveAllDataToExcel();
                //printQoSDetails();

//                for (int i=0; i<5; i++){
//                    //CloudSim.runStart();
//                    CloudSim.startSimulation();
//                    CloudSim.stopSimulation();
//
//                    if (i == SimulationParameters.numberOfSimulation){
//                        System.exit(0);
//                        break;
//                    }
//                }

                System.exit(0);
                break;

        }
    }

    private void printWaitingList() {

        Map<String, Integer> microserviceWiseWaitingCounter = new HashMap<>();

        for (String microserviceName: SimulationParameters.serviceWaitingAtDeviceWithCount.keySet()){

            int counter  = 0;

            Map<String, Map<Integer, Integer>> deviceCount =
                    SimulationParameters.serviceWaitingAtDeviceWithCount.get(microserviceName);
            for (String deviceName: deviceCount.keySet()){
                Map<Integer, Integer> tupleCounter = deviceCount.get(deviceName);

                for (int tupleId: tupleCounter.keySet()){
                     counter += tupleCounter.get(tupleId);

//                    System.out.println("Microservice "+microserviceName+ " request "+tupleId+" waited in device "+deviceName+" for "+counter+
//                            " times.");

                }

                System.out.println("Microservice "+microserviceName+ " waited in device "+deviceName+" for "+counter+
                        " times.");

            }


//            System.out.println("Microservice "+microserviceName+ " waited in on average for "+(counter / deviceCount.size())+ " " +
//                    "times per device");

        }

        for (int id: SimulationParameters.actualTupleInWaiting.keySet()){



        }

    }


    private void saveAllDataToExcel() {

        /* TODO: Next thing to do for the Cloud-Fog FCFS placement and RR load balancing:
        1. Checking the cost in cloud. It should increase with request.
        2. Latency for other services. The delay should increase for request, atleast for the new modules going to cloud
        3.
         */

        double overallNetworkDelay = 0;
        double applicationExecutionTime =
                Calendar.getInstance().getTimeInMillis() - TimeKeeper.getInstance().getSimulationStartTime();
        long numberOfRequest = SimulationParameters.tupleCounter;
        double successfulRequestRate = (SimulationParameters.successfullyReachedLastActuator / numberOfRequest) * 100;
        successfulRequestRate = UtilFactory.getFormattedDouble(successfulRequestRate);

        System.out.println("=========================================");
        System.out.println("============== RESULTS ==================");
        System.out.println("=========================================");

        System.out.println("Number of Request: " + numberOfRequest);
        System.out.println("Number of Request successfully executed: " + SimulationParameters.successfullyReachedLastActuator);
        System.out.println("Total CPU Execution time: " + applicationExecutionTime + "ms");

        StringBuilder stringBuilder = new StringBuilder();

        //Map<String, Double> microserviceRequestExecutionTime = new HashMap<>();
//        for (String microservice : UtilFactory.microserviceToProcessingTuple.keySet()) {
//            double delayTime = 0;
//            for (String tupleType : TimeKeeper.getInstance().getTupleTypeToAverageCpuTime().keySet()) {
//                for (String requestType : UtilFactory.microserviceToProcessingTuple.get(microservice)) {
//                    if (tupleType.equals(requestType)) {
//                        delayTime += TimeKeeper.getInstance().getTupleTypeToAverageCpuTime().get(tupleType);
//                    }
//                }
//
//              //  microserviceRequestExecutionTime.put(microservice, executionTime);
//            }
//        }

        Map<String, Double> loopExecutionTime = new HashMap<>();
        for (Integer loopId : TimeKeeper.getInstance().getLoopIdToTupleIds().keySet()) {
            loopExecutionTime.put(getStringForLoopId(loopId),
                    TimeKeeper.getInstance().getLoopIdToCurrentAverage().get(loopId));
        }


        for (Milestone milestone : milestones) {

            stringBuilder.append("\n==========Milestone specific result============\n");
            stringBuilder.append("\nMilestone: " + milestone.getAppId());

            String loopModule = getStringForLoopId(milestone.getLoopId().get(0));
            if (loopExecutionTime.keySet().contains(loopModule)) {
                milestone.totalDelay = loopExecutionTime.get(loopModule);
                stringBuilder.append("\nTotal execution delay: " + milestone.totalDelay + " ms");
                overallNetworkDelay += milestone.totalDelay;
            }

            Map<String, Double> tupleTypeToTime =
                    SimulationParameters.appIdToTupleTypeToTime.get(milestone.getAppId());
            Map<String, Double> microserviceRequestExecutionTime = new HashMap<>();
            for (String microservice : UtilFactory.microserviceToProcessingTuple.keySet()) {
                double executionTime = 0;
                for (String requestType : UtilFactory.microserviceToProcessingTuple.get(microservice)) {

                    if (tupleTypeToTime.keySet().contains(requestType)) {
                        executionTime += tupleTypeToTime.get(requestType);
                    }

                }
                microserviceRequestExecutionTime.put(microservice, executionTime);

//                for (String tupleType : TimeKeeper.getInstance().getTupleTypeToAverageCpuTime().keySet()) {
//                    for (String requestType : UtilFactory.microserviceToProcessingTuple.get(microservice)) {
//                        if (tupleType.equals(requestType)) {
//                            executionTime += TimeKeeper.getInstance().getTupleTypeToAverageCpuTime().get(tupleType);
//                        }
//                    }
//                    microserviceRequestExecutionTime.put(microservice, executionTime);
//                }
            }


            double milestoneExecutionTime = 0;
            for (AppModule microservice : milestone.getModules()) {
                if (microserviceRequestExecutionTime.keySet().contains(microservice.getName())) {
                    milestoneExecutionTime += microserviceRequestExecutionTime.get(microservice.getName());
                }
            }
            milestone.averageExecutionTime = milestoneExecutionTime;
            stringBuilder.append("\nAverage Request execution time: " + milestoneExecutionTime + " ms");

       //     double achievedThroughput = (1 / applicationExecutionTime) * 1000;
            double achievedThroughput = (SimulationParameters.maxTupleNumber / applicationExecutionTime) * 1000; //(1 /
            // milestoneExecutionTime) * 1000;
            stringBuilder.append("\nAchieved Throughput: " + UtilFactory.getFormattedDouble(achievedThroughput) +
                    " /s");
            stringBuilder.append("\nRequired Throughput: " + milestone.getRequiredMilestoneThroughput() + " /s");
            stringBuilder.append("\nIs throughput achieved?: " + (achievedThroughput >= milestone.getRequiredMilestoneThroughput()));

            stringBuilder.append("\n\n==========Microservice specific result============");


            Map<Integer, Map<String, Double>> tupleIdToTupleTypeToTime =
                    SimulationParameters.appIdToTupleIdToTupleTypeToTime.get(milestone.getAppId());
            for (int tupleId : tupleIdToTupleTypeToTime.keySet()) {
                Map<String, Double> tupleTypeToTimeNew = tupleIdToTupleTypeToTime.get(tupleId);

             //   double reRes = 0;
                for (Microservice microservice : milestone.microservices) {

                    if (UtilFactory.microserviceToProcessingTuple.containsKey(microservice.getName())) {
                        double executionTime = 0;

                        for (String requestType : UtilFactory.microserviceToProcessingTuple.get(microservice.getName())) {
//
//                            for (Microservice microservice1 : milestone.microservices) {
//
//                                if (microservice1.getName().equals(requestType)){
//                                    reRes += microservice1.getRequiredResponsiveness();
//                                    System.out.println("Microservice name: "+microservice1.getName()+" and res is: "+reRes);
//                                }
//
//                            }

                            if (tupleTypeToTimeNew.containsKey(requestType)) {
                                executionTime += tupleTypeToTimeNew.get(requestType);
                            }

                        }


                        double maxReceiveidDelay = SimulationParameters.moduleToNetworkDelays.get(microservice.getName()).getFirst();
                        double maxSendDelay = SimulationParameters.moduleToNetworkDelays.get(microservice.getName()).getSecond();
                        if (UtilFactory.microserviceToInternalServiceMap.containsKey(microservice.getName())) {

                            List<String> internalModules =
                                    UtilFactory.microserviceToInternalServiceMap.get(microservice.getName());


                            for (String interModule : internalModules) {
                                if (SimulationParameters.moduleToNetworkDelays.containsKey(interModule)) {
                                    maxReceiveidDelay += SimulationParameters.moduleToNetworkDelays.get(interModule).getFirst();
                                    maxSendDelay += SimulationParameters.moduleToNetworkDelays.get(interModule).getSecond();
                                    if (SimulationParameters.placementType == PlacementType.CLOUD_ONLY && microservice.getName().contains("Monitoring")) {
                                        maxReceiveidDelay += SimulationParameters.gatewayDelayToCloud;
                                    }
                                } else {
                                    //System.out.println(interModule);
                                }
                            }
                        }

                        if (microservice.getName().contains("Schedule")) {
                            executionTime += maxSendDelay;
                        }else {
                            executionTime += maxReceiveidDelay;
                        }

                        if (microservice.maximumExecutionTime < executionTime){
                            microservice.maximumExecutionTime = executionTime;
                        }

                        if (microservice.minExecutionTime > executionTime){
                            microservice.minExecutionTime = executionTime;
                        }

//                        if (microservice.getName().contains(MicroserviceName.RESOURCE_ANALYSIS_MODULE)){
//                            microservice.setRequiredResponsiveness(microservice.getRequiredResponsiveness() * 3);
//                        }

                        if (microservice.getRequiredResponsiveness() >= (executionTime)) {
                            microservice.numberOfTimeResponsivenessMet++;
                        } else {
                            microservice.numberOfTimeResponsivenessDidNotMet++;
                        }

//                        reRes = 0;
//                        System.out.println("Microservice name: "+microservice.getName()+" and res is: "+reRes);

                    }
                }
            }


            for (Microservice microservice : milestone.microservices) {
                if (UtilFactory.microserviceToProcessingTuple.containsKey(microservice.getName())) {
                    double delayTime = 0;
                    for (String tupleType : TimeKeeper.getInstance().getTupleTypeToAverageCpuTime().keySet()) {
                        for (String requestType : UtilFactory.microserviceToProcessingTuple.get(microservice.getName())) {
                            if (tupleType.equals(requestType)) {
                                delayTime += TimeKeeper.getInstance().getTupleTypeToAverageCpuTime().get(tupleType);
                            }
                        }

                        //  microserviceRequestExecutionTime.put(microservice, executionTime);
                    }

                    microservice.averageDelayFaced = delayTime;
                }
            }


            for (Microservice microservice : milestone.microservices) {
//                if (microserviceRequestExecutionTime.keySet().contains(microservice)) {
//                    stringBuilder.append("\nAverage Execution time for Microservice: " + microservice + " is: " + microserviceRequestExecutionTime.get(microservice));
//                }

//                double networkDelay = 0;
//
//                for (FogDevice fogDevice : fogDevices) {
//                   if (fogDevice.getPlacedAppModulesPerApplication(milestone.getAppId()).contains(microservice.getName())){
//                       networkDelay = fogDevice.getUplinkLatency();
//                   }
//                }

                if (UtilFactory.microserviceToInternalServiceMap.containsKey(microservice.getName())) {

                    List<String> internalModules =
                            UtilFactory.microserviceToInternalServiceMap.get(microservice.getName());

                    double maxReceiveidDelay = SimulationParameters.moduleToNetworkDelays.get(microservice.getName()).getFirst();
                    double maxSendDelay = SimulationParameters.moduleToNetworkDelays.get(microservice.getName()).getSecond();
                    for (String interModule: internalModules){
                        if (SimulationParameters.moduleToNetworkDelays.containsKey(interModule)) {
                            maxReceiveidDelay += SimulationParameters.moduleToNetworkDelays.get(interModule).getFirst();
                            maxSendDelay += SimulationParameters.moduleToNetworkDelays.get(interModule).getSecond();
                            if (SimulationParameters.placementType == PlacementType.CLOUD_ONLY && microservice.getName().contains("Monitoring")){
                                maxReceiveidDelay += SimulationParameters.gatewayDelayToCloud;
                            }
                        }else {
                            System.out.println(interModule);
                        }
                    }


                    stringBuilder.append("\n\nMicroservice: " + microservice.getName());
                   // stringBuilder.append("\n\tAverage delay faced: " + microservice.averageDelayFaced +" ms");
                    stringBuilder.append("\n\tNumber of instances created: " + UtilFactory.microserviceTypeToInstanceNumber.get(microservice.getName()));
                    stringBuilder.append("\n\tMax delay faced to receive data: " + maxReceiveidDelay + "ms");
                    stringBuilder.append("\n\tMax delay faced in sending data: " + maxSendDelay + "ms");
                    stringBuilder.append("\n\tAverage Execution time: " +
                            ((microservice.minExecutionTime + microservice.maximumExecutionTime) / 2) +" ms");
                    stringBuilder.append("\n\tMinimum Execution time: " + microservice.minExecutionTime +" ms");
                    stringBuilder.append("\n\tMaximum Execution time: " + microservice.maximumExecutionTime +" ms");
                    stringBuilder.append("\n\tRequired responsiveness within " + microservice.getRequiredResponsiveness() +" ms");
                    stringBuilder.append("\n\tMeet responsiveness " +
                            "requirement:" + microservice.numberOfTimeResponsivenessMet);
                    stringBuilder.append("\n\tdid not meet responsiveness " +
                            "requirement:" + microservice.numberOfTimeResponsivenessDidNotMet);
                }
            }
        }

        stringBuilder.append("\n");
//        Map<String, Double> fogDeviceEnergyConsumptions = new HashMap<>();
//        for (FogDevice fogDevice : fogDevices) {
//            fogDeviceEnergyConsumptions.put(fogDevice.getName(), fogDevice.getEnergyConsumption());
//            stringBuilder.append("\nEnergy consumed in " + fogDevice.getName() + ": " + fogDevice.getEnergyConsumption());
//        }

        double costInCloud = getCloud().getTotalCost();
        double networkUsed = NetworkUsageMonitor.getNetworkUsage() / Config.MAX_SIMULATION_TIME;
        stringBuilder.append("\n\nCost In Cloud: " + costInCloud);
        stringBuilder.append("\nNetwork used: " + networkUsed);

        System.out.println("Number of requests waiting in the running state of the application "+SimulationParameters.requestInWaiting);
        System.out.println("Overall Application Network Delay: " + overallNetworkDelay);
        System.out.println("Percentage of Successfully processed request: " + successfulRequestRate);


//        savingDataToExcel(numberOfRequest, applicationExecutionTime, SimulationParameters.CLOUD_FOG_SIMULATION,
//                SimulationParameters.COMPARING_EXECUTION_TIME);
//        savingDataToExcel(numberOfRequest, overallNetworkDelay, SimulationParameters.CLOUD_FOG_SIMULATION, SimulationParameters.COMPARING_NETWORK_DELAY);
//        savingDataToExcel(numberOfRequest, SimulationParameters.requestInWaiting, SimulationParameters.CLOUD_ONLY_SIMULATION, SimulationParameters.COMPARING_NUMBER_OF_REQUEST_IN_WAITING); // Column choosen Cloud Only because its value is 1 and we want to value in column = 1 as well.


        System.out.println(stringBuilder);

    }

    private void savingDataToExcel(long numberOfRequest, double dataValue, int simulationType, int sheetNumber) {

        try {

            File excel = new File("/Users/razon/Library/CloudStorage/OneDrive-QueenslandUniversityofTechnology/Publications/Journal/Static_Optimisation/Results/Results.xlsx");

            if (!excel.exists()){

            }

            FileInputStream file = new FileInputStream(excel);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(sheetNumber);

            int rowCount = sheet.getLastRowNum();

            for (int i=1; i<=rowCount; i++){

                if (i == rowCount){

                    Row row = sheet.createRow(i+1);

                    Cell cell_0 = row.createCell(0);
                    cell_0.setCellValue(numberOfRequest);

                    Cell cell_1 = row.createCell(simulationType);
                    cell_1.setCellValue(dataValue);

                }else {

                    Row row = sheet.getRow(i);
                    double existingNumberOfRequest = row.getCell(0).getNumericCellValue();
                    if (existingNumberOfRequest == numberOfRequest) {
                        Cell cell_1 = row.getCell(simulationType);
                        if (cell_1 == null){
                            cell_1 = row.createCell(simulationType);
                        }
                        cell_1.setCellValue(dataValue);
                        break;
                    }
                }
            }

            file.close();

            FileOutputStream outputStream = new FileOutputStream(excel.getAbsolutePath());
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
