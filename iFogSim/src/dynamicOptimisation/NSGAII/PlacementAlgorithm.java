package dynamicOptimisation.NSGAII;

import org.fog.application.AppEdge;
import org.fog.entities.MicroserviceFogDevice;
import org.fog.entities.Tuple;
import dynamicOptimisation.components.CloudFogMicroservicePlacementOptimise;
import dynamicOptimisation.components.Milestone;
import dynamicOptimisation.model.Microservice;
import dynamicOptimisation.utils.EntityName.IoTDeviceName;
import dynamicOptimisation.utils.SimulationParameters;
import dynamicOptimisation.utils.UtilFactory;

import java.util.*;

public class PlacementAlgorithm {

    CloudFogMicroservicePlacementOptimise placementLogic;
    List<MicroserviceFogDevice> deviceList = new ArrayList<>();
    List<Microservice> microserviceList = new ArrayList<>();
    Milestone milestone = null;
    Map<Integer, List<String>> deviceToMicroserviceMap;
    private List<Individual> population;

    public void PlacementMapper(CloudFogMicroservicePlacementOptimise placementLogic, Milestone milestone) {
        this.placementLogic = placementLogic;
        this.milestone = milestone;
    }

    public void initiateTheAlgo(){

        this.population = new ArrayList<>();
        run();

        // Randomly place modules on Fog devices
//        randomlyPlaceModules();
//        double processingTime = processTheTuple();
//
//        System.out.println("Processing time: "+processingTime);

    }

    private void run() {

        // Step 1: Initialize population
        initializePopulation();
        Individual bestInd = population.get(0);

        for (int generation = 1; generation <= SimulationParameters.maxGenerations; generation++) {
            // Step 2: Evaluate fitness (objective values) for each individual
            //System.out.println("Generation: "+generation);
            evaluateFitness();

            // Step 3: Perform selection, crossover, and mutation to create offspring
            //System.out.println("Step 3: Perform selection");
            List<Individual> offspring = performSelectionCrossoverMutation(population);

            // Step 4: Combine parent population and offspring
            //System.out.println("Step 4: Combine parent");
            List<Individual> combinedPopulation = new ArrayList<>(population);
            combinedPopulation.addAll(offspring);

            // Step 5: Perform environmental selection to create the next generation
            //System.out.println("Step 5: Perform environmental");
            population = environmentalSelection(combinedPopulation, SimulationParameters.populationSize);

            // Optional: Track and output the best individuals or Pareto front in each generation
            // Print the Pareto front individuals in the current generation
            //System.out.println("Print the Pareto front individuals");
            List<Individual> paretoFront = getNonDominatedSolutions(population);
            //  System.out.println("Generation " + generation + " Pareto Front:");
//            for (Individual individual : paretoFront) {
//                System.out.println(individual);
//            }
            Individual currentBestInd = paretoFront.get(0);
            if (bestInd.compareDominance(currentBestInd) < 0){
                bestInd = currentBestInd;
                SimulationParameters.genNumberToGetTheOptimisedSol = generation;
            }

//            Individual milestoneBest = Entities.bestIndividualsPerMilestone[currentBestInd.milestoneId];
//
//            if (currentBestInd.compareDominance(milestoneBest) > 0){
//                Entities.bestIndividualsPerMilestone[currentBestInd.milestoneId] = currentBestInd;
//            }

        }

        // After all generations, retrieve and output the final Pareto front
        SimulationParameters.finalParetoFront = getNonDominatedSolutions(population);

    }

    private void initializePopulation() {
        // Initialize the population with random microservice placements
        for (int i = 0; i < SimulationParameters.populationSize; i++) {
            Individual individual = new Individual(placementLogic);
            individual.setMilestone(milestone);
            individual.setDeviceList(deviceList);
            individual.setMicroserviceList(microserviceList);
            individual.initialize(); // Customize the initialization based on your problem domain
            population.add(individual);
            // SimulationParameters.bestIndividualsPerMilestone[milestoneId] = individual;
        }
    }

