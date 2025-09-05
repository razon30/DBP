package dynamicOptimisation.model;

import org.apache.commons.math3.util.Pair;
import org.cloudbus.cloudsim.CloudletScheduler;
import org.fog.application.AppModule;
import org.fog.application.selectivity.SelectivityModel;

import java.util.HashMap;
import java.util.Map;

public class Microservice extends AppModule {

    double requiredResponsiveness = 0;
    public int numberOfTimeResponsivenessMet = 0;
    public int numberOfTimeResponsivenessDidNotMet = 0;
    public double maximumExecutionTime = 0;
    public double minExecutionTime = Double.MAX_VALUE;
    public double averageDelayFaced = 0;


    private double execTime = 0;

    public void setExecTime(double execTime) {
        this.execTime = execTime;
    }

    public double getExecTime() {
        return execTime;
    }

    /*
    Track the time required by the microservice in a particular Fog device
     */
    Map<Integer, Double> deviceToExecutionTime = new HashMap<>();

    public Microservice(int id, String name, String appId, int userId, double mips, int ram, long bw, long size, String vmm, CloudletScheduler cloudletScheduler, Map<Pair<String, String>, SelectivityModel> selectivityMap) {
        super(id, name, appId, userId, mips, ram, bw, size, vmm, cloudletScheduler, selectivityMap);
    }

    public Microservice(AppModule operator) {
        super(operator);
    }

    public double getRequiredResponsiveness() {
        return requiredResponsiveness;
    }

    public void setRequiredResponsiveness(double requiredResponsiveness) {
        this.requiredResponsiveness = requiredResponsiveness;
    }

    public int getNumberOfTimeResponsivenessMet() {
        return numberOfTimeResponsivenessMet;
    }

    public void setNumberOfTimeResponsivenessMet(int numberOfTimeResponsivenessMet) {
        this.numberOfTimeResponsivenessMet = numberOfTimeResponsivenessMet;
    }

    public Map<Integer, Double> getDeviceToExecutionTime() {
        return deviceToExecutionTime;
    }

    public void setDeviceToExecutionTime(Map<Integer, Double> deviceToExecutionTime) {
        this.deviceToExecutionTime = deviceToExecutionTime;
    }
}
