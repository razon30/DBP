package dynamicOptimisation.components;

import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.MicroserviceFogDevice;
import dynamicOptimisation.model.Microservice;

import java.util.ArrayList;
import java.util.List;

public class MasterFogDevice extends MicroserviceFogDevice {

    static List<FogDevice> citizenFogDevice = new ArrayList<>();
    static List<Microservice> microservices = new ArrayList<>();

    public MasterFogDevice(String name, FogDeviceCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval, double uplinkBandwidth, double downlinkBandwidth, double clusterLinkBandwidth, double uplinkLatency, double ratePerMips, String deviceType) throws Exception {
        super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, uplinkBandwidth, downlinkBandwidth, clusterLinkBandwidth, uplinkLatency, ratePerMips, deviceType);
    }

    public List<FogDevice> getCitizenFogDevice() {
        return citizenFogDevice;
    }

    public void setCitizenFogDevice(List<FogDevice> citizenFogDevice) {
        MasterFogDevice.citizenFogDevice = citizenFogDevice;
    }

    public List<Microservice> getMicroservices() {
        return microservices;
    }

    public void setMicroservices(List<Microservice> microservices) {
        MasterFogDevice.microservices = microservices;
    }
}
