package dynamicOptimisation.NSGAII;

import org.fog.application.AppEdge;
import org.fog.application.AppModule;
import org.fog.entities.FogDevice;
import org.fog.entities.MicroserviceFogDevice;
import org.fog.entities.Tuple;
import dynamicOptimisation.components.CloudFogMicroservicePlacementOptimise;
import dynamicOptimisation.components.Milestone;
import dynamicOptimisation.model.Microservice;
import dynamicOptimisation.utils.EntityName.IoTDeviceName;

import java.util.*;

public class Individual {

   // Map<Integer, List<String>> deviceToMicroserviceMap;

    public Map<String, Integer> microserviceToDeviceMapping;

    public double[] objectives;

    //public Resource[] availableResource = new Resource[SimulationParameters.numberOfCF];
    Map<Integer, Resource> availableResource;
    public int rank;
    public int dominationCount;
    public List<Individual> dominatedIndividuals;
    private double crowdingDistance;
    //boolean[] isMSPlaced;// = new boolean[Entities.msNumber];
    Map<String, Boolean> isMSPlaced;
    int adminPrioritisedService;
    //int milestoneId = -1;
    Milestone milestone;
    List<MicroserviceFogDevice> deviceList = new ArrayList<>();
    List<MicroserviceFogDevice> allDeviceList = new ArrayList<>();
    List<Microservice> microserviceList = new ArrayList<>();
    Priority priority;
    Resource requiredTotalResource;

    CloudFogMicroservicePlacementOptimise placementLogic;

    public Individual(CloudFogMicroservicePlacementOptimise placementLogic) {
        // Initialize the individual's attributes
        objectives = new double[3]; // Number of objectives
        //deviceToMicroserviceMap = new HashMap<>(); //int[Entities.milestones[milestoneId].getMicroservices().size()];
        microserviceToDeviceMapping = new HashMap<>();
        rank = 0;
        dominationCount = 0;
        dominatedIndividuals = new ArrayList<>();
        crowdingDistance = 0.0;
        adminPrioritisedService = 0;
        requiredTotalResource = new Resource(0, 0, 0, 0);

        isMSPlaced = new HashMap<>();
        this.placementLogic = placementLogic;
    }


    public void initialize() {

        availableResource = new HashMap<>();
        for (FogDevice fogDevice : placementLogic.fogDevices) {

            int id = fogDevice.getId();

            Resource resource = new Resource(fogDevice.getHost().getRam(), fogDevice.getHost().getStorage(),
                    fogDevice.getHost().getTotalMips(), fogDevice.getHost().getBw());

            availableResource.put(id, resource);

        }

        randomlyPlaceModules();

        //System.out.println(fogToMS);
        objectives[0] = -1;
        objectives[1] = 0;//milestone.getRequiredCompletionTime() + 100;
        objectives[2] = -1;
        //objectives[3] = -1;
//        objectives[4] = -1;
    }

    private void randomlyPlaceModules() {

        //deviceToMicroserviceMap = new HashMap<>();

        // Randomly place modules on Fog devices
        Random random = new Random();

        for (Microservice microservice : microserviceList) {
            int randomIndex = random.nextInt(deviceList.size());
            MicroserviceFogDevice fogDevice = deviceList.get(randomIndex);
            List<String> mappedMicroservice = new ArrayList<>();
//            if (deviceToMicroserviceMap.containsKey(fogDevice.getId()) && deviceToMicroserviceMap.get(fogDevice.getId()) != null) {
//                mappedMicroservice = deviceToMicroserviceMap.get(fogDevice.getId());
//            }
//            mappedMicroservice.add(microservice.getName());
//            deviceToMicroserviceMap.put(fogDevice.getId(), mappedMicroservice);
            microserviceToDeviceMapping.put(microservice.getName(), fogDevice.getId());
            isMSPlaced.put(microservice.getName(), false);
        }

    }


