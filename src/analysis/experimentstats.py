# Experiments with the original version, and optimized version, 
# of the Modified Lam annealing schedule.
# Copyright (C) 2020  Vincent A. Cicirello
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

import math
import statistics
import sys
import scipy.stats

class data :
    """Summarizes the data associated with one run length."""
    
    __slots__ = ['cost1', 'cost2', 'cpu1', 'cpu2']

    def __init__(self, c1, c2, t1, t2) :
        """Initializes a data object with the first data point.

        Keyword arguments:
        c1 - Optimization cost value of final solution for algorithm 1.
        c2 - Optimization cost value of final solution for algorithm 2.
        t1 - CPU time in nanoseconds for algorithm 1.
        t2 - CPU time in nanoseconds for algorithm 2.
        """
        self.cost1 = [ c1 ]
        self.cost2 = [ c2 ]
        self.cpu1 = [ t1 / 1000000000 ]
        self.cpu2 = [ t2 / 1000000000 ]

    def addDataPoint(self, c1, c2, t1, t2) :
        """Adds another data point.

        Keyword arguments:
        c1 - Optimization cost value of final solution for algorithm 1.
        c2 - Optimization cost value of final solution for algorithm 2.
        t1 - CPU time in nanoseconds for algorithm 1.
        t2 - CPU time in nanoseconds for algorithm 2.
        """
        self.cost1.append(c1)
        self.cost2.append(c2)
        self.cpu1.append(t1 / 1000000000)
        self.cpu2.append(t2 / 1000000000)

class dataTimeOnly :
    """Summarizes the data associated with one run length."""
    
    __slots__ = ['cpu1', 'cpu2']

    def __init__(self, t1, t2) :
        """Initializes a data object with the first data point.

        Keyword arguments:
        t1 - CPU time in nanoseconds for algorithm 1.
        t2 - CPU time in nanoseconds for algorithm 2.
        """
        self.cpu1 = [ t1 / 1000000000 ]
        self.cpu2 = [ t2 / 1000000000 ]

    def addDataPoint(self, t1, t2) :
        """Adds another data point.

        Keyword arguments:
        t1 - CPU time in nanoseconds for algorithm 1.
        t2 - CPU time in nanoseconds for algorithm 2.
        """
        self.cpu1.append(t1 / 1000000000)
        self.cpu2.append(t2 / 1000000000)

