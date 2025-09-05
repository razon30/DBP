package dynamicOptimisation.components;

import jdk.jshell.execution.Util;
import org.apache.commons.math3.util.Pair;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.application.selectivity.SelectivityModel;
import org.fog.entities.Actuator;
import org.fog.entities.Sensor;
import org.fog.scheduler.TupleScheduler;
import org.fog.utils.FogUtils;
import org.fog.utils.GeoCoverage;
import dynamicOptimisation.model.Microservice;
import dynamicOptimisation.utils.EntityName.MicroserviceName;
import dynamicOptimisation.utils.UtilFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Milestone extends Application {

    long requiredMilestoneThroughput = 0;
    ArrayList<Integer> loopIdList = new ArrayList<>();
    double totalDelay = Double.MAX_VALUE;
    double averageExecutionTime = Double.MAX_VALUE;
    public List<Microservice> microservices = new ArrayList<>();
    public List<String> toPlaceInCFs = new ArrayList<>();
    public List<Sensor> sensorList = new ArrayList<>();
    public List<Actuator> actuatorList = new ArrayList<>();


    public static Application createMilestone(String appId, int userId){
        return new Milestone(appId, userId);
    }

    public Milestone(String appId, int userId) {
        super(appId, userId);
    }

    public Milestone(String appId, List<AppModule> modules, List<AppEdge> edges, List<AppLoop> loops, GeoCoverage geoCoverage) {
        super(appId, modules, edges, loops, geoCoverage);
    }


    public void addMicroservice(String moduleName, int ram, int mips, int size, long bw) {
        String vmm = "Xen";

        Microservice microservice = new Microservice(FogUtils.generateEntityId(), moduleName, super.getAppId(), super.getUserId(),
                mips, ram, bw, size, vmm, new TupleScheduler(mips, 1), new HashMap<Pair<String, String>, SelectivityModel>());

        if (microservice.getName().contains(MicroserviceName.RESOURCE_ANALYSIS_MODULE_1)){
            microservice.setRequiredResponsiveness(UtilFactory.getValue(60, 65));
        }else if (microservice.getName().contains(MicroserviceName.ACTION_PLAN_MODULE_1) || microservice.getName().contains(MicroserviceName.PROD_SCHEDULE_MODULE_1)){
            double resValue = UtilFactory.getValue(160, 165);
            microservice.setRequiredResponsiveness(resValue);
        }else  {
            microservice.setRequiredResponsiveness(UtilFactory.getValue(13, 17));
        }

        microservices.add(microservice);
        getModules().add(microservice);

        String msType = moduleName.substring(0, moduleName.length() - 2);
        if (!UtilFactory.microserviceTypeToInstance.containsKey(msType)){
            UtilFactory.microserviceTypeToInstance.put(msType, new ArrayList<>());
        }
        UtilFactory.microserviceTypeToInstance.get(msType).add(moduleName);
    }

    public long getRequiredMilestoneThroughput() {
        return requiredMilestoneThroughput;
    }

    public void setRequiredMilestoneThroughput(long requiredMilestoneThroughput) {
        this.requiredMilestoneThroughput = requiredMilestoneThroughput;
    }

    public ArrayList<Integer> getLoopId() {
        return loopIdList;
    }

    public void setLoopId(int loopId) {
        loopIdList.add(loopId);
    }
}
