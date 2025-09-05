package dynamicOptimisation.utils;

import java.util.HashMap;
import java.util.Map;

public class Results {


    int numberOfRqstInWaiting = 0;
    boolean isThroughputMet = false;
    int numberOfMicroserviceMeetingResponsiveness = 0;
    public long applicationExecutionTime = 0;
    long applicationNetworkDelay = 0;
    public Map<String, Double> loopExecutionTime = new HashMap<>();
    public Map<String, Double> microserviceRequestExecutionTime = new HashMap<>();
    public Map<String, Double> microserviceDataReceiveDelay = new HashMap<>();
    public Map<String, Double> microserviceDataSendDelay = new HashMap<>();
    public Map<String, Double> microserviceMaxExecutionTime = new HashMap<>();
    public Map<String, Double> microserviceRequiredResponsiveness = new HashMap<>();
    public Map<String, Double> microserviceNUmberOfTimesResponsivenessAchieved = new HashMap<>();
    public Map<String, Double> fogDeviceEnergyConsumptions = new HashMap<>();
    public double costInCloud = 0.0;
    public double networkUsed = 0.0;
    public long numberOfRequest = 0;
    long milestoneAverageRequestExecutionTime = 0;
    long achievedThroughout = 0;
    long requiredThroughput = 0;


    public Results() {
    }

    public boolean isThroughputMet() {
        return isThroughputMet;
    }

    public void setThroughputMet(boolean throughputMet) {
        isThroughputMet = throughputMet;
    }

    public int getNumberOfMicroserviceMeetingResponsiveness() {
        return numberOfMicroserviceMeetingResponsiveness;
    }

    public void setNumberOfMicroserviceMeetingResponsiveness(int numberOfMicroserviceMeetingResponsiveness) {
        this.numberOfMicroserviceMeetingResponsiveness = numberOfMicroserviceMeetingResponsiveness;
    }

    public long getApplicationExecutionTime() {
        return applicationExecutionTime;
    }

    public void setApplicationExecutionTime(long applicationExecutionTime) {
        this.applicationExecutionTime = applicationExecutionTime;
    }

    public Map<String, Double> getLoopExecutionTime() {
        return loopExecutionTime;
    }

    public void setLoopExecutionTime(Map<String, Double> loopExecutionTime) {
        this.loopExecutionTime = loopExecutionTime;
    }

    public Map<String, Double> getMicroserviceRequestExecutionTime() {
        return microserviceRequestExecutionTime;
    }

    public void setMicroserviceRequestExecutionTime(Map<String, Double> microserviceRequestExecutionTime) {
        this.microserviceRequestExecutionTime = microserviceRequestExecutionTime;
    }

    public Map<String, Double> getFogDeviceEnergyConsumptions() {
        return fogDeviceEnergyConsumptions;
    }

    public void setFogDeviceEnergyConsumptions(Map<String, Double> fogDeviceEnergyConsumptions) {
        this.fogDeviceEnergyConsumptions = fogDeviceEnergyConsumptions;
    }

    public double getCostInCloud() {
        return costInCloud;
    }

    public void setCostInCloud(double costInCloud) {
        this.costInCloud = costInCloud;
    }

    public double getNetworkUsed() {
        return networkUsed;
    }

    public void setNetworkUsed(double networkUsed) {
        this.networkUsed = networkUsed;
    }

    public long getNumberOfRequest() {
        return numberOfRequest;
    }

    public void setNumberOfRequest(long numberOfRequest) {
        this.numberOfRequest = numberOfRequest;
    }


}