    private void evaluateFitness() {
        // Evaluate the fitness (objective values) for each individual in the population
        for (Individual individual : population) {
            individual.evaluateFitness(); // Customize the fitness evaluation based on your problem domain
        }
    }

    private List<Individual> performSelectionCrossoverMutation(List<Individual> population) {
        List<Individual> offspringPopulation = new ArrayList<>();
        Random random = new Random();

        while (offspringPopulation.size() < SimulationParameters.populationSize) {

//            int milestoneId = -1;//random.nextInt(Entities.milestones.length);
//            List<Individual> milestonePopulation = new ArrayList<>();//getMilestoneSpecificPopulation(milestoneId,
//            // population);
//
////            if (milestonePopulation.size() == 0){
////                System.out.println(milestoneId+", "+ Arrays.toString(Entities.generatedIds.values().toArray()));
////            }
//
//            while (milestonePopulation.size() == 0){
//                //  System.out.println(milestoneId+", "+ Arrays.toString(Entities.generatedIds.values().toArray()));
//                milestoneId = getMilestoneId();//random.nextInt(Entities.milestones.length);
//                milestonePopulation = getMilestoneSpecificPopulation(milestoneId, population);
//            }

          //  Milestone milestone = Entities.milestones[milestoneId];
          //  ArrayList<Microservice> microservices = milestone.getMicroservices();

            // Selection: Tournament selection
            //System.out.println("Selection: Tournament selection 1");
            Individual parent1 = tournamentSelection(population);
            //System.out.println("Selection: Tournament selection 2");
            Individual parent2 = tournamentSelection(population);

            // Crossover: Simulated binary crossover (SBX)
            Individual offspring1 = new Individual(placementLogic);
            offspring1.setMilestone(milestone);
            offspring1.setDeviceList(deviceList);
            offspring1.setMicroserviceList(microserviceList);
            offspring1.initialize();
            Individual offspring2 = new Individual(placementLogic);
            offspring2.setMilestone(milestone);
            offspring2.setDeviceList(deviceList);
            offspring2.setMicroserviceList(microserviceList);
            offspring2.initialize();

            //  if (random.nextDouble() < Entities.SelectionCrossoverProbability) {
            //System.out.println("Crossover: Simulated binar");
            crossover(parent1, parent2, offspring1, offspring2, microserviceList);
//            } else {
//                offspring1.copyFrom(parent1);
//                offspring2.copyFrom(parent2);
//            }

            // Mutation: Gaussian mutation
            //System.out.println("Mutation: Gaussian mutation 1");
            mutate(offspring1, microserviceList);
            //System.out.println("Mutation: Gaussian mutation 2");
            mutate(offspring2, microserviceList);

            //System.out.println("evaluateFitness 1");
            offspring1.evaluateFitness();
            //System.out.println("evaluateFitness 2");
            offspring2.evaluateFitness();

            offspringPopulation.add(offspring1);
            offspringPopulation.add(offspring2);
        }

        return offspringPopulation;
    }

    private Individual tournamentSelection(List<Individual> population) {
        Random random = new Random();

        List<Individual> tournament = new ArrayList<>();
        //System.out.println("Selection: Tournament selection-while entering "+ population.size()+", "+ milestoneId);

        // Randomly select individuals for the tournament
        for (int i = 0; i < SimulationParameters.tournamentSize; i++) {

            //int index = random.nextInt(populationSize);
            Individual individual = population.get(random.nextInt(population.size()));
            //System.out.println("Selection: Tournament selection ind miles "+ individual.milestoneId+", "+milestoneId);

            tournament.add(individual);
        }

        // Return the individual with the best fitness (based on dominance and crowding distance)
        //  System.out.println("Return the individual ");
        return getBestIndividual(tournament);
    }

