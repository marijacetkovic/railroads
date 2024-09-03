# Railroad Network Optimization Using Genetic Algorithms

## Overview

This project demonstrates the application of a Genetic Algorithm (GA) to solve a railroad network optimization problem. The goal is to design a train network on a 2D grid that allows trains to travel from their starting points to their destinations as efficiently as possible. The network is composed of various tile types, including straight junctions, turn junctions, and crossroads (with their tile costs), which can be configured to optimize the paths. It was developed as part of the *Programming III - Concurrent Programming* course 23/24 at Faculty of Mathematics, Natural Sciences and Information Technologies, Koper, Slovenia.

## Project Description

The project implements a Genetic Algorithm to evolve solutions for the railroad network problem. The GA simulates the process of natural selection to find an optimal or near-optimal solution. The key components of the GA include:

1. **Population Initialization**: A set of possible solutions (individuals) is generated. Each individual represents a different configuration of the railroad network.

2. **Fitness Evaluation**: The effectiveness of each individual is assessed using a fitness function that prioritizes connectivity and penalizes the solution for the cost of the tiles used in the network.

3. **Genetic Operators**:
   - **Selection**: Individuals are selected based on their fitness scores. This project primarily uses **Roulette Wheel Selection** to maintain a balance between selecting the fittest individuals and maintaining diversity in the population.
   - **Crossover**: A single random vertical point crossover is applied to combine the genetic material of two parent solutions to create offspring.
   - **Mutation**: Randomly alters parts of an individual to introduce diversity.

4. **Convergence Improvements**:
   - **Improving Initial Population**: Crossroad tile distribution are used to enhance the quality of the initial population, aiding in faster convergence.
   - **Dynamic Mutation Rate**: Adjusts the mutation rate during execution to avoid the algorithm getting stuck in local optima.

## Implementation Modes

The project offers three different implementation modes to execute the Genetic Algorithm:

1. **Sequential Mode**: The algorithm runs in a single thread, processing each task one after the other.

2. **Parallel Mode**: Tasks are distributed across multiple threads, significantly speeding up the execution of the algorithm.

3. **Distributed Mode**: Uses the Message Passing Interface (MPI) to distribute tasks across multiple processes, allowing the algorithm to run on a distributed system.

## User Interaction

A graphical user interface (GUI) is implemented to visualize the railroad network and the progress of the Genetic Algorithm. The GUI can be optionally displayed during the algorithm's execution, providing feedback on the optimization process.

## Testing
For detailed testing results please refer to the accompanying PDF paper available in the repository.