    public void evaluateFitness() {

        double timeToExecuteTheMilestone = 0;
        int numberOfMicroserviceCanEnsureResponsiveness = 0;
        int numberOfMicroserviceEnsureResourceRequirement = 0;
        int sumX = 0;
        Resource allocatedResource = new Resource(0, 0, 0, 0);

        ArrayList<Double> timeToExecuteTheActivities = new ArrayList<>();


        for (AppEdge edge : milestone.getEdges()) {

            String src = edge.getSource();
            String dest = edge.getDestination();


            if (src == IoTDeviceName.V_SENSOR || src == IoTDeviceName.T_SENSOR || src == IoTDeviceName.SDA_ACTUATION || src == IoTDeviceName.MONITOR_ACTUATION) {

                timeToExecuteTheMilestone += 5; // event transmission time
                //System.out.println("After Sensor transmission: "+timeToExecuteTheMilestone);

            } else {

                double cpuLength = edge.getTupleCpuLength();
                double nwLength = edge.getTupleNwLength();

                Microservice srcMicroservice = getModule(src, milestone);
                Microservice destMicroservice = getModule(dest, milestone);

//                if (!srcMicroservice.getName().equals(MicroserviceName.SENSOR_MODULE)
//                && !destMicroservice.getName().equals(MicroserviceName.SENSOR_MODULE)
//                        && !srcMicroservice.getName().equals(MicroserviceName.GATEWAY_MODULE)
//                        && !destMicroservice.getName().equals(MicroserviceName.GATEWAY_MODULE)
//                        && !srcMicroservice.getName().equals(MicroserviceName.ACTION_PLAN_MODULE)
//                        && !destMicroservice.getName().equals(MicroserviceName.ACTION_PLAN_MODULE)
//                        && !srcMicroservice.getName().equals(MicroserviceName.PROD_SCHEDULE_MODULE)
//                        && !destMicroservice.getName().equals(MicroserviceName.PROD_SCHEDULE_MODULE)) {


                    isMSPlaced.put(srcMicroservice.getName(), false);
                    boolean isDeadlineMet = false;
                    boolean isResourceRequirementMet = false;
                    double deadline = srcMicroservice.getRequiredResponsiveness();
                    double responsivenessOfSrcMS = 0;

                    MicroserviceFogDevice srcDevice = getDeviceFor(srcMicroservice);

                    double executionTime = cpuLength / (srcMicroservice.getMips() * srcMicroservice.getNumberOfPes());
                    double fileTransmissionTime = nwLength / srcMicroservice.getBw();

                    responsivenessOfSrcMS += executionTime;
                    timeToExecuteTheMilestone += executionTime;
                    timeToExecuteTheMilestone += fileTransmissionTime;

                    //System.out.println("From: "+ src + " to "+ dest + " execution and transmission time: "+ executionTime+fileTransmissionTime);

                    if (dest == IoTDeviceName.V_SENSOR || dest == IoTDeviceName.T_SENSOR || dest == IoTDeviceName.SDA_ACTUATION || dest == IoTDeviceName.MONITOR_ACTUATION) {
                        responsivenessOfSrcMS += srcDevice.getUplinkLatency(); // event transmission time
                        timeToExecuteTheMilestone += srcDevice.getUplinkLatency(); // event transmission time
                        //  System.out.println("From: "+ src + " to "+ dest + " DOWN latency: "+ srcDevice.getUplinkLatency());
                    } else {
                        MicroserviceFogDevice destDevice = getDeviceFor(destMicroservice);
                        if (!srcDevice.getName().equals(destDevice.getName())) {
                            if (edge.getDirection() == Tuple.DOWN) {
                                responsivenessOfSrcMS += destDevice.getUplinkLatency(); // network latency
                                timeToExecuteTheMilestone += destDevice.getUplinkLatency(); // network latency
                                //    System.out.println("From: "+ src + " to "+ dest + " DOWN latency: "+ destDevice.getUplinkLatency());
                            } else {
                                responsivenessOfSrcMS += srcDevice.getUplinkLatency();
                                timeToExecuteTheMilestone += srcDevice.getUplinkLatency();
                                //    System.out.println("From: "+ src + " to "+ dest + " UP latency: "+ srcDevice.getUplinkLatency());
                            }
                        }
                    }


                    if (responsivenessOfSrcMS <= deadline) {
                        numberOfMicroserviceCanEnsureResponsiveness++;
                        isDeadlineMet = true;
                    }

                    if (canSupportTheRequired(srcDevice, srcMicroservice)) {
                        numberOfMicroserviceEnsureResourceRequirement++;
                        isResourceRequirementMet = true;
                    }

                    updateResourceRequirement(srcMicroservice, allocatedResource);

                    // Deciding based on meeting the responsiveness deadline and resource requirement
                    if (isDeadlineMet && isResourceRequirementMet) {
                        isMSPlaced.put(srcMicroservice.getName(), true);
                        sumX++;
                    }
               // }
            }
        }

        // Objective 1: responsiveness
        objectives[0] = numberOfMicroserviceCanEnsureResponsiveness;

        // Objective 2: Milestone time to complete
        objectives[1] = timeToExecuteTheMilestone;

        // Need to consider the resource requirements


        // Objective 3: Number of MS placed in Fog devices.
        objectives[2] = sumX;

        //Objective 4: Maximise the admin prioritised number of microservices at the edge
        //objectives[3] = milestone.milestonePriority.getPriority();//placedAdminPrioritisedService;

        //Objective 5: Maximise the placement of high frequency microservice placement, stating that all the high
        // priority microservices are placed.
//        objectives[4] = adminRequestFrequency;

        //Objective 6: Tracking resource requirement
        requiredTotalResource = allocatedResource;

        //return timeToExecuteTheMilestone;
    }

