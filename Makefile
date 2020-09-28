ifeq ($(OS),Windows_NT)
	py = "python"
else
	py = "python3"
endif

.PHONY: build
build:
	mvn compile

.PHONY: experiments
experiments:
	mvn exec:java -q -Dexec.mainClass=org.cicirello.experiments.modifiedlam.AnnealingScheduleExperiment > anneal.txt
	mvn exec:java -q -Dexec.mainClass=org.cicirello.experiments.modifiedlam.OneMaxExperiment > onemax.txt
	mvn exec:java -q -Dexec.mainClass=org.cicirello.experiments.modifiedlam.BoundMaxExperiment > boundmax.txt
	mvn exec:java -q -Dexec.mainClass=org.cicirello.experiments.modifiedlam.HaystackExperiment > haystack.txt
	mvn exec:java -q -Dexec.mainClass=org.cicirello.experiments.modifiedlam.RootFindingExperiment > roots.txt
	
.PHONY: analysis
analysis:
	$(py) src/analysis/experimentstats.py data/onemax.txt
	$(py) src/analysis/experimentstats.py data/boundmax.txt
	$(py) src/analysis/experimentstats.py data/haystack.txt
	$(py) src/analysis/experimentstats.py data/roots.txt float

.PHONY: clean
clean:
	mvn clean	
	