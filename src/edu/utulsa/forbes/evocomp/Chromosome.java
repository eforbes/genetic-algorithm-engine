package edu.utulsa.forbes.evocomp;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import edu.utulsa.forbes.evocomp.data.Dataset;
import edu.utulsa.forbes.evocomp.data.Graph;
import edu.utulsa.forbes.evocomp.data.Point;

/**
 * This class represents the chromosome for the SGA
 * 
 * @author Evan Forbes
 *
 */
public class Chromosome implements Comparable<Chromosome> {

	public BitSet chromosome;
	
	private float fitness = -1;
	private boolean validFitness = false;
	
	private boolean isConnected;
	private boolean coversAll;
	
	public Dataset dataset;
	public GeneticAlgorithmEngine engine;

	public Chromosome(GeneticAlgorithmEngine engine) {
		this.dataset = engine.dataset;
		this.engine = engine;
		chromosome = new BitSet(dataset.getNumberOfPoints()-1);
	}
	
	/**
	 * Create a new chromosome with the same bit chromosome and the same dataset
	 * @param c
	 */
	public Chromosome(Chromosome c) {
		chromosome = (BitSet) c.chromosome.clone();
		engine = c.engine;
		dataset = c.dataset;
	}
	
	
	/**
	 * returns the fitness for this chromosome if it has already been calculated, otherwise calculates it then returns it.
	 * @return the fitness
	 */
	public float getFitness() {
		if (validFitness) return fitness;

		Graph graph = dataset.getGraph();
		
		//Calculate the number of covered points
		BitSet covered = new BitSet(chromosome.length());
		List<Integer> selectedIndexes = getSelectedIndexes();
		
		ArrayList<Point> selectedPoints = new ArrayList<>();

		for(int i=0;i<selectedIndexes.size();i++) {
			selectedPoints.add(dataset.getPoints().get(selectedIndexes.get(i)));
			List<Integer> connectedToSelected = graph.getConnectedIndexes(selectedIndexes.get(i));

			for(int j=0;j<connectedToSelected.size();j++) {
				covered.set(connectedToSelected.get(j));
			}
		}
		
		int numberOfCoveredPoints = covered.cardinality();
		int numberOfSelectedPoints = getNumberOfSelectedPoints();
		
		coversAll = (numberOfCoveredPoints==dataset.getNumberOfPoints());
		
		Graph subgraph = Dataset.generateGraphFromPoints(selectedPoints, dataset.getMaxDistance()); //The subgraph of selected points
		float connectedness = subgraph.calculateConnectedness();
		
		isConnected=(connectedness==1f);
		
		float factorA = (float) dataset.getNumberOfPoints() / (float) numberOfCoveredPoints;//cover more points
		float factorB = (float) numberOfSelectedPoints / (float) dataset.getNumberOfPoints();//select fewer points
		float factorC = connectedness;//increase connectedness of selected points
		
		fitness = 
				engine.getAlpha() * factorA 
			  + engine.getBeta()  * factorB
			  + engine.getGamma() * factorC;

		validFitness = true;
		return fitness;
	}
	
	/**
	 * 
	 * @return a list of all selected indexes in the chromosome
	 */
	public List<Integer> getSelectedIndexes() {
		List<Integer> selectedIndexes = new ArrayList<>();
		for (int i = chromosome.nextSetBit(0); i >= 0; i = chromosome.nextSetBit(i+1)) {
		     selectedIndexes.add(i);
		 }
		return selectedIndexes;
	}
	
	public String toString() {
		return chromosome.toString();
	}
	
	/**
	 * 
	 * @return a randomly generated chromosome
	 */
	public static Chromosome generateRandomChromosome(GeneticAlgorithmEngine engine) {
		Dataset dataset = engine.dataset;
		int length = dataset.getNumberOfPoints();
		Chromosome newChromosome = new Chromosome(engine);
		//Randomly flip a random number of bits
		int numberOfBitsToFlip = GeneticAlgorithmEngine.random.nextInt(length)+1;
		for(int i=0;i<numberOfBitsToFlip;i++) {
			newChromosome.chromosome.flip(GeneticAlgorithmEngine.random.nextInt(length-1));
		}
		return newChromosome;
	}

	@Override
	public int compareTo(Chromosome other) {
		return (int) Math.signum(getFitness()-other.getFitness());
	}
	
	/**
	 * 
	 * @return true if this chromosome covers all points and all selected points are connected to each other
	 */
	public boolean isFeasible() {
		if(!validFitness) getFitness();
		return isConnected && coversAll;
	}
	
	/**
	 * 
	 * @return the number of selected points
	 */
	public int getNumberOfSelectedPoints() {
		return chromosome.cardinality();
	}
	
	public void invalidateFitness() {
		validFitness = false;
		fitness = -1;
	}
}