    private boolean canSupportTheRequired(MicroserviceFogDevice srcDevice, Microservice srcMicroservice) {

        int id = srcDevice.getId();

        if (availableResource == null){
            System.out.println("resource is null");
        }

        if (availableResource.get(id).Storage >= srcMicroservice.getSize()
                && availableResource.get(id).RAM >= srcMicroservice.getRam()
                && availableResource.get(id).CPU >= srcMicroservice.getMips()
                && availableResource.get(id).BW >= srcMicroservice.getBw()) {

            availableResource.get(id).Storage = availableResource.get(id).Storage - srcMicroservice.getSize();
            availableResource.get(id).RAM = availableResource.get(id).RAM - srcMicroservice.getRam();
            availableResource.get(id).CPU = availableResource.get(id).CPU - srcMicroservice.getMips();
            availableResource.get(id).BW = availableResource.get(id).BW - srcMicroservice.getBw();

            return true;

        } else {
            return false;
        }


    }

    public int compareDominance(Individual other) {
        int dominanceCount1 = 0;
        int dominanceCount2 = 0;

        /*
        The individual dominates the other if:
        1. Higher number of high priority microservice placed. If equals, then
            1. Higher frequency-higher priority microservice placed. If equals, then
                1. Higher number of microservice placed. If equals, then
                    1. Faster completion of the milestone. If equals, then
                        1. Individuals with lower resource requirements is selected in the following fashion:
                            i. Requires less CPU
                            ii.Requires less RAM
                            iii. Requires less BW
                            iv. Requires less Storage
         */

//        if (priority.getPriority() < other.priority.getPriority()) {
//            dominanceCount1++;
//        } else if (priority.getPriority() > other.priority.getPriority()) {
//            dominanceCount2++;
//        } else {

//            if (milestone.frequency > other.milestone.frequency) {
//                dominanceCount1++;
//            } else if (milestone.frequency < other.milestone.frequency) {
//                dominanceCount2++;
//            }else {
        if (objectives[2] > other.objectives[2]) {
            dominanceCount1++;
        } else if (objectives[2] < other.objectives[2]) {
            dominanceCount2++;
        } else {
            if (objectives[1] < other.objectives[1]) {
                dominanceCount1++;
            } else if (objectives[1] > other.objectives[1]) {
                dominanceCount2++;
            } else {

                // Comparing resources:
                Resource otherResource = other.requiredTotalResource;

                if (requiredTotalResource.CPU < otherResource.CPU &&
                        requiredTotalResource.RAM < otherResource.RAM &&
                        requiredTotalResource.BW < otherResource.BW &&
                        requiredTotalResource.Storage < otherResource.Storage) {
                    dominanceCount1++;
                } else if (requiredTotalResource.CPU > otherResource.CPU &&
                        requiredTotalResource.RAM > otherResource.RAM &&
                        requiredTotalResource.BW > otherResource.BW &&
                        requiredTotalResource.Storage > otherResource.Storage) {
                    dominanceCount2++;
                } else {

                    if (requiredTotalResource.CPU < otherResource.CPU &&
                            requiredTotalResource.RAM < otherResource.RAM &&
                            requiredTotalResource.BW < otherResource.BW) {
                        dominanceCount1++;
                    } else if (requiredTotalResource.CPU > otherResource.CPU &&
                            requiredTotalResource.RAM > otherResource.RAM &&
                            requiredTotalResource.BW > otherResource.BW) {
                        dominanceCount2++;
                    } else {

                        if (requiredTotalResource.CPU < otherResource.CPU &&
                                requiredTotalResource.RAM < otherResource.RAM) {
                            dominanceCount1++;
                        } else if (requiredTotalResource.CPU > otherResource.CPU &&
                                requiredTotalResource.RAM > otherResource.RAM) {
                            dominanceCount2++;
                        } else {

                            if (requiredTotalResource.RAM < otherResource.RAM &&
                                    requiredTotalResource.BW < otherResource.BW &&
                                    requiredTotalResource.Storage < otherResource.Storage) {
                                dominanceCount1++;
                            } else if (requiredTotalResource.RAM > otherResource.RAM &&
                                    requiredTotalResource.BW > otherResource.BW &&
                                    requiredTotalResource.Storage > otherResource.Storage) {
                                dominanceCount2++;
                            } else {

                                if (requiredTotalResource.RAM < otherResource.RAM &&
                                        requiredTotalResource.BW < otherResource.BW) {
                                    dominanceCount1++;
                                } else if (requiredTotalResource.RAM > otherResource.RAM &&
                                        requiredTotalResource.BW > otherResource.BW) {
                                    dominanceCount2++;
                                } else {

                                    if (requiredTotalResource.RAM < otherResource.RAM &&
                                            requiredTotalResource.Storage < otherResource.Storage) {
                                        dominanceCount1++;
                                    } else if (requiredTotalResource.RAM > otherResource.RAM &&
                                            requiredTotalResource.Storage > otherResource.Storage) {
                                        dominanceCount2++;
                                    } else {

                                        if (requiredTotalResource.CPU < otherResource.CPU &&
                                                requiredTotalResource.BW < otherResource.BW) {
                                            dominanceCount1++;
                                        } else if (requiredTotalResource.CPU > otherResource.CPU &&
                                                requiredTotalResource.BW > otherResource.BW) {
                                            dominanceCount2++;
                                        } else {

                                            if (requiredTotalResource.CPU < otherResource.CPU) {
                                                dominanceCount1++;
                                            } else if (requiredTotalResource.CPU > otherResource.CPU) {
                                                dominanceCount2++;
                                            } else {
                                                if (requiredTotalResource.RAM < otherResource.RAM) {
                                                    dominanceCount1++;
                                                } else if (requiredTotalResource.RAM > otherResource.RAM) {
                                                    dominanceCount2++;
                                                } else {
                                                    if (requiredTotalResource.BW < otherResource.BW) {
                                                        dominanceCount1++;
                                                    } else if (requiredTotalResource.BW > otherResource.BW) {
                                                        dominanceCount2++;
                                                    }
                                                }
                                            }

                                        }
                                    }

                                }

                            }

                        }

                    }

                }


            }

//                }
            //  }


        }

        // Compare dominance counts
        // Individual 2 dominates individual 1
        // No dominance relationship, both individuals are non-dominated
        return Integer.compare(dominanceCount1, dominanceCount2); // Individual 1 dominates individual 2

    }


