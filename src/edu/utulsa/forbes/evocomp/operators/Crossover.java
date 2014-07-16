package edu.utulsa.forbes.evocomp.operators;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import edu.utulsa.forbes.evocomp.Chromosome;
import edu.utulsa.forbes.evocomp.GeneticAlgorithmEngine;

/**
 * This class contains the various crossover methods used in the SGA:
 * Single point, double point, n-point, uniform
 * 
 * @author Evan Forbes
 *
 */
public class Crossover {

	public enum Type {
		SINGLE_POINT, DOUBLE_POINT, N_POINT, UNIFORM;
	}
	
	public static String[] getTypes() {
		Crossover.Type[] crossovers = Crossover.Type.values();
		String[] selectionNames = new String[crossovers.length];
		for(int i=0;i<crossovers.length;i++) {
			selectionNames[i] = crossovers[i].name();
		}
		return selectionNames;
	}
	
	/**
	 * performs a crossover of the given type on the given parents
	 * @param parents the parents
	 * @param type
	 * @return the children pool
	 */
	public static List<Chromosome> crossover(List<Chromosome> parents, Type type) {
		switch(type) {
		case SINGLE_POINT: return nPointCrossover(parents, 1);
		case DOUBLE_POINT: return nPointCrossover(parents, 2);
		case N_POINT: return nPointCrossover(parents);
		case UNIFORM: return uniform(parents);
		default: return null;
		}
	}
	
	/**
	 * Performs a uniform crossover with a randomly generated bit mask
	 * @param parents
	 * @return children
	 */
	private static List<Chromosome> uniform(List<Chromosome> parents) {
		int length = parents.get(0).dataset.getNumberOfPoints();
		BitSet parent1 = parents.get(0).chromosome;
		BitSet parent2 = parents.get(1).chromosome;

		BitSet mask1 = new BitSet(length);
		for (int i = 0; i < length; i++) {
			mask1.set(i, GeneticAlgorithmEngine.random.nextBoolean());
		}

		BitSet mask2 = (BitSet) mask1.clone();
		mask2.flip(0, mask2.size());
		
		BitSet child1A = (BitSet) parent1.clone();
		child1A.and(mask1);
		
		BitSet child1B = (BitSet) parent2.clone();
		child1B.and(mask2);
		
		child1A.or(child1B);
		
		BitSet child2A = (BitSet) parent1.clone();
		child2A.and(mask2);
		
		BitSet child2B = (BitSet) parent2.clone();
		child2B.and(mask1);
		
		child2A.or(child2B);		
		

		Chromosome child1 = new Chromosome(parents.get(0).engine);
		child1.chromosome = child1A;

		Chromosome child2 = new Chromosome(parents.get(0).engine);
		child2.chromosome = child2A;
		
		return Arrays.asList(child1, child2);
	}
	
	/**
	 * perform an n-point crossover where n is a random value between 1..(n-1)/2
	 * @param parents
	 * @return children
	 */
	private static List<Chromosome> nPointCrossover(List<Chromosome> parents) {
		int n = GeneticAlgorithmEngine.random.nextInt((parents.get(0).dataset.getNumberOfPoints()-1)/2)+1;
		return nPointCrossover(parents, n);
	}
	
	/**
	 * Performs an n-point crossover
	 * @param parents
	 * @param n number of crossover points (must be >=1)
	 * @return children
	 */
	private static List<Chromosome> nPointCrossover(List<Chromosome> parents, int n) {
		int length = parents.get(0).dataset.getNumberOfPoints();
		BitSet parent1 = parents.get(0).chromosome;
		BitSet parent2 = parents.get(1).chromosome;
		
		boolean adjust = n%2==0;
		if(adjust) {
			n++;
		}
		int[] crossoverIndexes = new int[n];
		for(int i=0;i<n;i++) {
			crossoverIndexes[i] = GeneticAlgorithmEngine.random.nextInt(length);
		}
		if(adjust) {
			crossoverIndexes[n-1] = length;
		}
		Arrays.sort(crossoverIndexes);
		
		BitSet mask1 = new BitSet(length);
		int last = 0;
		for(int i=0;i<n;i+=2) {
			mask1.set(last, crossoverIndexes[i]);
			if(i+1<n) {
				last = crossoverIndexes[i+1];
			}
		}

		BitSet mask2 = (BitSet) mask1.clone();
		mask2.flip(0, mask2.size());
		
		BitSet child1A = (BitSet) parent1.clone();
		child1A.and(mask1);
		
		BitSet child1B = (BitSet) parent2.clone();
		child1B.and(mask2);
		
		child1A.or(child1B);
		
		BitSet child2A = (BitSet) parent1.clone();
		child2A.and(mask2);
		
		BitSet child2B = (BitSet) parent2.clone();
		child2B.and(mask1);
		
		child2A.or(child2B);		
		

		Chromosome child1 = new Chromosome(parents.get(0).engine);
		child1.chromosome = child1A;

		Chromosome child2 = new Chromosome(parents.get(0).engine);
		child2.chromosome = child2A;
		
		return Arrays.asList(child1, child2);
	}
}
