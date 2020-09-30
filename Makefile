ifeq ($(OS),Windows_NT)
	py = "python"
else
	py = "python3"
endif

pathToDataFiles = "data/"

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
	$(py) -m pip install --user scipy
	$(py) src/analysis/experimentstats.py ${pathToDataFiles}onemax.txt > stats.txt
	$(py) src/analysis/experimentstats.py ${pathToDataFiles}boundmax.txt >> stats.txt
	$(py) src/analysis/experimentstats.py ${pathToDataFiles}haystack.txt >> stats.txt
	$(py) src/analysis/experimentstats.py ${pathToDataFiles}roots.txt float >> stats.txt
	$(py) src/analysis/experimentstats.py ${pathToDataFiles}anneal.txt anneal >> stats.txt

.PHONY: clean
clean:
	mvn clean	
	