    private void updateResourceRequirement(Microservice srcMicroservice, Resource resource) {

        // updating the required resources by adding allocated resources
        resource.Storage = resource.Storage + srcMicroservice.getSize();
        resource.RAM = resource.RAM + srcMicroservice.getRam();
        resource.CPU = resource.CPU + srcMicroservice.getMips();
        resource.BW = resource.BW + srcMicroservice.getBw();

    }

    private MicroserviceFogDevice getDeviceFor(Microservice microservice) {

        MicroserviceFogDevice fogDevice = null;

        if (milestone.getSpecialPlacementInfo().get(microservice.getName()) != null) {
            List<String> placedDeviceNameList = milestone.getSpecialPlacementInfo().get(microservice.getName());
            fogDevice = (MicroserviceFogDevice) placementLogic.getDeviceByName(placedDeviceNameList.get(0));
        } else {

//            for (int deviceId : deviceToMicroserviceMap.keySet()) {
//
//                if (deviceToMicroserviceMap.get(deviceId).contains(microservice.getName())) {
//                    fogDevice = (MicroserviceFogDevice) getDevice(deviceId);
//                }
//
//            }
//
            if (microserviceToDeviceMapping.containsKey(microservice.getName())){
                fogDevice =
                        (MicroserviceFogDevice) placementLogic.getDevice(microserviceToDeviceMapping.get(microservice.getName()));
            }


            //        fogDevice = (MicroserviceFogDevice) placementLogic.getDevice(placementLogic.microserviceToDeviceListMap
            //        .get(microservice.getName()).get(0));

        }

        if (fogDevice == null) {
            System.out.println("Source is null");
        }

        return fogDevice;
    }

