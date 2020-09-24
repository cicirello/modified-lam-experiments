package org.cicirello.experiments.modifiedlam;

import org.cicirello.search.operators.bits.BitVectorInitializer;
import org.cicirello.search.operators.bits.DefiniteBitFlipMutation;
import org.cicirello.search.problems.OneMax;
import org.cicirello.search.representations.BitVector;
import org.cicirello.search.sa.ModifiedLam;
import org.cicirello.search.sa.ModifiedLamOriginal;
import org.cicirello.search.sa.SimulatedAnnealing;

/**
 * 
 *
 */
public class OneMaxExperiment {
	
    public static void main( String[] args ) {
		
		final int N = 128;
		for (int runLength = 1000; runLength <= 1000000; runLength *= 10) {
			OneMax problem = new OneMax();
			
			SimulatedAnnealing<BitVector> sa1 = new SimulatedAnnealing<BitVector>(
				problem, 
				new DefiniteBitFlipMutation(5),
				new BitVectorInitializer(N),
				new ModifiedLamOriginal()
			);
			
			SimulatedAnnealing<BitVector> sa2 = new SimulatedAnnealing<BitVector>(
				problem, 
				new DefiniteBitFlipMutation(5), 
				new BitVectorInitializer(N),
				new ModifiedLam()
			);
			
			sa1.optimize(runLength);
			sa2.optimize(runLength);
			System.out.printf("%d\t%d\t%d\n",
				runLength,
				sa1.getProgressTracker().getCost(), 
				sa2.getProgressTracker().getCost()
			);
		}
    }
}
