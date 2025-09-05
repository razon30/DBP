package org.fog.entities;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.utils.*;
import dynamicOptimisation.utils.EntityName.EventName;
import dynamicOptimisation.utils.EntityName.IoTDeviceName;
import dynamicOptimisation.utils.SimulationParameters;

import java.util.ArrayList;
import java.util.List;

public class Actuator extends SimEntity {

    private int gatewayDeviceId;
    private double latency;
    private GeoLocation geoLocation;
    private String appId;
    private int userId;
    private String actuatorType;
    private Application app;
	private List<SimEntity> actuatingObjects;

    public Actuator(String name, int userId, String appId, int gatewayDeviceId, double latency, GeoLocation geoLocation, String actuatorType, String srcModuleName) {
        super(name);
        this.setAppId(appId);
        this.gatewayDeviceId = gatewayDeviceId;
        this.geoLocation = geoLocation;
        setUserId(userId);
        setActuatorType(actuatorType);
        setLatency(latency);
		actuatingObjects = new ArrayList<>();
    }

    public Actuator(String name, int userId, String appId, String actuatorType) {
        super(name);
        this.setAppId(appId);
        setUserId(userId);
        setActuatorType(actuatorType);
		actuatingObjects = new ArrayList<>();
    }

    @Override
    public void startEntity() {
        sendNow(gatewayDeviceId, FogEvents.ACTUATOR_JOINED, getLatency());
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case FogEvents.TUPLE_ARRIVAL:
                processTupleArrival(ev);
                break;
        }
    }

    private void processTupleArrival(SimEvent ev) {
        SimulationParameters.tupleGenerated++;
        Tuple tuple = (Tuple) ev.getData();
        Logger.debug(getName(), "Received tuple " + tuple.getCloudletId() + "on " + tuple.getDestModuleName());
        String srcModule = tuple.getSrcModuleName();
        String destModule = tuple.getDestModuleName();
        Application app = getApp();

        for (AppLoop loop : app.getLoops()) {
            //	if(loop.hasEdge(srcModule, destModule) && loop.isEndModule(destModule)){
            if (loop.getLoopId() == 2){
                System.out.println("loop 2");
            }
            Double startTime = TimeKeeper.getInstance().getEmitTimes().get(tuple.getActualTupleId());
            if (startTime == null)
                break;
            if (!TimeKeeper.getInstance().getLoopIdToCurrentAverage().containsKey(loop.getLoopId())) {
                TimeKeeper.getInstance().getLoopIdToCurrentAverage().put(loop.getLoopId(), 0.0);
                TimeKeeper.getInstance().getLoopIdToCurrentNum().put(loop.getLoopId(), 0);
            }
            double currentAverage = TimeKeeper.getInstance().getLoopIdToCurrentAverage().get(loop.getLoopId());
            int currentCount = TimeKeeper.getInstance().getLoopIdToCurrentNum().get(loop.getLoopId());
            double delay = CloudSim.clock() - TimeKeeper.getInstance().getEmitTimes().get(tuple.getActualTupleId());
            TimeKeeper.getInstance().getEmitTimes().remove(tuple.getActualTupleId());
            double newAverage = (currentAverage * currentCount + delay) / (currentCount + 1);
            TimeKeeper.getInstance().getLoopIdToCurrentAverage().put(loop.getLoopId(), newAverage);
            TimeKeeper.getInstance().getLoopIdToCurrentNum().put(loop.getLoopId(), currentCount + 1);

            SimulationParameters.successfullyReachedLastActuator++;

			if (tuple.getTupleType().equals(EventName.REQUEST_SENSOR_DATA)) {
				//System.out.println(getName() + " Received tuple " + tuple.getCloudletId() + "on " + tuple
                // .getDestModuleName());

                for (AppEdge edge: app.getEdges()){
                    if (edge.getSource().equals(IoTDeviceName.SDA_ACTUATION)){
                        String newDestModule = edge.getDestination();
                        tuple.setDestModuleName(newDestModule);
                        tuple.setSrcModuleName(IoTDeviceName.SDA_ACTUATION);

                        if (!getActuatingObjects().isEmpty()) {
                            for (SimEntity entity: getActuatingObjects()){
                                if (entity.getName().contains(newDestModule)){
                                    Sensor tempSens = (Sensor) entity;
                                    send(tempSens.getId(), getLatency(), FogEvents.SENSOR_RECEIVED_TUPLE, tuple);
                                }
                            }
                        }

                    }
                }


			}
            else {
                SimulationParameters.successfullyReachedLastActuator++;
            }


            break;
//			}else {
//				if (loop.hasEdge(srcModule, destModule) && !loop.isEndModule(destModule)){
//					System.out.println("Actuator Name: "+getName()+ " Received tuple from "+tuple.getSrcModuleName()+" to "+tuple.getDestModuleName());
//					SimulationParameters.cameToTSensor++;
//
//
//
//
//					break;
//				}
//			}
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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getActuatorType() {
        return actuatorType;
    }

    public void setActuatorType(String actuatorType) {
        this.actuatorType = actuatorType;
    }

    public Application getApp() {
        return app;
    }

    public void setApp(Application app) {
        this.app = app;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }

	public List<SimEntity> getActuatingObjects() {
		return actuatingObjects;
	}

	public void setActuatingObjects(List<SimEntity> actuatingObjects) {
		this.actuatingObjects = actuatingObjects;
	}
}
