package edu.utulsa.forbes.evocomp.operators;

import java.util.BitSet;
import java.util.List;

import edu.utulsa.forbes.evocomp.Chromosome;
import edu.utulsa.forbes.evocomp.GeneticAlgorithmEngine;

/**
 * This class contains the various mutation methods used in the SGA:
 * 
 * 
 * @author Evan Forbes
 *
 */
public class Mutation {

	public enum Type {
		SINGLE_INVERT, DOUBLE_INVERT, HYPERMUTATE;
	}
	
	public static String[] getTypes() {
		Mutation.Type[] mutations = Mutation.Type.values();
		String[] selectionNames = new String[mutations.length];
		for(int i=0;i<mutations.length;i++) {
			selectionNames[i] = mutations[i].name();
		}
		return selectionNames;
	}
	
	/**
	 * Mutates the given chromosome using the specified method
	 * @param chromosome child to mutate
	 * @param type mutation method
	 */
	public static void mutate(Chromosome chromosome, Type type) {
		switch(type) {
		case SINGLE_INVERT: singleInvert(chromosome);
		case DOUBLE_INVERT: doubleInvert(chromosome);
		case HYPERMUTATE: hypermutate(chromosome);
		}
	}

	private static void singleInvert(Chromosome chromosome) {
		chromosome.chromosome.flip(GeneticAlgorithmEngine.random.nextInt(chromosome.dataset.getNumberOfPoints()));
	}
	
	private static void doubleInvert(Chromosome chromosome) {
		singleInvert(chromosome);
		singleInvert(chromosome);
	}
	
	/**
	 * Performs an N4N hypermutation
	 */
	private static void hypermutate(Chromosome chromosome) {
		int index = GeneticAlgorithmEngine.random.nextInt(chromosome.dataset.getNumberOfPoints());
		List<Integer> adjacencyList = chromosome.dataset.getNearestN(index);
		
		float bestFitness = Float.MAX_VALUE;
		Chromosome bestChromosome = null;
		
		int numberOfCombinations = (int) Math.pow(2, adjacencyList.size());
		//System.out.println("hypermutating: "+numberOfCombinations);
		
		
		for(int i=0;i<numberOfCombinations;i++) {
			Chromosome tempChromosome = new Chromosome(chromosome);
			String binary = Integer.toBinaryString(i);

			while(binary.length()<adjacencyList.size()) {
				binary = "0"+binary;
			}
			
			for(int j=0;j<binary.length();j++) {
				if(binary.charAt(j)=='1') {
					tempChromosome.chromosome.flip(adjacencyList.get(j));
				}
			}
			
			float tempFitness = tempChromosome.getFitness();
			if(tempFitness<bestFitness) {
				bestFitness = tempFitness;
				bestChromosome = tempChromosome;
			}
			
		}
		chromosome.chromosome = (BitSet) bestChromosome.chromosome.clone();
		chromosome.invalidateFitness();
	}

}
