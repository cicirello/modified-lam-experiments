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

import org.cicirello.search.sa.ModifiedLam;
import org.cicirello.search.sa.ModifiedLamOriginal;

/**
 * <p>Driver program for experiment comparing the runtime of 
 * the original Modified Lam annealing schedule to an optimized
 * version of the Modified Lam annealing schedule, where the
 * comparisons are done independently from simulated annealing.
 * That is, in the experiment, the annealing schedule object is
 * initialized for a run length, and then the acceptance decision
 * and annealing schedule update is called repeatedly the number of
 * times corresponding to the run length for which it was 
 * initialized.</p>
 *
 * <p>The output is formatted in columns as follows:<br>
 * L  R  cpu1  cpu2<br>
 * where L is the run length, R is the number of restarts,
 * cpu1 is the cpu time for the original modified Lam schedule,
 * and cpu2 is the cpu time for our optimized version.
 * The cpu times are in nanoseconds.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class AnnealingScheduleExperiment {
	
	/**
	 * Initializes the original modified Lam schedule, and then repeatedly
	 * calls the method that determines move acceptance and updates the schedule.
	 * To demonstrate added benefit of caching initialization constants for the optimized
	 * version, also simulates a restarted search.
	 *
	 * @param runLength The length of one run.
	 * @param numRestarts The number of runs.
	 *
	 * @return The double returned is the average number of moves accepted during a run. 
	 * The only
	 * actual purpose is to produce a result to avoid the JVM from optimizing
	 * away the work we are timing.
	 */
	public static double runOriginal(int runLength, int numRestarts) {
		ModifiedLamOriginal lam = new ModifiedLamOriginal();
		long count = 0;
		for (int r = 0; r < numRestarts; r++) {
			lam.init(runLength);
			double currentCost = 1000 * r;
			for (int i = 0; i < runLength; i++) {
				double neighborCost = currentCost;
				if (i % 2 == 0) neighborCost = neighborCost + i % 1000;
				else neighborCost = neighborCost - i % 1000;
				if (lam.accept(neighborCost, currentCost)) count = count + 1;
			}
		}
		return 1.0 * count / numRestarts;
	}
	
	/**
	 * Initializes the optimized modified Lam schedule, and then repeatedly
	 * calls the method that determines move acceptance and updates the schedule.
	 * To demonstrate added benefit of caching initialization constants for the optimized
	 * version, also simulates a restarted search.
	 *
	 * @param runLength The length of one run.
	 * @param numRestarts The number of runs.
	 *
	 * @return The double returned is the average number of moves accepted during a run. 
	 * The only
	 * actual purpose is to produce a result to avoid the JVM from optimizing
	 * away the work we are timing.
	 */
	public static double runOptimized(int runLength, int numRestarts) {
		ModifiedLam lam = new ModifiedLam();
		long count = 0;
		for (int r = 0; r < numRestarts; r++) {
			lam.init(runLength);
			double currentCost = 1000 * r;
			for (int i = 0; i < runLength; i++) {
				double neighborCost = currentCost;
				if (i % 2 == 0) neighborCost = neighborCost + i % 1000;
				else neighborCost = neighborCost - i % 1000;
				if (lam.accept(neighborCost, currentCost)) count = count + 1;
			}
		}
		return 1.0 * count / numRestarts;
	}
	
	/**
	 * Runs the experiment.
	 * @param args There are no command line arguments.
	 */
    public static void main(String[] args) {
		final int WARMUP_NUM_SAMPLES = 10;
		final int NUM_SAMPLES = 100;
		final int MIN_RUNLENGTH = 2000;
		final int MAX_RUNLENGTH = 1024000;
		final int MIN_RESTARTS = 1;
		final int MAX_EVALUATIONS = 16384000;
		
		// Warm up JVM prior to timing alternatives
		// The warm up phase uses the longest run length.
		double totalDiff = 0;
		{
			final int MAX_RESTARTS = MAX_EVALUATIONS / MAX_RUNLENGTH;
			for (int i = 0; i < WARMUP_NUM_SAMPLES; i++) {
				double x = runOriginal(MAX_RUNLENGTH, MAX_RESTARTS);
				double y = runOptimized(MAX_RUNLENGTH, MAX_RESTARTS);
				// Do something with return values to avoid JVM from optimizing away the calls.
				totalDiff += (x-y);		
			}
		}
		// End warm up
		
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		
		System.out.printf("%7s\t%4s\t%12s\t%12s\n",
			"L",
			"R",
			"cpu1",
			"cpu2"
		);
		
		
		for (int L = MIN_RUNLENGTH; L <= MAX_RUNLENGTH; L *= 8) {
			final int MAX_RESTARTS = MAX_EVALUATIONS / L;
			for (int R = MIN_RESTARTS; R <= MAX_RESTARTS; R *= 2) {
				for (int i = 0; i < NUM_SAMPLES; i++) {
					long start = bean.getCurrentThreadCpuTime();
					double x = runOriginal(L, R);
					long mid = bean.getCurrentThreadCpuTime();
					double y = runOptimized(L, R);
					long end = bean.getCurrentThreadCpuTime();
					// Do something with return values to avoid JVM from optimizing away the calls.
					totalDiff += (x-y);
					
					System.out.printf("%7d\t%4d\t%12d\t%12d\n",
						L,
						R,
						mid-start,
						end-mid
					);
				}
			}
		}
		System.out.println("Experiment finished: " + totalDiff);
	}
}