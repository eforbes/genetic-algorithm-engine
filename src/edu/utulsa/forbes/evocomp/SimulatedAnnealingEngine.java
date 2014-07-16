package edu.utulsa.forbes.evocomp;

import edu.utulsa.forbes.evocomp.data.Dataset;
import edu.utulsa.forbes.evocomp.data.Result;
import edu.utulsa.forbes.evocomp.operators.Mutation;

public class SimulatedAnnealingEngine extends GeneticAlgorithmEngine{

	public static final float T0 = 10;
	public static final int I0 = 1000;
	public static final float ALPHA = .95f;
	public static final float BETA = 1.05f;
	
	public static final long MAX_TIME = 600000;
	
	Chromosome best;
	Chromosome s; //solution
	
	float t; //temperature
	int iterations; 
	
	public SimulatedAnnealingEngine(Dataset d, String id) {
		super(d, id);
		
		s = Chromosome.generateRandomChromosome(this);
		best = s;
		
		t= T0;
		iterations = I0;
	}
	
	public Result start() {
		startTimeMillis = System.currentTimeMillis();
		do {
			for(int i=0;i<iterations;i++) {
				if(super.forceStop) return null;
				Chromosome newS = new Chromosome(s);
				Mutation.mutate(newS, super.mutationType);
				float rand = -1f + (float) Math.pow(Math.E, ((newS.getFitness()/3f)-(s.getFitness()/3f))/t);
				boolean better = newS.getFitness()<s.getFitness();
				boolean takeAnyway = Math.random()<rand;
				if(better || takeAnyway) {
					s = newS;
					
					String infoString = "SA Running: Dataset "+dataset.name+" id "+id+" Sel "+s.getNumberOfSelectedPoints()+" Feasible "+s.isFeasible()+" best so far: "+best.getNumberOfSelectedPoints();
					if(viewApp==null) visualizationFrame.updateVisualization(s.chromosome, infoString);
					else viewApp.imageUpdate(viewIndex, s.chromosome, infoString);
					
					//keep track of elite
					if(s.getFitness()<=best.getFitness()) {
						best = s;
					}
				}
			}
			t *=ALPHA;
			iterations*=BETA;
		} while(!isDone());
		String infoString = "SA finished: Sel "+best.getNumberOfSelectedPoints()+" Feasible "+s.isFeasible();
		if(viewApp==null) visualizationFrame.updateVisualization(best.chromosome, infoString);
		else viewApp.imageUpdate(viewIndex, best.chromosome, infoString);
		System.out.println("DONE: Selected "+ best.getNumberOfSelectedPoints());
		return new Result(0, best, super.id);
	}
	
	protected boolean isDone() {
		if(System.currentTimeMillis()>(startTimeMillis+super.maxMillis)) {
			return true;
		}
		return false;
	}
	
}