    private void crossover(Individual parent1, Individual parent2, Individual offspring1, Individual offspring2,
                           List<Microservice> microservices) {
        Random random = new Random();

        for(Microservice microservice: microservices){
            if (random.nextDouble() < SimulationParameters.crossOverProbability) {

                offspring1.microserviceToDeviceMapping.put(microservice.getName(),
                        parent1.microserviceToDeviceMapping.get(microservice.getName()));
                offspring2.microserviceToDeviceMapping.put(microservice.getName(),
                        parent2.microserviceToDeviceMapping.get(microservice.getName()));

            }else {
                offspring1.microserviceToDeviceMapping.put(microservice.getName(),
                        parent2.microserviceToDeviceMapping.get(microservice.getName()));
                offspring2.microserviceToDeviceMapping.put(microservice.getName(),
                        parent1.microserviceToDeviceMapping.get(microservice.getName()));
            }
        }

    }

    private void mutate(Individual individual, List<Microservice> microservices) {
        Random random = new Random();
        double gaussianAverage = (double) (SimulationParameters.numberOfCF - 1) / 2; //Arrays.stream(individual.microservicePlacements).sum() / 5.0;

        for (Microservice microservice : microservices){

            if (random.nextDouble() < SimulationParameters.mutationProbability) {

                double oldValue = individual.microserviceToDeviceMapping.get(microservice.getName());

                // ToDo:
                //Need to check Entities.getGaussianStandardDeviation(individual) for new code
                double perturbation = random.nextGaussian() * UtilFactory.getGaussianStandardDeviation(individual) + gaussianAverage;
                int value = (int) Math.max(0, Math.min(SimulationParameters.numberOfCF - 1, perturbation));
                individual.microserviceToDeviceMapping.put(microservice.getName(), deviceList.get(value).getId());

            }

        }

    }

    private Individual getBestIndividual(List<Individual> individuals) {
        Individual bestIndividual = individuals.get(0);

        for (int i = 1; i < individuals.size(); i++) {
            Individual currentIndividual = individuals.get(i);

            if (currentIndividual.rank < bestIndividual.rank ||
                    (currentIndividual.rank == bestIndividual.rank &&
                            currentIndividual.getCrowdingDistance() > bestIndividual.getCrowdingDistance())) {
                bestIndividual = currentIndividual;
            }
        }

        return bestIndividual;
    }

    private List<Individual> environmentalSelection(List<Individual> combinedPopulation, int populationSize) {
        // Perform environmental selection (NSGA-II Fast Non-dominated Sorting + Crowding Distance)
        List<Individual> selectedPopulation = new ArrayList<>();

        // Perform fast non-dominated sorting
        List<List<Individual>> fronts = fastNonDominatedSorting(combinedPopulation);

        // Select individuals for the next generation based on crowding distance
        int remainingPopulationSize = populationSize;
        int currentFront = 0;

        while (remainingPopulationSize > 0 && currentFront < fronts.size()) {
            List<Individual> front = fronts.get(currentFront);

            if (front.size() <= remainingPopulationSize) {
                // Add all individuals from the current front to the next generation
                selectedPopulation.addAll(front);
                remainingPopulationSize -= front.size();
            } else {
                // Apply crowding distance sorting to select individuals from the current front
                List<Individual> selectedFront = crowdingDistanceSorting(front, remainingPopulationSize);
                selectedPopulation.addAll(selectedFront);
                remainingPopulationSize = 0;
            }

            currentFront++;
        }

        return selectedPopulation;
    }

