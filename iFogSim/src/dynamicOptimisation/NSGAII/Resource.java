package dynamicOptimisation.NSGAII;

public class Resource {

    double RAM; // in GB
    double Storage; // in GB
    double CPU; // in number
    double BW; // in GB

    public Resource(double RAM, double storage, int CPU, double BW) {
        this.RAM = RAM;
        Storage = storage;
        this.CPU = CPU;
        this.BW = BW;
    }

}
