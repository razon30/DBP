package org.fog.entities;

import java.util.ArrayList;

import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.utils.*;
import org.fog.utils.distribution.Distribution;
import dynamicOptimisation.utils.SimulationParameters;

public class Sensor extends SimEntity {

    int i = 0;
    private int gatewayDeviceId;
    private GeoLocation geoLocation;
    private long outputSize;
    private String appId;
    private int userId;
    private String tupleType;
    private String sensorName;
    private String destModuleName;
    private Distribution transmitDistribution;
    private int controllerId;
    private Application app;
    private double latency;
    private boolean isInitialEmitter = false;

    private int transmissionStartDelay = Config.TRANSMISSION_START_DELAY;

    public Sensor(String name, int userId, String appId, int gatewayDeviceId, double latency, GeoLocation geoLocation,
                  Distribution transmitDistribution, int cpuLength, int nwLength, String tupleType, String destModuleName) {
        super(name);
        this.setAppId(appId);
        this.gatewayDeviceId = gatewayDeviceId;
        this.geoLocation = geoLocation;
        this.outputSize = 3;
        this.setTransmitDistribution(transmitDistribution);
        setUserId(userId);
        setDestModuleName(destModuleName);
        setTupleType(tupleType);
        setSensorName(sensorName);
        setLatency(latency);
    }

    public Sensor(String name, int userId, String appId, int gatewayDeviceId, double latency, GeoLocation geoLocation,
                  Distribution transmitDistribution, String tupleType) {
        super(name);
        this.setAppId(appId);
        this.gatewayDeviceId = gatewayDeviceId;
        this.geoLocation = geoLocation;
        this.outputSize = 3;
        this.setTransmitDistribution(transmitDistribution);
        setUserId(userId);
        setTupleType(tupleType);
        setSensorName(sensorName);
        setLatency(latency);
    }

    /**
     * This constructor is called from the code that generates PhysicalTopology from JSON
     *
     * @param name
     * @param tupleType
     * @param string
     * @param userId
     * @param appId
     * @param transmitDistribution
     */
    public Sensor(String name, String tupleType, int userId, String appId, Distribution transmitDistribution) {
        super(name);
        this.setAppId(appId);
        this.setTransmitDistribution(transmitDistribution);
        setTupleType(tupleType);
        setSensorName(tupleType);
        setUserId(userId);
    }

    public void transmit(Tuple inputTuple) {
        AppEdge _edge = null;
        for (AppEdge edge : getApp().getEdges()) {
            if (edge.getSource().equals(getTupleType()))
                _edge = edge;
        }
        long cpuLength = (long) _edge.getTupleCpuLength();
        long nwLength = (long) _edge.getTupleNwLength();

        Tuple newtuple = new Tuple(inputTuple.getAppId(), FogUtils.generateTupleId(), Tuple.UP, cpuLength,
                inputTuple.getNumberOfPes(),
                nwLength,
                inputTuple.getCloudletOutputSize(),
                inputTuple.getUtilizationModelCpu(),
                inputTuple.getUtilizationModelRam(),
                inputTuple.getUtilizationModelBw());

        newtuple.setUserId(inputTuple.getUserId());
        newtuple.setTupleType(getTupleType());
        newtuple.setTraversedMicroservices(inputTuple.getTraversed());

        newtuple.setDestModuleName(_edge.getDestination());
        newtuple.setSrcModuleName(getSensorName());
        Logger.debug(getName(), "Sending tuple with tupleId = " + newtuple.getCloudletId());

        newtuple.setDestinationDeviceId(getGatewayDeviceId());

        updateTimings(getSensorName(), inputTuple.getDestModuleName(), inputTuple.getActualTupleId());
        newtuple.setActualTupleId(inputTuple.getActualTupleId());
        newtuple.setSourceModuleId(getId());
        send(gatewayDeviceId, getLatency(), FogEvents.TUPLE_ARRIVAL, newtuple);
    }