    private List<List<Individual>> fastNonDominatedSorting(List<Individual> population) {
        // Perform fast non-dominated sorting to divide the population into fronts
        List<List<Individual>> fronts = new ArrayList<>();
        List<Individual> firstFront = new ArrayList<>();

        // Initialize domination counts and dominated individuals for each individual in the population
        for (Individual p : population) {
            p.dominationCount = 0;
            p.dominatedIndividuals = new ArrayList<>();
        }

        for (int i = 0; i < population.size(); i++) {
            Individual p = population.get(i);

            for (int j = i + 1; j < population.size(); j++) {
                Individual q = population.get(j);

                // Check dominance relationship between p and q
                if (p.compareDominance(q) == 1) {
                    p.dominatedIndividuals.add(q);
                } else if (p.compareDominance(q) == -1) {
                    p.dominationCount++;
                }
            }

            if (p.dominationCount == 0) {
                // Individual p belongs to the first front
                p.rank = 1;
                firstFront.add(p);
            }
        }

        fronts.add(firstFront);

        int currentFront = 0;
        List<Individual> currentFrontIndividuals = fronts.get(currentFront);

        while (!currentFrontIndividuals.isEmpty()) {
            List<Individual> nextFrontIndividuals = new ArrayList<>();

            for (Individual p : currentFrontIndividuals) {
                for (Individual q : p.dominatedIndividuals) {
                    q.dominationCount--;

                    if (q.dominationCount == 0) {
                        // Individual q belongs to the next front
                        q.rank = currentFront + 2;
                        nextFrontIndividuals.add(q);
                    }
                }
            }

            currentFront++;
            fronts.add(nextFrontIndividuals);
            currentFrontIndividuals = nextFrontIndividuals;
        }

        return fronts;
    }

    private List<Individual> crowdingDistanceSorting(List<Individual> front, int maxIndividuals) {
        // Perform crowding distance sorting to select individuals from the front
        List<Individual> selectedFront = new ArrayList<>();

        if (front.size() <= maxIndividuals) {
            // Select all individuals from the front
            selectedFront.addAll(front);
        } else {
            // Sort individuals based on crowding distance
            calculateCrowdingDistance(front);
            front.sort(Comparator.comparingDouble(Individual::getCrowdingDistance).reversed());

            // Select the top individuals with the highest crowding distance
            selectedFront.addAll(front.subList(0, maxIndividuals));
        }

        return selectedFront;
    }

    private void calculateCrowdingDistance(List<Individual> front) {
        // Calculate the crowding distance for individuals in the front
        int objectivesCount = front.get(0).objectives.length;

        for (int i = 0; i < objectivesCount; i++) {
            final int objectiveIndex = i;

            // Sort individuals based on the current objective
            front.sort(Comparator.comparingDouble(individual -> individual.objectives[objectiveIndex]));

            // Assign infinite crowding distance to the boundary individuals
            front.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
            front.get(front.size() - 1).setCrowdingDistance(Double.POSITIVE_INFINITY);

            // Calculate the crowding distance for the remaining individuals
            for (int j = 1; j < front.size() - 1; j++) {
                double currentCrowdingDistance = front.get(j).getCrowdingDistance();
                double previousObjective = front.get(j - 1).objectives[objectiveIndex];
                double nextObjective = front.get(j + 1).objectives[objectiveIndex];
                double distance = nextObjective - previousObjective;

                currentCrowdingDistance += distance;
                front.get(j).setCrowdingDistance(currentCrowdingDistance);
            }
        }
    }

    private List<Individual> getNonDominatedSolutions(List<Individual> population) {
        // Retrieve the non-dominated solutions (Pareto front) from the population
        List<Individual> paretoFront = new ArrayList<>();

        for (Individual p : population) {
            boolean isDominated = false;

            for (Individual q : population) {
                if (p.compareDominance(q) < 0) {
                    isDominated = true;
                    break;
                }
            }

            if (!isDominated) {
                paretoFront.add(p);
            }
        }

        return paretoFront;
    }



