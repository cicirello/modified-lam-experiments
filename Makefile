ifeq ($(OS),Windows_NT)
	py = "python"
else
	py = "python3"
endif

pathToDataFiles = ""

.PHONY: build
build:
	mvn clean compile

.PHONY: download
download:
	mvn -f pom-get-from-central.xml clean dependency:copy-dependencies

.PHONY: experiments
experiments: POMFILE = pom.xml
experiments: anneal.txt onemax.txt boundmax.txt haystack.txt roots.txt
	echo Experiments Complete

.PHONY: experimentsCentral
experimentsCentral: POMFILE = pom-get-from-central.xml
experimentsCentral: anneal.txt onemax.txt boundmax.txt haystack.txt roots.txt
	echo Experiments Complete

.PHONY: analysis
analysis:
	$(py) -m pip install --user scipy
	$(py) src/analysis/experimentstats.py ${pathToDataFiles}onemax.txt > stats.txt
	$(py) src/analysis/experimentstats.py ${pathToDataFiles}boundmax.txt >> stats.txt
	$(py) src/analysis/experimentstats.py ${pathToDataFiles}haystack.txt >> stats.txt
	$(py) src/analysis/experimentstats.py ${pathToDataFiles}roots.txt float >> stats.txt
	$(py) src/analysis/experimentstats.py ${pathToDataFiles}anneal.txt anneal >> stats.txt

anneal.txt:
	mvn -f ${POMFILE} exec:java -q -Dexec.mainClass=org.cicirello.experiments.modifiedlam.AnnealingScheduleExperiment > anneal.txt
	
onemax.txt:
	mvn -f ${POMFILE} exec:java -q -Dexec.mainClass=org.cicirello.experiments.modifiedlam.OneMaxExperiment > onemax.txt

boundmax.txt:
	mvn -f ${POMFILE} exec:java -q -Dexec.mainClass=org.cicirello.experiments.modifiedlam.BoundMaxExperiment > boundmax.txt

haystack.txt:
	mvn -f ${POMFILE} exec:java -q -Dexec.mainClass=org.cicirello.experiments.modifiedlam.HaystackExperiment > haystack.txt

roots.txt:
	mvn -f ${POMFILE} exec:java -q -Dexec.mainClass=org.cicirello.experiments.modifiedlam.RootFindingExperiment > roots.txt

.PHONY: clean
clean:
	mvn clean	
	