    public void transmit() {
        AppEdge _edge = null;
        for (AppEdge edge : getApp().getEdges()) {
            if (edge.getSource().equals(getTupleType()))
                _edge = edge;
        }
        long cpuLength = (long) _edge.getTupleCpuLength();
        long nwLength = (long) _edge.getTupleNwLength();

        Tuple tuple = new Tuple(getAppId(), FogUtils.generateTupleId(), Tuple.UP, cpuLength, 1, nwLength, outputSize,
                new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
        tuple.setUserId(getUserId());
        tuple.setTupleType(getTupleType());

        tuple.setDestModuleName(_edge.getDestination());
        tuple.setSrcModuleName(getSensorName());
        Logger.debug(getName(), "Sending tuple with tupleId = " + tuple.getCloudletId());

        tuple.setDestinationDeviceId(getGatewayDeviceId());

        int actualTupleId = updateTimings(getSensorName(), tuple.getDestModuleName());
        tuple.setActualTupleId(actualTupleId);
        tuple.setSourceModuleId(getId());
        send(gatewayDeviceId, getLatency(), FogEvents.TUPLE_ARRIVAL, tuple);
    }

    public int updateTimings(String src, String dest) {
        Application application = getApp();
        for (AppLoop loop : application.getLoops()) {
            if (loop.getLoopId() == 2){
                System.out.println("loop 2");
            }
            if (loop.hasEdge(src, dest)) {

                int tupleId = TimeKeeper.getInstance().getUniqueId();
                if (!TimeKeeper.getInstance().getLoopIdToTupleIds().containsKey(loop.getLoopId()))
                    TimeKeeper.getInstance().getLoopIdToTupleIds().put(loop.getLoopId(), new ArrayList<Integer>());
                TimeKeeper.getInstance().getLoopIdToTupleIds().get(loop.getLoopId()).add(tupleId);
                TimeKeeper.getInstance().getEmitTimes().put(tupleId, CloudSim.clock());
                return tupleId;
            }
        }
        return -1;
    }

    protected void updateTimings(String src, String dest, int tupleId) {
        Application application = getApp();
        for (AppLoop loop : application.getLoops()) {
            if (loop.hasEdge(src, dest)) {

              //  int tupleId = TimeKeeper.getInstance().getUniqueId();
                if (!TimeKeeper.getInstance().getLoopIdToTupleIds().containsKey(loop.getLoopId()))
                    TimeKeeper.getInstance().getLoopIdToTupleIds().put(loop.getLoopId(), new ArrayList<Integer>());
                TimeKeeper.getInstance().getLoopIdToTupleIds().get(loop.getLoopId()).add(tupleId);
                TimeKeeper.getInstance().getEmitTimes().put(tupleId, CloudSim.clock());
            }
        }
    }

    @Override
    public void startEntity() {
        send(gatewayDeviceId, CloudSim.getMinTimeBetweenEvents(), FogEvents.SENSOR_JOINED, getLatency());//geoLocation);
        if (isInitialEmitter()) {
            send(getId(), getTransmitDistribution().getNextValue() + transmissionStartDelay, FogEvents.EMIT_TUPLE);
        }
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case FogEvents.SENSOR_RECEIVED_TUPLE:
               // System.out.println("Received");
                transmit((Tuple) ev.getData());
                break;
            case FogEvents.TUPLE_ACK:
                //transmit(transmitDistribution.getNextValue());
                break;
            case FogEvents.EMIT_TUPLE:
                if (SimulationParameters.tupleCounter < SimulationParameters.maxTupleNumber) {
                    transmit();
                    send(getId(), getTransmitDistribution().getNextValue(), FogEvents.EMIT_TUPLE);
                    SimulationParameters.tupleCounter++;
                    break;
                }

        }

    }

    @Override
    public void shutdownEntity() {

    }

    public int getGatewayDeviceId() {
        return gatewayDeviceId;
    }

    public void setGatewayDeviceId(int gatewayDeviceId) {
        this.gatewayDeviceId = gatewayDeviceId;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTupleType() {
        return tupleType;
    }

    public void setTupleType(String tupleType) {
        this.tupleType = tupleType;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDestModuleName() {
        return destModuleName;
    }

    public void setDestModuleName(String destModuleName) {
        this.destModuleName = destModuleName;
    }

    public Distribution getTransmitDistribution() {
        return transmitDistribution;
    }

    public void setTransmitDistribution(Distribution transmitDistribution) {
        this.transmitDistribution = transmitDistribution;
    }

    public int getControllerId() {
        return controllerId;
    }

    public void setControllerId(int controllerId) {
        this.controllerId = controllerId;
    }

    public Application getApp() {
        return app;
    }

    public void setApp(Application app) {
        this.app = app;
    }

    public Double getLatency() {
        return latency;
    }

    public void setLatency(Double latency) {
        this.latency = latency;
    }

    protected long getOutputSize() {
        return this.outputSize;
    }

    public void setTransmissionStartDelay(int transmissionStartDelay) {
        this.transmissionStartDelay = transmissionStartDelay;
    }

    public int getTransmissionStartDelay() {
        return transmissionStartDelay;
    }

    public boolean isInitialEmitter() {
        return isInitialEmitter;
    }

    public void setInitialEmitter(boolean initialEmitter) {
        isInitialEmitter = initialEmitter;
    }
}