//    Request number: 500
//        data 1:
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 406
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1,
//        Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor,
//        Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1,
//        Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1,
//        GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 32.6270833333333
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.08161403488901223
//        Anomaly_Detected_Object_Data ---> 0.14922109150858087
//        Production_Schedule_Data ---> 0.29442565881650296
//        Processed_Visual_Sensor_Data ---> 0.2518784570647938
//        Action_Plan_Execution_Data ---> 0.07858134374014666
//        Visual_Sensor ---> 0.8176029402617014
//        Temperature_Sensor ---> 0.19607160634892085
//        Analysed_Resource_Data ---> 0.14727034455482607
//        Processed_Temperature_Sensor_Data ---> 2.4211208289006954
//        Analysed_Resource_Report_Data ---> 0.3075695299263925
//        Request_sensor_Data ---> 0.12401790341055442
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 11207.257552083329
//        Available RAM = 2688.0
//        Available Storage = 51824.0
//        Available BW = 1572.0
//        Available CPU = 848.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 8000.0
//        Available RAM = 7168.0
//        Available Storage = 221184.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 8000.0
//        Available RAM = 2048.0
//        Available Storage = 34816.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 8000.0
//        Available RAM = 4096.0
//        Available Storage = 202752.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11642.762499999993
//        Available RAM = 3328.0
//        Available Storage = 93208.0
//        Available BW = 1572.0
//        Available CPU = 1572.0
//
//        ---------Total available resources--------------
//        Total available RAM 19328.0
//        Total available Storage 603784.0
//        Total available CPU 8564.0
//        Total available BW 12360.0
//        Cost of execution in cloud = 1.2956007800000014E9
//        Total network usage = 20340.3
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 159 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-4 for 259 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-4 for 243 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-4 for 286 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 50 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 110 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 140 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 189 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 245 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 291 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 351 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 402 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 437 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 482 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 76 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-0 for 78 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 46 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-0 for 102 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 500
//        Number of Request successfully executed: 298
//        Total CPU Execution time: 413.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 1731
//        Overall Application Network Delay: 32.6270833333333
//        Percentage of Successfully processed request: 0.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 32.6270833333333 ms
//        Average Request execution time: 1.9358823958333744 ms
//        Achieved Throughput: 1210.65 /s
//        Required Throughput: 8 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 6.578125ms
//        Average Execution time: 10.48977864583334 ms
//        Minimum Execution time: 10.029296875 ms
//        Maximum Execution time: 10.95026041666668 ms
//        Required responsiveness within 11.0 ms
//        Meet responsiveness requirement:500
//        did not meet responsiveness requirement:0
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 20.959635416666664ms
//        Average Execution time: 22.44072265625004 ms
//        Minimum Execution time: 20.065104166666664 ms
//        Maximum Execution time: 24.816341145833412 ms
//        Required responsiveness within 22.0 ms
//        Meet responsiveness requirement:376
//        did not meet responsiveness requirement:124
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 2.0833333333333335ms
//        Average Execution time: 11.296747291666671 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 12.528390416666676 ms
//        Required responsiveness within 11.0 ms
//        Meet responsiveness requirement:464
//        did not meet responsiveness requirement:36
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.089065 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390675 ms
//        Required responsiveness within 173.0 ms
//        Meet responsiveness requirement:500
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 5.5ms
//        Max delay faced in sending data: 157.078173ms
//        Average Execution time: 158.69600654166663 ms
//        Minimum Execution time: 157.078173 ms
//        Maximum Execution time: 160.31384008333328 ms
//        Required responsiveness within 157.0 ms
//        Meet responsiveness requirement:0
//        did not meet responsiveness requirement:500
//
//
//        Cost In Cloud: 1.2956007800000014E9
//        Network used: 20340.3
//
//        data 2:
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 524
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1, Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor, Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1, Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1, GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 58.12904010416664
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.0200252480374333
//        Anomaly_Detected_Object_Data ---> 0.20628186682295668
//        Production_Schedule_Data ---> 4.91479875227498
//        Processed_Visual_Sensor_Data ---> 0.2614564480464573
//        Action_Plan_Execution_Data ---> 0.043033584694275526
//        Visual_Sensor ---> 1.926658758990034
//        Temperature_Sensor ---> 0.19077941067847165
//        Analysed_Resource_Data ---> 0.0592107508997471
//        Processed_Temperature_Sensor_Data ---> 3.2805437393562307
//        Analysed_Resource_Report_Data ---> 4.430664070041068
//        Request_sensor_Data ---> 0.19211766395627056
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 11319.72454427083
//        Available RAM = 6912.0
//        Available Storage = 170608.0
//        Available BW = 1572.0
//        Available CPU = 1148.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 11595.716536458342
//        Available RAM = 6912.0
//        Available Storage = 143984.0
//        Available BW = 2172.0
//        Available CPU = 2472.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 8000.0
//        Available RAM = 5120.0
//        Available Storage = 82944.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 8000.0
//        Available RAM = 7168.0
//        Available Storage = 121856.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11751.29986979166
//        Available RAM = 5888.0
//        Available Storage = 38112.0
//        Available BW = 972.0
//        Available CPU = 848.0
//
//        ---------Total available resources--------------
//        Total available RAM 32000.0
//        Total available Storage 557504.0
//        Total available CPU 9588.0
//        Total available BW 10860.0
//        Cost of execution in cloud = 1.2266755910000021E9
//        Total network usage = 31843.95
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 237 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-1 for 263 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-4 for 265 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-4 for 322 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 70 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 121 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 201 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 246 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 292 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 364 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 438 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 514 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 584 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 647 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 93 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-0 for 146 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 51 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-0 for 175 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 500
//        Number of Request successfully executed: 375
//        Total CPU Execution time: 533.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 2199
//        Overall Application Network Delay: 58.12904010416664
//        Percentage of Successfully processed request: 0.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 58.12904010416664 ms
//        Average Request execution time: 41.5030648958336 ms
//        Achieved Throughput: 938.09 /s
//        Required Throughput: 19 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 5.828125ms
//        Average Execution time: 10.930957031249996 ms
//        Minimum Execution time: 10.029296875 ms
//        Maximum Execution time: 11.832617187499991 ms
//        Required responsiveness within 11.0 ms
//        Meet responsiveness requirement:477
//        did not meet responsiveness requirement:23
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 20.665364583333336ms
//        Average Execution time: 33.80625 ms
//        Minimum Execution time: 20.065104166666664 ms
//        Maximum Execution time: 47.54739583333333 ms
//        Required responsiveness within 20.0 ms
//        Meet responsiveness requirement:0
//        did not meet responsiveness requirement:500
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 0.9722222222222223ms
//        Average Execution time: 13.513541666666676 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 16.961979166666687 ms
//        Required responsiveness within 12.0 ms
//        Meet responsiveness requirement:453
//        did not meet responsiveness requirement:47
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.089065 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390675 ms
//        Required responsiveness within 153.0 ms
//        Meet responsiveness requirement:500
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 5.5ms
//        Max delay faced in sending data: 156.078173ms
//        Average Execution time: 163.04067550000002 ms
//        Minimum Execution time: 156.078173 ms
//        Maximum Execution time: 170.00317800000005 ms
//        Required responsiveness within 165.0 ms
//        Meet responsiveness requirement:499
//        did not meet responsiveness requirement:1
//
//
//        Cost In Cloud: 1.2266755910000021E9
//        Network used: 31843.95
//
//        data 3:
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 573
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1, Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor, Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1, Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1, GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 56.88497048611111
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.05943107700771759
//        Anomaly_Detected_Object_Data ---> 0.17088194669156354
//        Production_Schedule_Data ---> 0.3297433191207242
//        Processed_Visual_Sensor_Data ---> 0.32611580754898634
//        Action_Plan_Execution_Data ---> 0.071328864281472
//        Visual_Sensor ---> 2.275368715091765
//        Temperature_Sensor ---> 2.6749823153380285
//        Analysed_Resource_Data ---> 0.2564415948471638
//        Processed_Temperature_Sensor_Data ---> 2.294863088034149
//        Analysed_Resource_Report_Data ---> 0.3066270023781142
//        Request_sensor_Data ---> 0.1142709963946047
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 11694.65507812499
//        Available RAM = 1664.0
//        Available Storage = 142960.0
//        Available BW = 2172.0
//        Available CPU = 1148.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 8000.0
//        Available RAM = 7168.0
//        Available Storage = 115712.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 8000.0
//        Available RAM = 3072.0
//        Available Storage = 138240.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 8000.0
//        Available RAM = 6144.0
//        Available Storage = 90112.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11712.776106770827
//        Available RAM = 6656.0
//        Available Storage = 39136.0
//        Available BW = 972.0
//        Available CPU = 848.0
//
//        ---------Total available resources--------------
//        Total available RAM 24704.0
//        Total available Storage 526160.0
//        Total available CPU 9164.0
//        Total available BW 12360.0
//        Cost of execution in cloud = 1.2210100104166684E9
//        Total network usage = 24699.35
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 153 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-0 for 323 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-4 for 219 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-0 for 308 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 72 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 117 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 176 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 235 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 294 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 344 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 405 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 466 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 512 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 578 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 70 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-4 for 93 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 38 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-4 for 117 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 500
//        Number of Request successfully executed: 375
//        Total CPU Execution time: 581.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 1899
//        Overall Application Network Delay: 56.88497048611111
//        Percentage of Successfully processed request: 0.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 56.88497048611111 ms
//        Average Request execution time: 2.091090729166723 ms
//        Achieved Throughput: 860.59 /s
//        Required Throughput: 6 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 10.17578125ms
//        Average Execution time: 10.455273437499997 ms
//        Minimum Execution time: 10.080468749999994 ms
//        Maximum Execution time: 10.830078125 ms
//        Required responsiveness within 11.0 ms
//        Meet responsiveness requirement:500
//        did not meet responsiveness requirement:0
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 20.626302083333336ms
//        Average Execution time: 23.341276041666667 ms
//        Minimum Execution time: 20.065104166666664 ms
//        Maximum Execution time: 26.61744791666667 ms
//        Required responsiveness within 22.0 ms
//        Meet responsiveness requirement:335
//        did not meet responsiveness requirement:165
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 1.25ms
//        Average Execution time: 11.298356119791658 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 12.531608072916649 ms
//        Required responsiveness within 11.0 ms
//        Meet responsiveness requirement:453
//        did not meet responsiveness requirement:47
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.089065 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390675 ms
//        Required responsiveness within 153.0 ms
//        Meet responsiveness requirement:500
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 6.0ms
//        Max delay faced in sending data: 156.078173ms
//        Average Execution time: 162.50278237499998 ms
//        Minimum Execution time: 156.078173 ms
//        Maximum Execution time: 168.92739174999997 ms
//        Required responsiveness within 163.0 ms
//        Meet responsiveness requirement:498
//        did not meet responsiveness requirement:2
//
//
//        Cost In Cloud: 1.2210100104166684E9
//        Network used: 24699.35
//
//        data 4:
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 638
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1, Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor, Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1, Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1, GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 111.22324348958342
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.03385251590828395
//        Anomaly_Detected_Object_Data ---> 0.20622559168776788
//        Production_Schedule_Data ---> 5.422591541553393
//        Processed_Visual_Sensor_Data ---> 0.06992187743895444
//        Action_Plan_Execution_Data ---> 0.054621563624254355
//        Visual_Sensor ---> 0.1938228816290893
//        Temperature_Sensor ---> 0.21106949653534118
//        Analysed_Resource_Data ---> 0.10555217675474272
//        Processed_Temperature_Sensor_Data ---> 0.07048276381614493
//        Analysed_Resource_Report_Data ---> 6.988281250000167
//        Request_sensor_Data ---> 0.20521718666952757
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 8000.0
//        Available RAM = 7168.0
//        Available Storage = 110592.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 11516.892708333336
//        Available RAM = 6912.0
//        Available Storage = 179000.0
//        Available BW = 1872.0
//        Available CPU = 2472.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 11721.68828125
//        Available RAM = 2944.0
//        Available Storage = 155448.0
//        Available BW = 2172.0
//        Available CPU = 2772.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 11564.541666666668
//        Available RAM = 5888.0
//        Available Storage = 191088.0
//        Available BW = 1872.0
//        Available CPU = 1448.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11285.871354166666
//        Available RAM = 6656.0
//        Available Storage = 52448.0
//        Available BW = 1572.0
//        Available CPU = 1448.0
//
//        ---------Total available resources--------------
//        Total available RAM 29568.0
//        Total available Storage 688576.0
//        Total available CPU 10188.0
//        Total available BW 10560.0
//        Cost of execution in cloud = 1.1465613280000026E9
//        Total network usage = 43127.45
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 391 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-3 for 302 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-2 for 223 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-1 for 324 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 83 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 163 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 248 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 324 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 408 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 488 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 573 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 653 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 735 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 812 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 141 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-4 for 236 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 84 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-4 for 280 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 500
//        Number of Request successfully executed: 500
//        Total CPU Execution time: 647.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 2793
//        Overall Application Network Delay: 111.22324348958342
//        Percentage of Successfully processed request: 100.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 111.22324348958342 ms
//        Average Request execution time: 59.48594750000052 ms
//        Achieved Throughput: 772.8 /s
//        Required Throughput: 5 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 10.17578125ms
//        Average Execution time: 11.046451822916666 ms
//        Minimum Execution time: 10.064192708333337 ms
//        Maximum Execution time: 12.028710937499994 ms
//        Required responsiveness within 10.0 ms
//        Meet responsiveness requirement:0
//        did not meet responsiveness requirement:500
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 20.665364583333336ms
//        Average Execution time: 42.687662760416856 ms
//        Minimum Execution time: 20.52552083333334 ms
//        Maximum Execution time: 64.84980468750038 ms
//        Required responsiveness within 24.0 ms
//        Meet responsiveness requirement:174
//        did not meet responsiveness requirement:326
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 2.0833333333333335ms
//        Average Execution time: 15.622916666666676 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 21.180729166666687 ms
//        Required responsiveness within 11.0 ms
//        Meet responsiveness requirement:313
//        did not meet responsiveness requirement:187
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.089065 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390675 ms
//        Required responsiveness within 162.0 ms
//        Meet responsiveness requirement:500
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 5.666666666666667ms
//        Max delay faced in sending data: 156.078173ms
//        Average Execution time: 163.27270925000005 ms
//        Minimum Execution time: 156.078173 ms
//        Maximum Execution time: 170.4672455000001 ms
//        Required responsiveness within 169.0 ms
//        Meet responsiveness requirement:498
//        did not meet responsiveness requirement:2
//
//
//        Cost In Cloud: 1.1465613280000026E9
//        Network used: 43127.45
//
//        data 5:
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 471
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1, Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor, Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1, Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1, GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 31.982736909256126
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.06903745212782428
//        Anomaly_Detected_Object_Data ---> 0.14751296794983015
//        Production_Schedule_Data ---> 0.23864314635495512
//        Processed_Visual_Sensor_Data ---> 0.19593012512012895
//        Action_Plan_Execution_Data ---> 0.07978771155736991
//        Visual_Sensor ---> 1.1114827201384916
//        Temperature_Sensor ---> 1.565101312909843
//        Analysed_Resource_Data ---> 0.34232679016158046
//        Processed_Temperature_Sensor_Data ---> 2.291306222254323
//        Analysed_Resource_Report_Data ---> 0.35577582974511873
//        Request_sensor_Data ---> 0.7858026067875168
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 11637.790364583328
//        Available RAM = 3584.0
//        Available Storage = 176152.0
//        Available BW = 1272.0
//        Available CPU = 1272.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 8000.0
//        Available RAM = 2048.0
//        Available Storage = 61440.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 8000.0
//        Available RAM = 5120.0
//        Available Storage = 243712.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 8000.0
//        Available RAM = 3072.0
//        Available Storage = 39936.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11604.594270833328
//        Available RAM = 2560.0
//        Available Storage = 48552.0
//        Available BW = 1872.0
//        Available CPU = 1148.0
//
//        ---------Total available resources--------------
//        Total available RAM 16384.0
//        Total available Storage 569792.0
//        Total available CPU 9588.0
//        Total available BW 12360.0
//        Cost of execution in cloud = 1.2989202790000017E9
//        Total network usage = 20217.6
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 111 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-0 for 279 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-0 for 245 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-0 for 310 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 62 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 98 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 158 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 206 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 256 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 295 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 335 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 389 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 426 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 483 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 58 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-4 for 72 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 32 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-4 for 101 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 500
//        Number of Request successfully executed: 298
//        Total CPU Execution time: 479.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 1691
//        Overall Application Network Delay: 31.982736909256126
//        Percentage of Successfully processed request: 0.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 31.982736909256126 ms
//        Average Request execution time: 1.8948667708332891 ms
//        Achieved Throughput: 1043.84 /s
//        Required Throughput: 7 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 6.578125ms
//        Average Execution time: 10.48977864583334 ms
//        Minimum Execution time: 10.029296875 ms
//        Maximum Execution time: 10.95026041666668 ms
//        Required responsiveness within 13.0 ms
//        Meet responsiveness requirement:500
//        did not meet responsiveness requirement:0
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 20.959635416666664ms
//        Average Execution time: 22.838964843750006 ms
//        Minimum Execution time: 20.065104166666664 ms
//        Maximum Execution time: 25.612825520833347 ms
//        Required responsiveness within 21.0 ms
//        Meet responsiveness requirement:279
//        did not meet responsiveness requirement:221
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 2.5ms
//        Average Execution time: 11.066604062499986 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 12.068103958333305 ms
//        Required responsiveness within 12.0 ms
//        Meet responsiveness requirement:499
//        did not meet responsiveness requirement:1
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.089065 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390675 ms
//        Required responsiveness within 165.0 ms
//        Meet responsiveness requirement:500
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 5.5ms
//        Max delay faced in sending data: 157.078173ms
//        Average Execution time: 157.55161050000004 ms
//        Minimum Execution time: 157.078173 ms
//        Maximum Execution time: 158.02504800000008 ms
//        Required responsiveness within 172.0 ms
//        Meet responsiveness requirement:500
//        did not meet responsiveness requirement:0
//
//
//        Cost In Cloud: 1.2989202790000017E9
//        Network used: 20217.6

