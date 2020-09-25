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

import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.integers.UndoableRandomValueChangeMutation;
import org.cicirello.search.problems.BoundMax;
import org.cicirello.search.representations.IntegerVector;
import org.cicirello.search.sa.ModifiedLam;
import org.cicirello.search.sa.ModifiedLamOriginal;
import org.cicirello.search.sa.SimulatedAnnealing;

/**
 * <p>Driver program for experiment comparing the runtime of 
 * the original Modified Lam annealing schedule to an optimized
 * version of the Modified Lam annealing schedule, on the
 * BoundMax problem.  The BoundMax problem is a generalization of
 * the OneMax problem to non-binary strings, specifically to general 
 * vectors of integers, from the interval [0,B] for some bound B.
 * It was chosen for the experiment to have a simple optimization
 * problem with an integer representation of solutions.</p>
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
public class BoundMaxExperiment {
	
	/**
	 * Runs the experiment.
	 * @param args There are no command line arguments.
	 */
    public static void main(String[] args) {
		final int NUM_SAMPLES = 100;
		final int N = 650;
		final int B = 127;
		final int MIN_RUN_LENGTH = 10000;
		final int MAX_RUN_LENGTH = 1000000;
		
		BoundMax problem = new BoundMax(N, B);
		final double P = 1.0/N;
		final int K = 1;
		
		// Warm up JVM prior to timing alternatives
		// The warm up phase uses the longest run length.
		for (int i = 0; i < NUM_SAMPLES; i++) {
			SimulatedAnnealing<IntegerVector> sa1 = new SimulatedAnnealing<IntegerVector>(
				problem, 
				new UndoableRandomValueChangeMutation<IntegerVector>(0, B, P, K),
				(Initializer<IntegerVector>)problem.split(),
				new ModifiedLamOriginal()
			);
			
			SimulatedAnnealing<IntegerVector> sa2 = new SimulatedAnnealing<IntegerVector>(
				problem, 
				new UndoableRandomValueChangeMutation<IntegerVector>(0, B, P, K), 
				(Initializer<IntegerVector>)problem.split(),
				new ModifiedLam()
			);
			
			sa1.optimize(MAX_RUN_LENGTH);
			sa2.optimize(MAX_RUN_LENGTH);
		}
		// End warm up
		
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		
		System.out.printf("%7s\t%5s\t%5s\t%12s\t%12s\n",
			"length",
			"cost1",
			"cost2",
			"cpu1",
			"cpu2"
		);
		
		for (int runLength = MIN_RUN_LENGTH; runLength <= MAX_RUN_LENGTH; runLength *= 10) {
			
			for (int i = 0; i < NUM_SAMPLES; i++) {
				SimulatedAnnealing<IntegerVector> sa1 = new SimulatedAnnealing<IntegerVector>(
					problem, 
					new UndoableRandomValueChangeMutation<IntegerVector>(0, B, P, K),
					(Initializer<IntegerVector>)problem.split(),
					new ModifiedLamOriginal()
				);
				
				SimulatedAnnealing<IntegerVector> sa2 = new SimulatedAnnealing<IntegerVector>(
					problem, 
					new UndoableRandomValueChangeMutation<IntegerVector>(0, B, P, K), 
					(Initializer<IntegerVector>)problem.split(),
					new ModifiedLam()
				);
				
				long start = bean.getCurrentThreadCpuTime();
				sa1.optimize(runLength);
				long mid = bean.getCurrentThreadCpuTime();
				sa2.optimize(runLength);
				long end = bean.getCurrentThreadCpuTime();
				
				System.out.printf("%7d\t%5d\t%5d\t%12d\t%12d\n",
					runLength,
					sa1.getProgressTracker().getCost(), 
					sa2.getProgressTracker().getCost(),
					mid-start,
					end-mid
				);
			}
		}
    }
}
