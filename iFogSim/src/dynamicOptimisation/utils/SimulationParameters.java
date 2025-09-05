package dynamicOptimisation.utils;

import org.apache.commons.math3.util.Pair;
import org.fog.entities.Tuple;
import dynamicOptimisation.NSGAII.Individual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SimulationParameters {

    public static int CLOUD_ONLY_SIMULATION = 1;
    public static int CLOUD_FOG_SIMULATION = 2;

    public static PlacementType placementType = PlacementType.CLOUD_FOG_OPTIMISE;

    public static int COMPARING_EXECUTION_TIME = 0;
    public static int COMPARING_NETWORK_DELAY = 1;
    public static int COMPARING_NUMBER_OF_REQUEST_IN_WAITING = 2;

    public static int numberOfSimulation = 1;

    //cluster link latency 2ms
    public static Double clusterLatency = 2.0;
    //numberOf
    public static int numberOfSenSorDevice = 2;
    public static int numberOfCF = 5;

    public static Map<Integer, Map<String, Boolean>> deviceToModuleToRunningState = new HashMap<>();
    public static int tupleGenerated = 0;
    public static int tupleCounter = 0;
    public static int maxTupleNumber = 2500;
    public static int successfullyReachedLastActuator = 0;
    public static int cameToTSensor = 0;
    public static int goingToActuator = 0;
    public static int requestInWaiting = 0;
    public static Map<String, Map<String, Map<Integer, Integer>>> serviceWaitingAtDeviceWithCount = new HashMap<>();
    //Map
    // <ServiceName, Map<DeviceName, Count>>
    public static Map<Integer, Tuple> actualTupleInWaiting = new HashMap<>();
    public static List<Integer> numberOfTUpleDelegatedToCloud = new ArrayList<>();
    public static Map<String, Map<String, Double>> appIdToTupleTypeToTime = new HashMap<>();
    public static Map<String, Map<Integer, Map<String, Double>>> appIdToTupleIdToTupleTypeToTime = new HashMap<>();
    public static Map<String, Pair<Double, Double>> moduleToNetworkDelays = new HashMap<>();


    public static double crossOverProbability = 0.8;
    public static double tournamentSize = 2;
    public static double SelectionCrossoverProbability = 0.8;
    public static double mutationProbability = 0.8;
    public static int populationSize = 100;
    public static int maxGenerations = 300;
    public static int genNumberToGetTheOptimisedSol = 0;
    public static int gatewayDelayToCloud = 150;


    public static int numberOfTimesOfSimulation = 1;

    public static List<Individual> finalParetoFront;// = 1;

}