//        Number of request: 1000
//        data 1:
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 872
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1, Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor, Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1, Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1, GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 134.73564036458336
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.020025247890703035
//        Anomaly_Detected_Object_Data ---> 0.07795650972299235
//        Production_Schedule_Data ---> 7.33488886962765
//        Processed_Visual_Sensor_Data ---> 0.20865486589173687
//        Action_Plan_Execution_Data ---> 0.043033576583875605
//        Visual_Sensor ---> 1.4700291605911617
//        Temperature_Sensor ---> 6.034950637224128
//        Analysed_Resource_Data ---> 0.07256427415685673
//        Processed_Temperature_Sensor_Data ---> 0.10328577379592939
//        Analysed_Resource_Report_Data ---> 20.175000000443458
//        Request_sensor_Data ---> 0.22932346203045284
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 11794.209765625
//        Available RAM = 2560.0
//        Available Storage = 191312.0
//        Available BW = 672.0
//        Available CPU = 1148.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 8000.0
//        Available RAM = 5120.0
//        Available Storage = 69632.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 8000.0
//        Available RAM = 4096.0
//        Available Storage = 183296.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 8000.0
//        Available RAM = 5120.0
//        Available Storage = 142336.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11443.876432291676
//        Available RAM = 5760.0
//        Available Storage = 248032.0
//        Available BW = 1572.0
//        Available CPU = 1872.0
//
//        ---------Total available resources--------------
//        Total available RAM 22656.0
//        Total available Storage 834608.0
//        Total available CPU 11212.0
//        Total available BW 11460.0
//        Cost of execution in cloud = 1.1572071087500036E9
//        Total network usage = 43825.95
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 295 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-4 for 570 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-0 for 387 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-4 for 620 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 125 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 191 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 318 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 428 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 515 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 609 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 741 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 846 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 964 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 1095 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 102 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-0 for 242 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 56 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-0 for 292 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 1000
//        Number of Request successfully executed: 750
//        Total CPU Execution time: 883.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 3659
//        Overall Application Network Delay: 134.73564036458336
//        Percentage of Successfully processed request: 0.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 134.73564036458336 ms
//        Average Request execution time: 96.1514301875003 ms
//        Achieved Throughput: 1132.5 /s
//        Required Throughput: 15 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 10.17578125ms
//        Average Execution time: 10.465755208333334 ms
//        Minimum Execution time: 10.062369791666669 ms
//        Maximum Execution time: 10.869140625 ms
//        Required responsiveness within 10.0 ms
//        Meet responsiveness requirement:0
//        did not meet responsiveness requirement:1000
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 20.459635416666664ms
//        Average Execution time: 57.54147135416699 ms
//        Minimum Execution time: 20.065104166666664 ms
//        Maximum Execution time: 95.01783854166732 ms
//        Required responsiveness within 23.0 ms
//        Meet responsiveness requirement:565
//        did not meet responsiveness requirement:435
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 2.5ms
//        Average Execution time: 15.588407906249707 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 21.11171164583275 ms
//        Required responsiveness within 12.0 ms
//        Meet responsiveness requirement:930
//        did not meet responsiveness requirement:70
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.089065 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390675 ms
//        Required responsiveness within 172.0 ms
//        Meet responsiveness requirement:1000
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 5.5ms
//        Max delay faced in sending data: 155.578173ms
//        Average Execution time: 163.32192550000008 ms
//        Minimum Execution time: 155.578173 ms
//        Maximum Execution time: 171.06567800000016 ms
//        Required responsiveness within 170.0 ms
//        Meet responsiveness requirement:998
//        did not meet responsiveness requirement:2
//
//
//        Cost In Cloud: 1.1572071087500036E9
//        Network used: 43825.95
//
//        Data 2:
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 917
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1, Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor, Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1, Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1, GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 130.9404013888886
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.026635917239283626
//        Anomaly_Detected_Object_Data ---> 0.09183995924457389
//        Production_Schedule_Data ---> 5.911151305859648
//        Processed_Visual_Sensor_Data ---> 0.4138515131350293
//        Action_Plan_Execution_Data ---> 0.029595781544783734
//        Visual_Sensor ---> 2.450895831710637
//        Temperature_Sensor ---> 1.6132458859452532
//        Analysed_Resource_Data ---> 0.11344501383791664
//        Processed_Temperature_Sensor_Data ---> 0.06680430761375213
//        Analysed_Resource_Report_Data ---> 33.26088664369677
//        Request_sensor_Data ---> 0.25163678713186116
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 11613.977343749999
//        Available RAM = 5504.0
//        Available Storage = 132120.0
//        Available BW = 372.0
//        Available CPU = 548.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 8000.0
//        Available RAM = 2048.0
//        Available Storage = 123904.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 8000.0
//        Available RAM = 3072.0
//        Available Storage = 223232.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 8000.0
//        Available RAM = 2048.0
//        Available Storage = 258048.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11443.614322916666
//        Available RAM = 2688.0
//        Available Storage = 172656.0
//        Available BW = 1572.0
//        Available CPU = 2172.0
//
//        ---------Total available resources--------------
//        Total available RAM 15360.0
//        Total available Storage 909960.0
//        Total available CPU 9888.0
//        Total available BW 11160.0
//        Cost of execution in cloud = 1.1543151496666698E9
//        Total network usage = 38482.55
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 165 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-4 for 575 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-0 for 365 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-4 for 609 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 99 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 175 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 278 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 346 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 474 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 610 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 704 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 836 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 945 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 1052 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 78 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-0 for 127 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 40 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-0 for 288 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 1000
//        Number of Request successfully executed: 750
//        Total CPU Execution time: 927.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 3299
//        Overall Application Network Delay: 130.9404013888886
//        Percentage of Successfully processed request: 0.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 130.9404013888886 ms
//        Average Request execution time: 52.322526041666976 ms
//        Achieved Throughput: 1078.75 /s
//        Required Throughput: 15 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 10.17578125ms
//        Average Execution time: 10.568749999999994 ms
//        Minimum Execution time: 10.063281249999989 ms
//        Maximum Execution time: 11.07421875 ms
//        Required responsiveness within 14.0 ms
//        Meet responsiveness requirement:1000
//        did not meet responsiveness requirement:0
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 20.959635416666664ms
//        Average Execution time: 42.66712239583339 ms
//        Minimum Execution time: 20.065104166666664 ms
//        Maximum Execution time: 65.26914062500012 ms
//        Required responsiveness within 21.0 ms
//        Meet responsiveness requirement:580
//        did not meet responsiveness requirement:420
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 0.9722222222222223ms
//        Average Execution time: 11.366278541666683 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 12.667452916666699 ms
//        Required responsiveness within 12.0 ms
//        Meet responsiveness requirement:988
//        did not meet responsiveness requirement:12
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.0890675 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390725 ms
//        Required responsiveness within 163.0 ms
//        Meet responsiveness requirement:1000
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 5.5ms
//        Max delay faced in sending data: 157.078173ms
//        Average Execution time: 161.02270174999995 ms
//        Minimum Execution time: 157.078173 ms
//        Maximum Execution time: 164.9672304999999 ms
//        Required responsiveness within 166.0 ms
//        Meet responsiveness requirement:1000
//        did not meet responsiveness requirement:0
//
//
//        Cost In Cloud: 1.1543151496666698E9
//        Network used: 38482.55
//
//        Data 3:
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 1287
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1, Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor, Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1, Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1, GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 60.759174921138204
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.0346975620093999
//        Anomaly_Detected_Object_Data ---> 4.237311044512116
//        Production_Schedule_Data ---> 0.23254683561039838
//        Processed_Visual_Sensor_Data ---> 11.07027259509683
//        Action_Plan_Execution_Data ---> 0.06409647984679392
//        Visual_Sensor ---> 2.4301185814124806
//        Temperature_Sensor ---> 3.6965569823447075
//        Analysed_Resource_Data ---> 1.4906265007046522
//        Processed_Temperature_Sensor_Data ---> 0.12849532573978661
//        Analysed_Resource_Report_Data ---> 4.641015634358954
//        Request_sensor_Data ---> 1.118117105270888
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 11334.966796874989
//        Available RAM = 3584.0
//        Available Storage = 149728.0
//        Available BW = 1872.0
//        Available CPU = 1572.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 8000.0
//        Available RAM = 5120.0
//        Available Storage = 175104.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 8000.0
//        Available RAM = 7168.0
//        Available Storage = 218112.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 8000.0
//        Available RAM = 6144.0
//        Available Storage = 141312.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11342.191536458331
//        Available RAM = 6784.0
//        Available Storage = 235120.0
//        Available BW = 1272.0
//        Available CPU = 848.0
//
//        ---------Total available resources--------------
//        Total available RAM 28800.0
//        Total available Storage 919376.0
//        Total available CPU 9588.0
//        Total available BW 12360.0
//        Cost of execution in cloud = 1.2309615480000029E9
//        Total network usage = 40440.25
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 312 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-0 for 551 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-0 for 480 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-0 for 620 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 105 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 214 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 322 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 418 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 511 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 606 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 687 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 786 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 876 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 968 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 123 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-4 for 177 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 73 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-4 for 243 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 1000
//        Number of Request successfully executed: 601
//        Total CPU Execution time: 1293.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 3547
//        Overall Application Network Delay: 60.759174921138204
//        Percentage of Successfully processed request: 0.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 60.759174921138204 ms
//        Average Request execution time: 48.29265322916777 ms
//        Achieved Throughput: 773.4 /s
//        Required Throughput: 11 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 5.453125ms
//        Average Execution time: 12.508593750000003 ms
//        Minimum Execution time: 10.029296875 ms
//        Maximum Execution time: 14.987890625000006 ms
//        Required responsiveness within 11.0 ms
//        Meet responsiveness requirement:893
//        did not meet responsiveness requirement:107
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 20.459635416666664ms
//        Average Execution time: 39.098404947917125 ms
//        Minimum Execution time: 20.065104166666664 ms
//        Maximum Execution time: 58.131705729167585 ms
//        Required responsiveness within 23.0 ms
//        Meet responsiveness requirement:669
//        did not meet responsiveness requirement:331
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 2.0833333333333335ms
//        Average Execution time: 13.747916666666676 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 17.430729166666687 ms
//        Required responsiveness within 13.0 ms
//        Meet responsiveness requirement:951
//        did not meet responsiveness requirement:49
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.089065 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390675 ms
//        Required responsiveness within 169.0 ms
//        Meet responsiveness requirement:1000
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 6.0ms
//        Max delay faced in sending data: 155.578173ms
//        Average Execution time: 158.52583175000007 ms
//        Minimum Execution time: 155.578173 ms
//        Maximum Execution time: 161.47349050000014 ms
//        Required responsiveness within 167.0 ms
//        Meet responsiveness requirement:1000
//        did not meet responsiveness requirement:0
//
//
//        Cost In Cloud: 1.2309615480000029E9
//        Network used: 40440.25
//
//        data 4:
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 930
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1, Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor, Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1, Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1, GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 119.90310703124979
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.02225259381687698
//        Anomaly_Detected_Object_Data ---> 0.09869178927132725
//        Production_Schedule_Data ---> 4.728619874746464
//        Processed_Visual_Sensor_Data ---> 0.11568903864039845
//        Action_Plan_Execution_Data ---> 0.03493045811218086
//        Visual_Sensor ---> 2.928857371323099
//        Temperature_Sensor ---> 0.5428192361149846
//        Analysed_Resource_Data ---> 0.19036567150501096
//        Processed_Temperature_Sensor_Data ---> 0.060610449015148775
//        Analysed_Resource_Report_Data ---> 54.53828125000064
//        Request_sensor_Data ---> 0.5286624288988325
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 11689.161588541667
//        Available RAM = 3712.0
//        Available Storage = 53472.0
//        Available BW = 1272.0
//        Available CPU = 2172.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 8000.0
//        Available RAM = 2048.0
//        Available Storage = 98304.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 8000.0
//        Available RAM = 5120.0
//        Available Storage = 143360.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 11657.962109375
//        Available RAM = 3968.0
//        Available Storage = 168760.0
//        Available BW = 2172.0
//        Available CPU = 1748.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11493.014843750007
//        Available RAM = 5760.0
//        Available Storage = 123304.0
//        Available BW = 972.0
//        Available CPU = 848.0
//
//        ---------Total available resources--------------
//        Total available RAM 20608.0
//        Total available Storage 587200.0
//        Total available CPU 9888.0
//        Total available BW 10560.0
//        Cost of execution in cloud = 1.0848038510000043E9
//        Total network usage = 56740.5
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 446 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-3 for 474 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-0 for 431 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-0 for 560 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 140 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 246 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 365 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 470 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 596 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 715 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 817 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 971 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 1079 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 1190 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 173 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-4 for 337 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 98 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-4 for 407 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 1000
//        Number of Request successfully executed: 750
//        Total CPU Execution time: 941.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 4116
//        Overall Application Network Delay: 119.90310703124979
//        Percentage of Successfully processed request: 0.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 119.90310703124979 ms
//        Average Request execution time: 85.49128104166746 ms
//        Achieved Throughput: 1062.7 /s
//        Required Throughput: 14 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 5.453125ms
//        Average Execution time: 10.429296875000006 ms
//        Minimum Execution time: 10.029296875 ms
//        Maximum Execution time: 10.829296875000011 ms
//        Required responsiveness within 12.0 ms
//        Meet responsiveness requirement:1000
//        did not meet responsiveness requirement:0
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 20.498697916666664ms
//        Average Execution time: 54.383854166666964 ms
//        Minimum Execution time: 20.065104166666664 ms
//        Maximum Execution time: 88.70260416666727 ms
//        Required responsiveness within 20.0 ms
//        Meet responsiveness requirement:0
//        did not meet responsiveness requirement:1000
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 0.9722222222222223ms
//        Average Execution time: 14.67682541666668 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 19.288546666666697 ms
//        Required responsiveness within 13.0 ms
//        Meet responsiveness requirement:915
//        did not meet responsiveness requirement:85
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.089065 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390675 ms
//        Required responsiveness within 164.0 ms
//        Meet responsiveness requirement:1000
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 7.0ms
//        Max delay faced in sending data: 155.578173ms
//        Average Execution time: 164.31567300000006 ms
//        Minimum Execution time: 155.578173 ms
//        Maximum Execution time: 173.05317300000013 ms
//        Required responsiveness within 174.0 ms
//        Meet responsiveness requirement:1000
//        did not meet responsiveness requirement:0
//
//
//        Cost In Cloud: 1.0848038510000043E9
//        Network used: 56740.5
//
//        data 5:
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 881
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1, Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor, Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1, Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1, GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 133.4636227430554
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.022622885648471938
//        Anomaly_Detected_Object_Data ---> 0.158628736541425
//        Production_Schedule_Data ---> 0.38528705756336623
//        Processed_Visual_Sensor_Data ---> 0.46022543254386467
//        Action_Plan_Execution_Data ---> 0.062056390789600915
//        Visual_Sensor ---> 1.7656152856543246
//        Temperature_Sensor ---> 3.642068782338744
//        Analysed_Resource_Data ---> 0.16223685228887366
//        Processed_Temperature_Sensor_Data ---> 3.637625776142471
//        Analysed_Resource_Report_Data ---> 20.100009952563195
//        Request_sensor_Data ---> 0.22766534380286838
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 11811.457629979233
//        Available RAM = 4608.0
//        Available Storage = 173280.0
//        Available BW = 1572.0
//        Available CPU = 1148.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 8000.0
//        Available RAM = 3072.0
//        Available Storage = 207872.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 8000.0
//        Available RAM = 5120.0
//        Available Storage = 69632.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 8000.0
//        Available RAM = 7168.0
//        Available Storage = 174080.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11720.002539062501
//        Available RAM = 5888.0
//        Available Storage = 156896.0
//        Available BW = 1872.0
//        Available CPU = 1148.0
//
//        ---------Total available resources--------------
//        Total available RAM 25856.0
//        Total available Storage 781760.0
//        Total available CPU 10488.0
//        Total available BW 12660.0
//        Cost of execution in cloud = 1.1569489155000033E9
//        Total network usage = 43489.35
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 235 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-4 for 635 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-0 for 386 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-4 for 607 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 113 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 188 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 314 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 402 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 493 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 611 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 730 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 835 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 956 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 1091 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 74 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-0 for 247 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 43 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-0 for 282 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 1000
//        Number of Request successfully executed: 750
//        Total CPU Execution time: 893.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 3600
//        Overall Application Network Delay: 133.4636227430554
//        Percentage of Successfully processed request: 0.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 133.4636227430554 ms
//        Average Request execution time: 69.8528044791673 ms
//        Achieved Throughput: 1119.82 /s
//        Required Throughput: 8 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 10.17578125ms
//        Average Execution time: 10.513867187499997 ms
//        Minimum Execution time: 10.080468749999994 ms
//        Maximum Execution time: 10.947265625 ms
//        Required responsiveness within 14.0 ms
//        Meet responsiveness requirement:1000
//        did not meet responsiveness requirement:0
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 20.459635416666664ms
//        Average Execution time: 53.43723958333358 ms
//        Minimum Execution time: 20.065104166666664 ms
//        Maximum Execution time: 86.8093750000005 ms
//        Required responsiveness within 23.0 ms
//        Meet responsiveness requirement:565
//        did not meet responsiveness requirement:435
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 1.6666666666666667ms
//        Average Execution time: 12.576044166666671 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 15.086984166666676 ms
//        Required responsiveness within 13.0 ms
//        Meet responsiveness requirement:980
//        did not meet responsiveness requirement:20
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.089065 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390675 ms
//        Required responsiveness within 161.0 ms
//        Meet responsiveness requirement:1000
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 6.0ms
//        Max delay faced in sending data: 155.578173ms
//        Average Execution time: 163.76905841666672 ms
//        Minimum Execution time: 155.578173 ms
//        Maximum Execution time: 171.95994383333345 ms
//        Required responsiveness within 170.0 ms
//        Meet responsiveness requirement:999
//        did not meet responsiveness requirement:1
//
//
//        Cost In Cloud: 1.1569489155000033E9
//        Network used: 43489.35
//
//
//        Number of request: 1500
//        data 1:
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 15999
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1, Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor, Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1, Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1, GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 125.97636177418269
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.06096073541287027
//        Anomaly_Detected_Object_Data ---> 24.29290837796877
//        Production_Schedule_Data ---> 4.7066980815449915
//        Processed_Visual_Sensor_Data ---> 72.75371185691583
//        Action_Plan_Execution_Data ---> 0.06301189546651405
//        Visual_Sensor ---> 1.1241636875364118
//        Temperature_Sensor ---> 7.922746008007357
//        Analysed_Resource_Data ---> 15.514713542213805
//        Processed_Temperature_Sensor_Data ---> 5.933408525663857
//        Analysed_Resource_Report_Data ---> 3.6339843754278824
//        Request_sensor_Data ---> 2.772495693249315
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 11541.507812500002
//        Available RAM = 2816.0
//        Available Storage = 206448.0
//        Available BW = 1872.0
//        Available CPU = 2772.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 8000.0
//        Available RAM = 3072.0
//        Available Storage = 87040.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 8000.0
//        Available RAM = 7168.0
//        Available Storage = 115712.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 8000.0
//        Available RAM = 4096.0
//        Available Storage = 39936.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11821.334374999984
//        Available RAM = 3072.0
//        Available Storage = 111240.0
//        Available BW = 672.0
//        Available CPU = 1572.0
//
//        ---------Total available resources--------------
//        Total available RAM 20224.0
//        Total available Storage 560376.0
//        Total available CPU 12536.0
//        Total available BW 11760.0
//        Cost of execution in cloud = 1.1653536662500045E9
//        Total network usage = 60079.0
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 437 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-4 for 962 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-4 for 909 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-4 for 1076 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 142 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 270 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 407 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 548 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 710 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 879 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 1031 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 1179 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 1315 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 1459 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 159 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-0 for 102 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 90 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-4 for 306 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 1500
//        Number of Request successfully executed: 902
//        Total CPU Execution time: 16004.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 5500
//        Overall Application Network Delay: 125.97636177418269
//        Percentage of Successfully processed request: 0.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 125.97636177418269 ms
//        Average Request execution time: 75.07897635416651 ms
//        Achieved Throughput: 93.73 /s
//        Required Throughput: 17 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 5.453125ms
//        Average Execution time: 23.533170572916703 ms
//        Minimum Execution time: 10.029296875 ms
//        Maximum Execution time: 37.037044270833405 ms
//        Required responsiveness within 14.0 ms
//        Meet responsiveness requirement:1111
//        did not meet responsiveness requirement:389
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 16.6953125ms
//        Average Execution time: 39.621321614583344 ms
//        Minimum Execution time: 20.065104166666664 ms
//        Maximum Execution time: 59.177539062500024 ms
//        Required responsiveness within 22.0 ms
//        Meet responsiveness requirement:961
//        did not meet responsiveness requirement:539
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 5.514322916666667ms
//        Average Execution time: 16.926171875 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 23.78723958333334 ms
//        Required responsiveness within 12.0 ms
//        Meet responsiveness requirement:1342
//        did not meet responsiveness requirement:158
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.0890675 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390725 ms
//        Required responsiveness within 168.0 ms
//        Meet responsiveness requirement:1500
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 5.666666666666667ms
//        Max delay faced in sending data: 155.578173ms
//        Average Execution time: 162.23676925000007 ms
//        Minimum Execution time: 155.578173 ms
//        Maximum Execution time: 168.89536550000014 ms
//        Required responsiveness within 158.0 ms
//        Meet responsiveness requirement:1384
//        did not meet responsiveness requirement:116
//
//
//        Cost In Cloud: 1.1653536662500045E9
//        Network used: 60079.0
//
//        data 2:
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 2276
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1, Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor, Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1, Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1, GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 376.68561523437523
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.030087458357830704
//        Anomaly_Detected_Object_Data ---> 0.7815967671154085
//        Production_Schedule_Data ---> 0.2888195823815014
//        Processed_Visual_Sensor_Data ---> 0.08619791666668183
//        Action_Plan_Execution_Data ---> 0.06949569169336353
//        Visual_Sensor ---> 0.4778560555151644
//        Temperature_Sensor ---> 0.19629137038657202
//        Analysed_Resource_Data ---> 0.15678555013566264
//        Processed_Temperature_Sensor_Data ---> 0.13219046358957695
//        Analysed_Resource_Report_Data ---> 55.21486509657748
//        Request_sensor_Data ---> 0.2716993243033763
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 11399.623567708331
//        Available RAM = 3456.0
//        Available Storage = 145632.0
//        Available BW = 672.0
//        Available CPU = 248.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 8000.0
//        Available RAM = 4096.0
//        Available Storage = 219136.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 8000.0
//        Available RAM = 4096.0
//        Available Storage = 118784.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 11127.93919270825
//        Available RAM = 7040.0
//        Available Storage = 197432.0
//        Available BW = 1872.0
//        Available CPU = 1748.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11268.018749999912
//        Available RAM = 2816.0
//        Available Storage = 251704.0
//        Available BW = 1872.0
//        Available CPU = 1748.0
//
//        ---------Total available resources--------------
//        Total available RAM 21504.0
//        Total available Storage 932688.0
//        Total available CPU 8864.0
//        Total available BW 10560.0
//        Cost of execution in cloud = 7.919276666666739E8
//        Total network usage = 89646.95
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 492 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-4 for 979 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-0 for 580 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-3 for 909 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 183 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 373 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 559 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 739 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 984 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 1219 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 1403 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 1629 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 1824 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 2070 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 216 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-0 for 294 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 126 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-0 for 843 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 1500
//        Number of Request successfully executed: 1500
//        Total CPU Execution time: 2283.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 6509
//        Overall Application Network Delay: 376.68561523437523
//        Percentage of Successfully processed request: 100.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 376.68561523437523 ms
//        Average Request execution time: 75.82930187500006 ms
//        Achieved Throughput: 657.03 /s
//        Required Throughput: 17 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 10.17578125ms
//        Average Execution time: 10.758007812499997 ms
//        Minimum Execution time: 10.080468749999994 ms
//        Maximum Execution time: 11.435546875 ms
//        Required responsiveness within 10.0 ms
//        Meet responsiveness requirement:0
//        did not meet responsiveness requirement:1500
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 20.498697916666664ms
//        Average Execution time: 57.2172526041667 ms
//        Minimum Execution time: 20.153906250000013 ms
//        Maximum Execution time: 94.2805989583334 ms
//        Required responsiveness within 21.0 ms
//        Meet responsiveness requirement:584
//        did not meet responsiveness requirement:916
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 1.25ms
//        Average Execution time: 11.235614479166694 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 12.406124791666722 ms
//        Required responsiveness within 12.0 ms
//        Meet responsiveness requirement:1498
//        did not meet responsiveness requirement:2
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.089065 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390675 ms
//        Required responsiveness within 168.0 ms
//        Meet responsiveness requirement:1500
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 5.5ms
//        Max delay faced in sending data: 155.578173ms
//        Average Execution time: 158.712936125 ms
//        Minimum Execution time: 155.578173 ms
//        Maximum Execution time: 161.84769925 ms
//        Required responsiveness within 167.0 ms
//        Meet responsiveness requirement:1500
//        did not meet responsiveness requirement:0
//
//
//        Cost In Cloud: 7.919276666666739E8
//        Network used: 89646.95
//
//        data 3:
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 2578
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1, Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor, Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1, Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1, GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 376.6795266927078
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.04443129068620065
//        Anomaly_Detected_Object_Data ---> 0.12326980391526808
//        Production_Schedule_Data ---> 0.25526818767874115
//        Processed_Visual_Sensor_Data ---> 0.15148211594957284
//        Action_Plan_Execution_Data ---> 0.06521395381801624
//        Visual_Sensor ---> 1.3299775094702402
//        Temperature_Sensor ---> 1.581585841358217
//        Analysed_Resource_Data ---> 0.088791099383131
//        Processed_Temperature_Sensor_Data ---> 0.12395306987028964
//        Analysed_Resource_Report_Data ---> 142.27239990085445
//        Request_sensor_Data ---> 1.5915888085081613
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 11231.579166666581
//        Available RAM = 7040.0
//        Available Storage = 64312.0
//        Available BW = 1872.0
//        Available CPU = 1448.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 10988.47278645822
//        Available RAM = 2816.0
//        Available Storage = 44856.0
//        Available BW = 1872.0
//        Available CPU = 2472.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 8000.0
//        Available RAM = 2048.0
//        Available Storage = 87040.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 8000.0
//        Available RAM = 4096.0
//        Available Storage = 219136.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11612.671744791676
//        Available RAM = 5632.0
//        Available Storage = 109992.0
//        Available BW = 672.0
//        Available CPU = 848.0
//
//        ---------Total available resources--------------
//        Total available RAM 21632.0
//        Total available Storage 525336.0
//        Total available CPU 9888.0
//        Total available BW 10560.0
//        Cost of execution in cloud = 7.190003023333418E8
//        Total network usage = 105816.75
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 726 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-0 for 949 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-4 for 601 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-1 for 1025 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 208 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 411 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 621 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 826 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 1065 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 1301 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 1518 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 1747 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 1952 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 2191 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 303 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-4 for 718 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 176 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-4 for 857 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 1500
//        Number of Request successfully executed: 1500
//        Total CPU Execution time: 2583.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 7546
//        Overall Application Network Delay: 376.6795266927078
//        Percentage of Successfully processed request: 100.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 376.6795266927078 ms
//        Average Request execution time: 179.69615175000024 ms
//        Achieved Throughput: 580.72 /s
//        Required Throughput: 16 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 10.17578125ms
//        Average Execution time: 10.642447916666669 ms
//        Minimum Execution time: 10.064192708333337 ms
//        Maximum Execution time: 11.220703125 ms
//        Required responsiveness within 13.0 ms
//        Meet responsiveness requirement:1500
//        did not meet responsiveness requirement:0
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 20.665364583333336ms
//        Average Execution time: 109.29628906250052 ms
//        Minimum Execution time: 20.45338541666667 ms
//        Maximum Execution time: 198.13919270833438 ms
//        Required responsiveness within 23.0 ms
//        Meet responsiveness requirement:182
//        did not meet responsiveness requirement:1318
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 0.9722222222222223ms
//        Average Execution time: 12.044167124999584 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 14.023230083332502 ms
//        Required responsiveness within 13.0 ms
//        Meet responsiveness requirement:1463
//        did not meet responsiveness requirement:37
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.089065 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390675 ms
//        Required responsiveness within 162.0 ms
//        Meet responsiveness requirement:1500
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 5.5ms
//        Max delay faced in sending data: 156.078173ms
//        Average Execution time: 165.85212883333327 ms
//        Minimum Execution time: 156.078173 ms
//        Maximum Execution time: 175.62608466666654 ms
//        Required responsiveness within 166.0 ms
//        Meet responsiveness requirement:1497
//        did not meet responsiveness requirement:3
//
//
//        Cost In Cloud: 7.190003023333418E8
//        Network used: 105816.75
//
//        data 4:
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 1049
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1, Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor, Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1, Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1, GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 214.72234265046384
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.01939349093956948
//        Anomaly_Detected_Object_Data ---> 0.27519599590482646
//        Production_Schedule_Data ---> 1.3381757316158789
//        Processed_Visual_Sensor_Data ---> 0.4822477815701544
//        Action_Plan_Execution_Data ---> 0.03590776397620077
//        Visual_Sensor ---> 1.45474372188557
//        Temperature_Sensor ---> 0.7121734374697336
//        Analysed_Resource_Data ---> 0.15670965254430816
//        Processed_Temperature_Sensor_Data ---> 0.19695423208512136
//        Analysed_Resource_Report_Data ---> 9.01181989486543
//        Request_sensor_Data ---> 0.29259301834672635
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 11532.727994791683
//        Available RAM = 2432.0
//        Available Storage = 39336.0
//        Available BW = 672.0
//        Available CPU = 1572.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 8000.0
//        Available RAM = 4096.0
//        Available Storage = 91136.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 8000.0
//        Available RAM = 5120.0
//        Available Storage = 205824.0
//        Available BW = 3072.0
//        Available CPU = 3072.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 8000.0
//        Available RAM = 4096.0
//        Available Storage = 95232.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11746.209960937507
//        Available RAM = 4736.0
//        Available Storage = 87464.0
//        Available BW = 1872.0
//        Available CPU = 1448.0
//
//        ---------Total available resources--------------
//        Total available RAM 20480.0
//        Total available Storage 518992.0
//        Total available CPU 11212.0
//        Total available BW 11760.0
//        Cost of execution in cloud = 1.019048135066672E9
//        Total network usage = 74115.5
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 522 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-4 for 902 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-0 for 603 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-4 for 880 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 178 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 281 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 468 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 674 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 839 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 1054 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 1250 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 1391 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 1505 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 1697 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 195 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-0 for 313 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 113 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-0 for 326 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 1500
//        Number of Request successfully executed: 1125
//        Total CPU Execution time: 1060.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 5551
//        Overall Application Network Delay: 214.72234265046384
//        Percentage of Successfully processed request: 0.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 214.72234265046384 ms
//        Average Request execution time: 76.51659945833342 ms
//        Achieved Throughput: 1415.09 /s
//        Required Throughput: 11 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 10.17578125ms
//        Average Execution time: 10.502734374999989 ms
//        Minimum Execution time: 10.077734374999977 ms
//        Maximum Execution time: 10.927734375 ms
//        Required responsiveness within 11.0 ms
//        Meet responsiveness requirement:1500
//        did not meet responsiveness requirement:0
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 20.959635416666664ms
//        Average Execution time: 41.97096354166687 ms
//        Minimum Execution time: 20.065104166666664 ms
//        Maximum Execution time: 63.876822916667074 ms
//        Required responsiveness within 21.0 ms
//        Meet responsiveness requirement:753
//        did not meet responsiveness requirement:747
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 1.25ms
//        Average Execution time: 21.188049833333118 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 32.31099549999957 ms
//        Required responsiveness within 10.0 ms
//        Meet responsiveness requirement:0
//        did not meet responsiveness requirement:1500
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.0890675 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390725 ms
//        Required responsiveness within 152.0 ms
//        Meet responsiveness requirement:1500
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 7.0ms
//        Max delay faced in sending data: 157.078173ms
//        Average Execution time: 168.20239425 ms
//        Minimum Execution time: 157.078173 ms
//        Maximum Execution time: 179.3266155 ms
//        Required responsiveness within 171.0 ms
//        Meet responsiveness requirement:1442
//        did not meet responsiveness requirement:58
//
//
//        Cost In Cloud: 1.019048135066672E9
//        Network used: 74115.5
//
//        data 5:
//        ============== RESULTS ==================
//        =========================================
//        EXECUTION TIME : 1215
//        =========================================
//        APPLICATION LOOP DELAYS
//        =========================================
//        [Visual_Sensor, Sensor_Application_Microservice_1, Site_Anomaly_Detection_Microservice_1, Resource_Event_Analysis_Microservice_1, Resource_Interface_Microservice_1, Sensor_Application_Microservice_1, Sensor_Data_Acquisition_Actuator, Temperature_Sensor, Sensor_Application_Microservice_1, Resource_Interface_Microservice_1, Resource_Event_Analysis_Microservice_1, Notification_Microservice_1, Monitoring_Work_Order_Microservice_1, GATEWAY_Microservice_1, Action_Plan_Execution_Microservice_1, Production_Schedule_Update_Microservice_1, GATEWAY_Microservice_1, Sensor_Application_Microservice_1, User_Display_Actuator] ---> 214.8846845486111
//        =========================================
//        TUPLE CPU EXECUTION DELAY
//        =========================================
//        Work_Order_Action_Plan_Data ---> 0.01718985795407259
//        Anomaly_Detected_Object_Data ---> 0.24523772151049208
//        Production_Schedule_Data ---> 0.28172143248801995
//        Processed_Visual_Sensor_Data ---> 0.12980605502239012
//        Action_Plan_Execution_Data ---> 0.040212682916704684
//        Visual_Sensor ---> 1.2350549364609678
//        Temperature_Sensor ---> 6.165793296680329
//        Analysed_Resource_Data ---> 0.5434716793750765
//        Processed_Temperature_Sensor_Data ---> 0.14506327973917824
//        Analysed_Resource_Report_Data ---> 55.92477161968637
//        Request_sensor_Data ---> 0.6047927704000785
//        =========================================
//
//        ---------Device Name: CF-4-0---------
//        Energy Consumed = 11767.740755208317
//        Available RAM = 6656.0
//        Available Storage = 169808.0
//        Available BW = 372.0
//        Available CPU = 248.0
//
//        ---------Device Name: CF-4-1---------
//        Energy Consumed = 8000.0
//        Available RAM = 2048.0
//        Available Storage = 82944.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-2---------
//        Energy Consumed = 8000.0
//        Available RAM = 2048.0
//        Available Storage = 62464.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-3---------
//        Energy Consumed = 8000.0
//        Available RAM = 7168.0
//        Available Storage = 32768.0
//        Available BW = 3072.0
//        Available CPU = 2048.0
//
//        ---------Device Name: CF-4-4---------
//        Energy Consumed = 11741.618229166668
//        Available RAM = 2688.0
//        Available Storage = 204400.0
//        Available BW = 1872.0
//        Available CPU = 1448.0
//
//        ---------Total available resources--------------
//        Total available RAM 20608.0
//        Total available Storage 552384.0
//        Total available CPU 7840.0
//        Total available BW 11460.0
//        Cost of execution in cloud = 1.012072580833338E9
//        Total network usage = 54650.8
//        Microservice GATEWAY_Microservice_1 waited in device Edge_Gateway_Fog_Device for 285 times.
//        Microservice Resource_Interface_Microservice_1 waited in device CF-4-4 for 870 times.
//        Microservice Site_Anomaly_Detection_Microservice_1 waited in device CF-4-0 for 600 times.
//        Microservice Resource_Event_Analysis_Microservice_1 waited in device CF-4-4 for 900 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-0 for 175 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-1 for 345 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-1 for 494 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-2-0 for 574 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-1 for 773 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-4-0 for 949 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-3-1 for 1035 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-1 for 1175 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-1-0 for 1311 times.
//        Microservice Sensor_Application_Microservice_1 waited in device SD-4-0-0 for 1524 times.
//        Microservice Action_Plan_Execution_Microservice_1 waited in device cloud for 132 times.
//        Microservice Monitoring_Work_Order_Microservice_1 waited in device CF-4-0 for 188 times.
//        Microservice Production_Schedule_Update_Microservice_1 waited in device cloud for 70 times.
//        Microservice Notification_Microservice_1 waited in device CF-4-0 for 467 times.
//        =========================================
//        ============== 0 ==================
//        =========================================
//        =========================================
//        ============== RESULTS ==================
//        =========================================
//        Number of Request: 1500
//        Number of Request successfully executed: 1125
//        Total CPU Execution time: 1225.0ms
//        Visual_Sensor
//        Resource_Interface_Microservice_2
//        Resource_Event_Analysis_Microservice_2
//        Temperature_Sensor
//        Number of requests waiting in the running state of the application 5036
//        Overall Application Network Delay: 214.8846845486111
//        Percentage of Successfully processed request: 0.0
//
//        ==========Milestone specific result============
//
//        Milestone: HazardousDetectionApp
//        Total execution delay: 214.8846845486111 ms
//        Average Request execution time: 72.32441906250051 ms
//        Achieved Throughput: 1224.49 /s
//        Required Throughput: 8 /s
//        Is throughput achieved?: true
//
//        ==========Microservice specific result============
//
//        Microservice: Site_Anomaly_Detection_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.029296875ms
//        Max delay faced in sending data: 10.17578125ms
//        Average Execution time: 10.494335937499997 ms
//        Minimum Execution time: 10.080468749999994 ms
//        Maximum Execution time: 10.908203125 ms
//        Required responsiveness within 14.0 ms
//        Meet responsiveness requirement:1500
//        did not meet responsiveness requirement:0
//
//        Microservice: Resource_Event_Analysis_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 20.065104166666664ms
//        Max delay faced in sending data: 20.959635416666664ms
//        Average Execution time: 55.23688151041678 ms
//        Minimum Execution time: 20.065104166666664 ms
//        Maximum Execution time: 90.4086588541669 ms
//        Required responsiveness within 21.0 ms
//        Meet responsiveness requirement:895
//        did not meet responsiveness requirement:605
//
//        Microservice: Monitoring_Work_Order_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 10.065104166666666ms
//        Max delay faced in sending data: 1.3888888888888888ms
//        Average Execution time: 12.338932291666746 ms
//        Minimum Execution time: 10.065104166666666 ms
//        Maximum Execution time: 14.612760416666825 ms
//        Required responsiveness within 14.0 ms
//        Meet responsiveness requirement:1493
//        did not meet responsiveness requirement:7
//
//        Microservice: Action_Plan_Execution_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 150.0390625ms
//        Max delay faced in sending data: 150.000048ms
//        Average Execution time: 150.089065 ms
//        Minimum Execution time: 150.0390625 ms
//        Maximum Execution time: 150.1390675 ms
//        Required responsiveness within 153.0 ms
//        Meet responsiveness requirement:1500
//        did not meet responsiveness requirement:0
//
//        Microservice: Production_Schedule_Update_Microservice_1
//        Number of instances created: 1
//        Max delay faced to receive data: 7.0ms
//        Max delay faced in sending data: 157.078173ms
//        Average Execution time: 162.10004800000002 ms
//        Minimum Execution time: 157.078173 ms
//        Maximum Execution time: 167.12192300000004 ms
//        Required responsiveness within 169.0 ms
//        Meet responsiveness requirement:1500
//        did not meet responsiveness requirement:0
//
//
//        Cost In Cloud: 1.012072580833338E9
//        Network used: 54650.8
//
//        Here, I have uploaded 5 data for each number of requests (500,1000,1500) for single instance microservice. analyse them and suggest which microservice should be scaled up and in which device and why?