if __name__ == "__main__" :
    datafile = sys.argv[1]
    floatValued = len(sys.argv) > 2 and sys.argv[2] == "float"
    annealOnly = len(sys.argv) > 2 and sys.argv[2] == "anneal"
        
    lengthMap = {}
    with open(datafile, 'r') as f :
        f.readline()
        for line in f :
            if line.startswith("Experiment") :
                break
            values = line.split()
            if annealOnly :
                key = (int(values[0]), int(values[1]))
                i = 2
            else :
                key = int(values[0])

            if annealOnly :
                if key in lengthMap :
                    lengthMap[key].addDataPoint(int(values[2]), int(values[3]))
                else :
                    lengthMap[key] = dataTimeOnly(int(values[2]), int(values[3]))
            elif floatValued :
                if key in lengthMap :
                    lengthMap[key].addDataPoint(float(values[1]), float(values[2]), int(values[3]), int(values[4]))
                else :
                    lengthMap[key] = data(float(values[1]), float(values[2]), int(values[3]), int(values[4]))
            else :
                if key in lengthMap :
                    lengthMap[key].addDataPoint(int(values[1]), int(values[2]), int(values[3]), int(values[4]))
                else :
                    lengthMap[key] = data(int(values[1]), int(values[2]), int(values[3]), int(values[4]))

    print("Statistical Analysis:", datafile)
    if annealOnly :
        print("{0:7s}\t{1:4s}\t{2}\t{3}\t{4}\t{5}\t{6}\t{7}\t{8}".format(
            "L",
            "R",
            "muT1",
            "muT2",
            "devT1",
            "devT2",
            "N",
            "t-cpu",
            "P-cpu"))
        outputTemplate = "{0:7d}\t{1:4d}\t{2:.5f}\t{3:.5f}\t{4:.5f}\t{5:.5f}\t{6:3d}\t{7:.2f}\t{8:.2g}"
        for key in sorted(lengthMap.keys()) :
            muCpu1 = statistics.mean(lengthMap[key].cpu1)
            muCpu2 = statistics.mean(lengthMap[key].cpu2)
            stdevCpu1 = statistics.stdev(lengthMap[key].cpu1)
            stdevCpu2 = statistics.stdev(lengthMap[key].cpu2)
            n = len(lengthMap[key].cpu1)
            cpuTTest = scipy.stats.ttest_ind_from_stats(
                mean1=muCpu1,
                std1=stdevCpu1,
                nobs1=n,
                mean2=muCpu2,
                std2=stdevCpu2,
                nobs2=n,
                equal_var=False)
            print(outputTemplate.format(
                key[0],
                key[1],
                muCpu1,
                muCpu2,
                stdevCpu1,
                stdevCpu2,
                n,
                cpuTTest.statistic,
                cpuTTest.pvalue))
    else :
        print("{0:9s}\t{1}\t{2}\t{3}\t{4}\t{5}\t{6}\t{7}\t{8}\t{9}\t{10}\t{11}\t{12}\t{13}".format(
            "L",
            "muC1",
            "muC2",
            "muT1",
            "muT2",
            "devC1",
            "devC2",
            "devT1",
            "devT2",
            "N",
            "t-cost",
            "P-cost",
            "t-cpu",
            "P-cpu"))
        if floatValued :
            outputTemplate = "{0:9d}\t{1:.5f}\t{2:.5f}\t{3:.3f}\t{4:.3f}\t{5:.5f}\t{6:.5f}\t{7:.5f}\t{8:.5f}\t{9:3d}\t{10:.2f}\t{11:.2f}\t{12:.2f}\t{13:.2g}"
        else :
            outputTemplate = "{0:9d}\t{1:.2f}\t{2:.2f}\t{3:.3f}\t{4:.3f}\t{5:.4f}\t{6:.4f}\t{7:.5f}\t{8:.5f}\t{9:3d}\t{10:.2f}\t{11:.2f}\t{12:.2f}\t{13:.2g}"
        for key in sorted(lengthMap.keys()) :
            muCost1 = statistics.mean(lengthMap[key].cost1)
            muCost2 = statistics.mean(lengthMap[key].cost2)
            muCpu1 = statistics.mean(lengthMap[key].cpu1)
            muCpu2 = statistics.mean(lengthMap[key].cpu2)
            stdevCost1 = statistics.stdev(lengthMap[key].cost1)
            stdevCost2 = statistics.stdev(lengthMap[key].cost2)
            stdevCpu1 = statistics.stdev(lengthMap[key].cpu1)
            stdevCpu2 = statistics.stdev(lengthMap[key].cpu2)
            n = len(lengthMap[key].cost2)
            costTTest = scipy.stats.ttest_ind_from_stats(
                mean1=muCost1,
                std1=stdevCost1,
                nobs1=n,
                mean2=muCost2,
                std2=stdevCost2,
                nobs2=n,
                equal_var=False)
            cpuTTest = scipy.stats.ttest_ind_from_stats(
                mean1=muCpu1,
                std1=stdevCpu1,
                nobs1=n,
                mean2=muCpu2,
                std2=stdevCpu2,
                nobs2=n,
                equal_var=False)
            print(outputTemplate.format(
                key,
                muCost1,
                muCost2,
                muCpu1,
                muCpu2,
                stdevCost1,
                stdevCost2,
                stdevCpu1,
                stdevCpu2,
                n,
                costTTest.statistic,
                costTTest.pvalue,
                cpuTTest.statistic,
                cpuTTest.pvalue))
    print()