    private double processTheTuple() {

        double processingTime = 0.0;

        for (AppEdge edge : milestone.getEdges()) {

            String src = edge.getSource();
            String dest = edge.getDestination();


            if (src == IoTDeviceName.V_SENSOR || src == IoTDeviceName.T_SENSOR || src == IoTDeviceName.SDA_ACTUATION || src == IoTDeviceName.MONITOR_ACTUATION){

               // processingTime += 5; // event transmission time
                System.out.println("After Sensor transmission: "+processingTime);

            }else{

                double cpuLength = edge.getTupleCpuLength();
                double nwLength = edge.getTupleNwLength();

                Microservice srcMicroservice = placementLogic.getModule(src, milestone);
                Microservice destMicroservice = placementLogic.getModule(dest, milestone);

                MicroserviceFogDevice srcDevice = getDeviceFor(srcMicroservice);

                double executionTime = cpuLength / (srcMicroservice.getMips() * srcMicroservice.getNumberOfPes());
                double fileTransmissionTime = nwLength / srcMicroservice.getBw();

                processingTime += executionTime;
                processingTime += fileTransmissionTime;

                System.out.println("From: "+ src + " to "+ dest + " execution and transmission time: "+ executionTime+fileTransmissionTime);

                if (dest == IoTDeviceName.V_SENSOR || dest == IoTDeviceName.T_SENSOR || dest == IoTDeviceName.SDA_ACTUATION || dest == IoTDeviceName.MONITOR_ACTUATION){
                    processingTime+= srcDevice.getUplinkLatency(); // event transmission time
                    System.out.println("From: "+ src + " to "+ dest + " DOWN latency: "+ srcDevice.getUplinkLatency());
                }else {
                    MicroserviceFogDevice destDevice = getDeviceFor(destMicroservice);
                    if (!srcDevice.getName().equals(destDevice.getName())) {
                        if (edge.getDirection() == Tuple.DOWN) {
                            processingTime += destDevice.getUplinkLatency(); // network latency
                            System.out.println("From: "+ src + " to "+ dest + " DOWN latency: "+ destDevice.getUplinkLatency());
                        } else {
                            processingTime += srcDevice.getUplinkLatency();
                            System.out.println("From: "+ src + " to "+ dest + " UP latency: "+ srcDevice.getUplinkLatency());
                        }
                    }
                }
            }
        }

        return processingTime;
    }

    private MicroserviceFogDevice getDeviceFor(Microservice microservice) {

        MicroserviceFogDevice fogDevice = null;

        if (milestone.getSpecialPlacementInfo().get(microservice.getName()) != null){
            List<String> placedDeviceNameList = milestone.getSpecialPlacementInfo().get(microservice.getName());
            fogDevice = (MicroserviceFogDevice) placementLogic.getDeviceByName(placedDeviceNameList.get(0));
        }else {

            for (int deviceId: deviceToMicroserviceMap.keySet()){

                if (deviceToMicroserviceMap.get(deviceId).contains(microservice.getName())){
                    fogDevice = (MicroserviceFogDevice) placementLogic.getDevice(deviceId);
                }

            }

    //        fogDevice = (MicroserviceFogDevice) placementLogic.getDevice(placementLogic.microserviceToDeviceListMap
            //        .get(microservice.getName()).get(0));

        }

        return fogDevice;
    }



    private void randomlyPlaceModules() {

        deviceToMicroserviceMap = new HashMap<>();

        // Randomly place modules on Fog devices
        Random random = new Random();

        for (Microservice microservice : microserviceList) {
            int randomIndex = random.nextInt(deviceList.size());
            MicroserviceFogDevice fogDevice = deviceList.get(randomIndex);
            List<String> mappedMicroservice = new ArrayList<>();
            if (deviceToMicroserviceMap.keySet().contains(fogDevice.getId()) && deviceToMicroserviceMap.get(fogDevice.getId()) != null){
                mappedMicroservice = deviceToMicroserviceMap.get(fogDevice.getId());
            }
            mappedMicroservice.add(microservice.getName());
            deviceToMicroserviceMap.put(fogDevice.getId(), mappedMicroservice);
        }

    }

    public List<MicroserviceFogDevice> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<MicroserviceFogDevice> deviceList) {
        this.deviceList = deviceList;
    }

    public List<Microservice> getMicroserviceList() {
        return microserviceList;
    }

    public void setMicroserviceList(List<Microservice> microserviceList) {
        this.microserviceList = microserviceList;
    }
}
