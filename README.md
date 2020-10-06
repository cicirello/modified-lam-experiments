# modified-lam-experiments

Copyright &copy; 2020 Vincent A. Cicirello

This repository contains code to reproduce the experiments, and analysis of 
experimental data, from the following paper (currently under review):

> Cicirello, V. A., (2020). Optimizing the Modified Lam Annealing Schedule.

## Requirements to Build and Run the Experiments

To build and run the experiments on your own machine, you will need the following:
* __JDK 11__: I used OpenJDK 11, but you should be fine with Oracle's JDK as well. Technically, there is nothing in the code that strictly requires Java 11, so you should be able to build and run with JDK 8 or later. However, the Maven pom.xml provided in the repository assumes Java 11. Also, if you want to recreate the experiments in as similar an environment as used in the reported results, then you should use Java 11.
* __Apache Maven__: In the root of the repository, there is a pom.xml for building the Java programs for the experiments. Using this pom.xml, Maven will take care of downloading the exact version of the [Chips-n-Salsa](https://chips-n-salsa.cicirello.org/) library that was used in the experiments (release 2.2.0), as well as Chips-n-Salsa's dependencies.
* __Python 3__: The repository contains a Python program that was used to compute summary statistics, as well as statistical significance tests. If you want to run this Python program, you will need Python 3. I specifically used Python 3.8.2.
* __Make__: The repository also contains a Makefile to simplify running the build, running the experiment's Java programs, and running the Python program to analyze the data. If you are familiar with using the Maven build tool, and running Python programs, then you can just run these directly, although the Makefile may be useful to see the specific commands needed.


