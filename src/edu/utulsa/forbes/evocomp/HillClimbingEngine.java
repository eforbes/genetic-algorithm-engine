package edu.utulsa.forbes.evocomp;

import edu.utulsa.forbes.evocomp.data.Dataset;
import edu.utulsa.forbes.evocomp.data.Result;
import edu.utulsa.forbes.evocomp.operators.Mutation;

public class HillClimbingEngine extends GeneticAlgorithmEngine{
	
	public static final long MAX_TIME = 600000;

	Chromosome s; //solution
	
	public HillClimbingEngine(Dataset d, String id) {
		super(d, id);
		
		s = Chromosome.generateRandomChromosome(this);
	}
	
	public Result start() {
		startTimeMillis = System.currentTimeMillis();
		do {
			if(super.forceStop) return null;
			Chromosome newS = new Chromosome(s);
			Mutation.mutate(newS, super.mutationType);
			if(newS.getFitness()<s.getFitness()) {
				s = newS;
				String infoString = "FHC Running: Dataset "+dataset.name+" id "+id+" Sel "+s.getNumberOfSelectedPoints()+" Feasible "+s.isFeasible();
				if(viewApp==null) visualizationFrame.updateVisualization(s.chromosome, infoString);
				else viewApp.imageUpdate(viewIndex, s.chromosome, infoString);
			}
		} while(!isDone());
		String infoString = "FHC finished: Sel "+s.getNumberOfSelectedPoints()+" Feasible "+s.isFeasible();
		if(viewApp==null) visualizationFrame.updateVisualization(s.chromosome, infoString);
		else viewApp.imageUpdate(viewIndex, s.chromosome, infoString);
		//visual.updateVisualization(best.chromosome, "SA finished: Sel "+best.getNumberOfSelectedPoints());
		System.out.println("FHC DONE: Selected "+ s.getNumberOfSelectedPoints());
		return new Result(-1,s,id);
	}
	
	protected boolean isDone() {
		if(System.currentTimeMillis()>(startTimeMillis+super.maxMillis)) {
			return true;
		}
		return false;
	}
	
}
