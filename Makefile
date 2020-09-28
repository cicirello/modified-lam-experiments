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

.PHONY: clean
clean:
	mvn clean	
	