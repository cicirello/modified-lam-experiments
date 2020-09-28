
import math
import statistics
import sys

def tTestWelch(mu1, mu2, stdev1, stdev2, n1, n2) :
    """Computes Welch's t-test, and returns (t, v) where t is
    the t statistic, and v is the degrees of freedom.

    Keyword arguments:
    mu1 - The mean of dataset 1.
    mu2 - The mean of dataset 2.
    stdev1 - The standard deviation of dataset 1.
    stdev2 - The standard deviation of dataset 2.
    n1 - The number of samples in dataset 1.
    n2 - The number of samples in dataset 2.
    """
    s1 = stdev1 * stdev1 / n1
    s2 = stdev2 * stdev2 / n2
    x = s1 + s2
    t = (mu1 - mu2) / math.sqrt(x)
    v = x * x / ( s1*s1/(n1-1) + s2*s2/(n2-1))
    return t, int(v)

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


if __name__ == "__main__" :
    datafile = sys.argv[1]
    floatValued = False
    if len(sys.argv) > 2 :
        floatValued = sys.argv[2] == "float"
    lengthMap = {}
    with open(datafile, 'r') as f :
        f.readline()
        for line in f :
            values = line.split()
            key = int(values[0])
            if floatValued :
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
        "t-cost", "dof",
        "t-cpu", "dof"))
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
        costT, costDOF = tTestWelch(muCost1, muCost2, stdevCost1, stdevCost2, n, n)
        cpuT, cpuDOF = tTestWelch(muCpu1, muCpu2, stdevCpu1, stdevCpu2, n, n)
        if floatValued :
            print("{0:9d}\t{1:.5f}\t{2:.5f}\t{3:.3f}\t{4:.3f}\t{5:.5f}\t{6:.5f}\t{7:.5f}\t{8:.5f}\t{9:3d}\t{10:.2f}\t{11}\t{12:.2f}\t{13}".format(
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
                costT, costDOF,
                cpuT, cpuDOF))
        else :
            print("{0:9d}\t{1:.2f}\t{2:.2f}\t{3:.3f}\t{4:.3f}\t{5:.4f}\t{6:.4f}\t{7:.5f}\t{8:.5f}\t{9:3d}\t{10:.2f}\t{11}\t{12:.2f}\t{13}".format(
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
                costT, costDOF,
                cpuT, cpuDOF))
    print()
