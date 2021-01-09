# modified-lam-experiments

Copyright &copy; 2020 Vincent A. Cicirello

This repository contains code to reproduce the experiments, and analysis of 
experimental data, from the following paper:

> Vincent A. Cicirello. 2020. Optimizing the Modified Lam Annealing Schedule. *Industrial
Networks and Intelligent Systems*, 7(25), Article e1 (December 2020). https:
//doi.org/10.4108/eai.16-12-2020.167653.

## Requirements to Build and Run the Experiments

To build and run the experiments on your own machine, you will need the following:
* __JDK 11__: I used OpenJDK 11, but you should be fine with Oracle's JDK as well. Technically, there is nothing in the code that strictly requires Java 11, so you should be able to build and run with JDK 8 or later. However, the Maven pom.xml provided in the repository assumes Java 11. Also, if you want to recreate the experiments in as similar an environment as used in the reported results, then you should use Java 11.
* __Apache Maven__: In the root of the repository, there is a pom.xml for building the Java programs for the experiments. Using this pom.xml, Maven will take care of downloading the exact version of the [Chips-n-Salsa](https://chips-n-salsa.cicirello.org/) library that was used in the experiments (release 2.2.0), as well as Chips-n-Salsa's dependencies.
* __Python 3__: The repository contains a Python program that was used to compute summary statistics, as well as statistical significance tests. If you want to run this Python program, you will need Python 3. I specifically used Python 3.8.2.  You also need scipy installed to run this Pythyon program.
* __Make__: The repository also contains a Makefile to simplify running the build, running the experiment's Java programs, and running the Python program to analyze the data. If you are familiar with using the Maven build tool, and running Python programs, then you can just run these directly, although the Makefile may be useful to see the specific commands needed.

## Building the Experiment's Java Programs

The source code of the Java programs, implementing the experiments
is in the [src/main](src/main) directory.  You can build the experiment 
programs in one of the following ways:
* Execute `mvn compile` at the root of the repository.
* Execute `make` or `make build` at the root of the repository (which simply executes a `mvn compile`). 

This produces 5 Java programs for running different parts of the experiments. If you are 
unfamiliar with the usual structure of the directories of a Java project built with Maven,
the `.class` files, etc will be found in a `target` directory that is created by the build process.

## Running the Experiments

Once you have successfully executed the build above, you can run the 
experiments by executing `make experiments` at the root of the 
repository. This will run each of the experiment programs in sequence, 
with the results piped to text files in the root of the project. The 
output from my runs are found in the [/data](data) directory.

The experiments produce 5 text files with data:
* `anneal.txt`: This has data comparing the two versions of the annealing schedule independent from Simulated Annealing.
* `onemax.txt`: This has results from a comparison on the OneMax problem.
* `boundmax.txt`: This has results from a comparison on the BoundMax problem.
* `haystack.txt`: This has results from a comparison on the Permutation in a Haystack problem.
* `roots.txt`: This has results from a comparison on a Polynomial Root Finding problem.

You can find these 5 files from my runs of the experiments in the [/data](data) directory.

## Analyzing the Experimental Data

To run the Python program that I used to generate summary statistics 
and run significance tests, you need Python 3 installed. The source code
of this Python program is found in the [src/analysis](src/analysis) directory,
and consists of a single Python file `experimentstats.py`.  To run that
Python program, execute `make analysis` in the root of the project. This
assumes that you have run the experiments above, and that the output
of the experiments is in the root of the project. If you want to simply
run it against the result of my runs, then change the variable
`pathToDataFiles = "data/"` in the `Makefile`.  Executing the `Makefile` in this
way will also take care of installing `scipy` if you don't already have it installed,
and will pass the appropriate command line parameters to the Python program
for each of the data files.  

The output of this analysis program is piped to a file `stats.txt` in the
root of the project. This file from my run of the experiments is found
in the [/data](data) directory.

## Other Files in the Repository

There are a few other files, potentially of interest, in the repository,
which include:
* `system-stats.txt`: This file contains details of the system I used to run the experiments, such as operating system, processor specs, Java JDK and VM.  It is in the [/data](data) directory.
* `graphs.xlsx`: An Excel spreadsheet used to generate the graphs of the paper.  It is also in the [/data](data) directory.
* [/data/figures](data/figures) directory: This is probably of less interest to someone other than me. It contains the figures of the paper in eps format, along with some intermediate formats.


