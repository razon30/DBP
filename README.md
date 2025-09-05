# From Cloud to Edge: Dynamic Placement Optimization of Business Processes in IIoT Networks

## Authors
Md Razon Hossain, Alistair Barros, Colin Fidge

## Overview
This repository contains the implementation code for our JNCA paper, *From Cloud to Edge: Dynamic Placement Optimization of Business Processes in IIoT Networks*. It provides a dynamic business-process (DBP) placement procedure with runtime scaling for Industrial IoT workloads, evaluated in a scaffold-safety use case. The approach integrates multi-objective placement with activity-wise scaling to meet **activity responsiveness** and **milestone throughput** targets in resource-constrained fog environments.

## Paper Details
**Title:** From cloud to edge: dynamic placement optimization of business processes in IIoT networks  
**Journal:** Journal of Network and Computer Applications (JNCA)  
**Year:** 2025  
**DOI:** 10.1016/j.jnca.2025.104317  
**Keywords:** IIoT, Business Process, Edge/Fog Computing, Microservices, Timeliness

## Abstract
Business processes for industrial applications gain from edge-side execution. Placement is complicated by device heterogeneity, control-flow structures, and fluctuating event rates. We model processes in coherent fragments, formulate a multi-objective placement problem for responsiveness and throughput, and pair it with runtime scaling for multi-instance activities. Validation in an industrial safety scenario shows consistent improvements in timeliness and throughput compared to conventional methods.

## Code Overview
- **Main entry point:** `dynamicOptimisation.DynamicOptimisation`  
- **Key packages and classes:**
  - NSGA-II placement: `dynamicOptimisation.NSGAII.*`
  - Optimized cloud–fog placement: `CloudFogMicroservicePlacementOptimise.java`
  - Non-optimized baselines: `CloudFogMicroservicePlacementNonOptimise*.java`
  - Cloud-only baseline: `CloudOnlyMicroservicePlacementLogic.java`
  - Runtime scaling and milestone control: `dynamicOptimisation.components.*`
  - Experiment configuration: `dynamicOptimisation.utils.SimulationParameters.java`

> This artifact does **not** include external datasets. Experiments rely on configurable parameters and built-in generators or constants. Adjust everything in `SimulationParameters.java`.

## Requirements
- Java JDK 11+  
- Maven 3.8+ (or any IDE with Maven support)  
- Windows, macOS, or Linux


## Configuration
Tune experiment knobs in:

`iFogSim/src/dynamicOptimisation/utils/SimulationParameters.java`  
Typical parameters: number of simulations, event frequencies, scaling thresholds, NSGA-II settings (population size, generations), and time limits.

## Reproducing Paper Scenarios
**Optimized DBP placement with runtime scaling:** run `DynamicOptimisation` with your chosen `SimulationParameters`.

**Baselines:** switch to the baseline placement classes (cloud-only or non-optimized cloud–fog) through the simulation driver or configuration flags to mirror paper comparisons.

## Outputs
Console logs summarize device placements, responsiveness, throughput, and scaling actions.

Optional spreadsheet logging may be enabled in components such as `MilestoneController.java`.

## Portability note
If any file paths are hard-coded for logs, change them to relative paths (for example, `./Results.xlsx`) before sharing.

## Funding and Competing Interests
Alistair Barros and Colin Fidge report that financial support is partly provided through the Australian Research Council Discovery Project **DP220101516**, *Embedding Enterprise Systems in IoT Fog Networks Through Microservices*. All other authors declare that they have no known competing financial interests or personal relationships that could have appeared to influence the work reported in this paper.

## License and Acknowledgments
Please include a LICENSE compatible with any bundled upstream components if you redistribute sources or JARs.

## Citation
If you use this code, please cite:

```bibtex
@article{Hossain2025DBP,
  title   = {From Cloud to Edge: Dynamic Placement Optimization of Business Processes in IIoT Networks},
  author  = {Md Razon Hossain and Alistair Barros and Colin Fidge},
  journal = {Journal of Network and Computer Applications},
  year    = {2025},
  doi     = {10.1016/j.jnca.2025.104317}
}