    public Microservice getModule(String moduleName, Milestone milestone) {
        for (AppModule appModule : milestone.getModules()) {
            if (appModule.getName().equals(moduleName))
                return (Microservice) appModule;
        }
        return null;
    }

    public FogDevice getDevice(int deviceId) {
        FogDevice fogDevice1 = null;
        for (FogDevice fogDevice : deviceList) {
            if (fogDevice.getId() == deviceId)
                fogDevice1 = fogDevice;
        }
        if (fogDevice1 == null) {
            System.out.println("Source is null");
        }
        return fogDevice1;
    }

    public FogDevice getDeviceByName(String deviceName) {
        for (FogDevice f : deviceList) {
            if (f.getName().equals(deviceName))
                return f;
        }
        return null;
    }

    public void setMilestone(Milestone milestone) {
        //this.milestoneId = milestone.getAppId();
        this.milestone = milestone;//Entities.milestones[milestoneId];
    }

    public int getDominationCount() {
        return dominationCount;
    }

    public void setDominationCount(int dominationCount) {
        this.dominationCount = dominationCount;
    }

    public List<Individual> getDominatedIndividuals() {
        return dominatedIndividuals;
    }

    public void setDominatedIndividuals(List<Individual> dominatedIndividuals) {
        this.dominatedIndividuals = dominatedIndividuals;
    }

    public double getCrowdingDistance() {
        return crowdingDistance;
    }

    public void setCrowdingDistance(double crowdingDistance) {
        this.crowdingDistance = crowdingDistance;
    }

    public Milestone getMilestone() {
        return milestone;
    }

    public List<MicroserviceFogDevice> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<MicroserviceFogDevice> deviceList) {
        this.deviceList = deviceList;
    }

    public List<MicroserviceFogDevice> getAllDeviceList() {
        return allDeviceList;
    }

    public void setAllDeviceList(List<MicroserviceFogDevice> allDeviceList) {
        this.allDeviceList = allDeviceList;
    }

    public List<Microservice> getMicroserviceList() {
        return microserviceList;
    }

    public void setMicroserviceList(List<Microservice> microserviceList) {
        this.microserviceList = microserviceList;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Rank: ").append(rank).append(", Objectives: ").append(Arrays.toString(objectives));
        sb.append("\n");
        sb.append("Microservices to Device Ids: ").append(Arrays.toString(microserviceToDeviceMapping.values().toArray()));
        sb.append("\n");
        sb.append("The placement Status of microservices: ").append(Arrays.toString(isMSPlaced.values().toArray()));
        sb.append("\n");

        for (String serviceName: microserviceToDeviceMapping.keySet()){

            sb.append("Microservice: ").append(serviceName);
            FogDevice fogDevice = placementLogic.getDevice(microserviceToDeviceMapping.get(serviceName));
            sb.append(" placed in device: ").append(fogDevice.getName()).append("\n");

        }

        return sb.toString();
    }
}
