package edu.utulsa.forbes.evocomp.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.utulsa.forbes.evocomp.Chromosome;
import edu.utulsa.forbes.evocomp.GeneticAlgorithmEngine;

/**
 * This class contains the various selection methods for the SGA:
 * Roulette, rank, and tournament
 * 
 * @author Evan Forbes
 *
 */
public class Selection {

	public static double TOURNAMENT_PARAMETER = 0.75; // parameter used in the tournament selection method
	
	public enum Type {
		ROULETTE, RANK, TOURNAMENT;
	}
	
	public static String[] getTypes() {
		Selection.Type[] selections = Selection.Type.values();
		String[] selectionNames = new String[selections.length];
		for(int i=0;i<selections.length;i++) {
			selectionNames[i] = selections[i].name();
		}
		return selectionNames;
	}
	
	/**
	 * Uses the given type of bias selection on the given population
	 * @param population population to select from
	 * @param type selection method to use
	 * @return the parent pool
	 */
	public static List<Chromosome> select(List<Chromosome> population, Type type) {
		switch(type) {
		case ROULETTE: return roulette(population);
		case RANK: return rank(population);
		case TOURNAMENT: return tournament(population);
		default: return null;
		}
	}
	
	/**
	 * Performs a roulette selection on the given population
	 * @param population the population to select from
	 * @return the parent pool
	 */
	private static List<Chromosome> roulette(List<Chromosome> population) {

		List<Chromosome> selectedChromosomes = new ArrayList<>();
		
		float fitnessSum = 0;
		for(int i=0;i<population.size();i++) {
			fitnessSum+=population.get(i).getFitness();
		}
		float reversedFitnessSum = 0;
		float[] reversedFitnesses = new float[population.size()];
		for(int i=0;i<population.size();i++) {
			reversedFitnesses[i] = (fitnessSum/population.get(i).getFitness());
			reversedFitnessSum += reversedFitnesses[i];
		}
		
		float[] percentOnWheel = new float[population.size()];
		for(int i=0;i<population.size();i++) {
			percentOnWheel[i] = reversedFitnesses[i]/reversedFitnessSum;
		}
		
		
		for(int i=0;i<population.size()-2;i++) { //-2 b/c eliteism
			float random = GeneticAlgorithmEngine.random.nextFloat();
			float currentPercent = 0;
			int currentIndex = 0;
			while(true) {
				currentPercent+=percentOnWheel[currentIndex];
				if (currentPercent>=random||currentIndex==(population.size()-1)) {
					//Select
					selectedChromosomes.add(population.get(currentIndex));
					break;
				}
				currentIndex++;
			}
		}
		
		return selectedChromosomes;
	}
	
	/**
	 * Performs a rank selection on the given population
	 * @param population the population to select from
	 * @return the parent pool
	 */
	private static List<Chromosome> rank(List<Chromosome> population) {
		List<Chromosome> selectedChromosomes = new ArrayList<>();
		Collections.sort(population);
		
		int popSize = population.size();
		float[] percentOnWheel = new float[popSize];
		int rankSum = (popSize*(popSize+1)) / 2;
		
		for (int i=0;i<popSize;i++) {
			percentOnWheel[i] = (float) (popSize-i) / (float) rankSum;
		}
		
		for(int i=0;i<popSize-2;i++) { //-2 b/c eliteism
			float random = GeneticAlgorithmEngine.random.nextFloat();
			float currentPercent = 0;
			int currentIndex = 0;
			while(true) {
				currentPercent+=percentOnWheel[currentIndex];
				if (currentPercent>=random||currentIndex==(popSize-1)) {
					//Select
					selectedChromosomes.add(population.get(currentIndex));
					break;
				}
				currentIndex++;
			}
		}
		
		return selectedChromosomes;
	}
	
	/**
	 * Performs a tournament selection on the given population
	 * @param population the population to select from
	 * @return the parent pool
	 */
	private static List<Chromosome> tournament(List<Chromosome> population) {
		List<Chromosome> selectedChromosomes = new ArrayList<>();
		for(int i =0;i<population.size()-2;i++) {
			Chromosome c1 = population.get(GeneticAlgorithmEngine.random.nextInt(population.size()));
			Chromosome c2 = population.get(GeneticAlgorithmEngine.random.nextInt(population.size()));
			double r = GeneticAlgorithmEngine.random.nextDouble();
			
			if(r<TOURNAMENT_PARAMETER) {
				//better one wins
				if(c1.getFitness()<c2.getFitness()) {
					selectedChromosomes.add(c1);
				} else {
					selectedChromosomes.add(c2);
				}
			} else {
				//worse one wins
				if(c1.getFitness()<c2.getFitness()) {
					selectedChromosomes.add(c2);
				} else {
					selectedChromosomes.add(c1);
				}
			}			
		}

		
		return selectedChromosomes;
	}
	
}
