/*
 * Experiments with the original version, and optimized version, 
 * of the Modified Lam annealing schedule.
 * Copyright (C) 2020  Vincent A. Cicirello
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.cicirello.experiments.modifiedlam;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.cicirello.search.operators.reals.RealValueInitializer;
import org.cicirello.search.operators.reals.UndoableGaussianMutation;
import org.cicirello.search.problems.PolynomialRootFinding;
import org.cicirello.search.representations.SingleReal;
import org.cicirello.search.sa.ModifiedLam;
import org.cicirello.search.sa.ModifiedLamOriginal;
import org.cicirello.search.sa.SimulatedAnnealing;

/**
 * <p>Driver program for experiment comparing the runtime of 
 * the original Modified Lam annealing schedule to an optimized
 * version of the Modified Lam annealing schedule, on a
 * Polynomial Root Finding problem.  This problem was chosen 
 * because it is a straightforward real-valued function optimization
 * problem.</p>
 *
 * <p>Output of the program is a table consisting of the
 * following columns:<br>
 * length  cost1  cost2  cpu1  cpu2<br>
 * where the length is the number of simulated annealing evaluations,
 * the cost1 is the best of run value of the cost function for
 * the original Modified Lam (and cost2 for the optimized version),
 * and cpu1 is the amount of cpu time (in nanoseconds) for the original
 * Modified Lam schedule (cpu2 is the same but for the optimized version).</p>
 *
 * <p>The cost function values are included in the output to confirm that
 * there is no effect on the cost function between the two versions, since
 * the sequences of temperatures, and target acceptance rates should be equivalent
 * between the two versions.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class RootFindingExperiment {
	
	/**
	 * Runs the experiment.
	 * @param args There are no command line arguments.
	 */
    public static void main(String[] args) {
		final int WARMUP_NUM_SAMPLES = 10;
		final int NUM_SAMPLES = 100;
		final int MIN_RUN_LENGTH = 100000;
		final int MAX_RUN_LENGTH = 100000000;
		
		// Construct an instance of the Polynomial Root Finding problem:
		//     12500 - 2500 X - 5 pow(X, 2) + pow(X, 3).
		// It has 3 roots: 50, -50, and 5.
		double[] coefficients = { 12500, -2500, -5, 1 };
		PolynomialRootFinding problem = new PolynomialRootFinding(coefficients);
		
		RealValueInitializer initializer = new RealValueInitializer(-100.0, 100.0);
		UndoableGaussianMutation<SingleReal> mutation = UndoableGaussianMutation.createGaussianMutation(0.1);
		
		// Warm up JVM prior to timing alternatives
		// The warm up phase uses the longest run length.
		for (int i = 0; i < WARMUP_NUM_SAMPLES; i++) {
			SimulatedAnnealing<SingleReal> sa1 = new SimulatedAnnealing<SingleReal>(
				problem, 
				mutation.split(),
				initializer.split(),
				new ModifiedLamOriginal()
			);
			
			SimulatedAnnealing<SingleReal> sa2 = new SimulatedAnnealing<SingleReal>(
				problem, 
				mutation.split(), 
				initializer.split(),
				new ModifiedLam()
			);
			
			sa1.optimize(MAX_RUN_LENGTH);
			sa2.optimize(MAX_RUN_LENGTH);
		}
		// End warm up
		
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		
		System.out.printf("%9s\t%10s\t%10s\t%12s\t%12s\n",
			"length",
			"cost1",
			"cost2",
			"cpu1",
			"cpu2"
		);
		
		for (int runLength = MIN_RUN_LENGTH; runLength <= MAX_RUN_LENGTH; runLength *= 10) {
			
			for (int i = 0; i < NUM_SAMPLES; i++) {
				SimulatedAnnealing<SingleReal> sa1 = new SimulatedAnnealing<SingleReal>(
					problem, 
					mutation.split(),
					initializer.split(),
					new ModifiedLamOriginal()
				);
				
				SimulatedAnnealing<SingleReal> sa2 = new SimulatedAnnealing<SingleReal>(
					problem, 
					mutation.split(), 
					initializer.split(),
					new ModifiedLam()
				);

				long start = bean.getCurrentThreadCpuTime();
				sa1.optimize(runLength);
				long mid = bean.getCurrentThreadCpuTime();
				sa2.optimize(runLength);
				long end = bean.getCurrentThreadCpuTime();
				
				System.out.printf("%9d\t%10.8f\t%10.8f\t%12d\t%12d\n",
					runLength,
					sa1.getProgressTracker().getCostDouble(), 
					sa2.getProgressTracker().getCostDouble(),
					mid-start,
					end-mid
				);
			}
		}
	}
}
