package edu.utulsa.forbes.evocomp.data;

import java.util.BitSet;

import edu.utulsa.forbes.evocomp.Chromosome;

public class Result implements Comparable<Result> {
	private int generations;
	private String id;
	private Chromosome answer;
	
	public Result(int generations, Chromosome answer, String id) {
		this.generations = generations;
		this.answer = answer;
		this.id = id;
	}
	
	public int getNumberOfGenerations(){
		return generations;
	}
	
	public int getNumberSelected() {
		return answer.getNumberOfSelectedPoints();
	}
	
	public boolean isFeasible() {
		return answer.isFeasible();
	}
	
	public String getId() {
		return id;
	}
	
	public BitSet getChromosome() {
		return answer.chromosome;
	}
	
	public String toString() {
		String feasString = isFeasible() ? "Feasible" : "Not feasible";
		return "Dataset "+answer.dataset.name+" ID "+id+" Gen "+generations+" Fit "+answer.getFitness()+" ("+feasString+", Selected "+answer.getNumberOfSelectedPoints()+")";
	}

	@Override
	public int compareTo(Result other) {
		return this.id.compareTo(other.id);
	}
}
