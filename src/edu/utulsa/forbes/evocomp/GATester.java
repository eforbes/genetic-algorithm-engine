package edu.utulsa.forbes.evocomp;

import edu.utulsa.forbes.evocomp.operators.Crossover;
import edu.utulsa.forbes.evocomp.operators.Mutation;
import edu.utulsa.forbes.evocomp.operators.Selection;

public class GATester {

	public static void main(String[] args) {
//		Dataset.generateRandomDataset(100, "test100c");	
		
//		new ContrivedDatasetCreator();
//		
//		ArrayList<Chromosome> testChromosomes = new ArrayList<>();
//
//		Dataset d = Dataset.loadDatasetFromFile("contrived1");
//		Chromosome p1 = Chromosome.generateRandomChromosome(d);
//		testChromosomes.add(p1);
//		Chromosome p2 = Chromosome.generateRandomChromosome(d);
//		testChromosomes.add(p2);
//
//		int[] select = {0, 3, 29, 35};
//		Chromosome p3 = new Chromosome(d);
//		BitSet ch = new BitSet();
//		for(int e:select) {
//			ch.set(e);
//		}
//		p3.chromosome = ch;
//		testChromosomes.add(p3);
//
//		Chromosome p4 = new Chromosome(d);
//		ch = new BitSet();
//		ch.set(0,5);
//		p4.chromosome = ch;	
//		testChromosomes.add(p4);
//	
//		for(int i=0;i<testChromosomes.size();i++) {
//			new GAFrame(d, testChromosomes.get(i).chromosome, "Dataset contrived1; Example Solution "+i+"; Fitness: "+testChromosomes.get(i).getFitness());
//			System.out.println(i+": "+testChromosomes.get(i)+" "+testChromosomes.get(i).getFitness());			
//		}

		//while(true) {
		GeneticAlgorithmEngine.create("test25", "test25")
		.setPopulationSize(100)
		.setSelectionType(Selection.Type.ROULETTE)
		.setCrossoverType(Crossover.Type.N_POINT)
		.setMutationType(Mutation.Type.DOUBLE_INVERT)
		.setMaxGenerations(10000)
		.setMutationRate(0.05f)
		.setStagnationTimeout(2000)
		.setVisualizationUpdatePeriod(1)
		.setLoggingEnabled(true)
		.start();
//		}
		//new GAFrame(d.points, d.maxDistance);
		
//		Chromosome p1 = Chromosome.generateRandomChromosome(d);
//		Chromosome p2 = Chromosome.generateRandomChromosome(d);
//		List<Chromosome> parents = new ArrayList<>();
//		parents.add(p1);
//		parents.add(p2);
//		Crossover.crossover(parents, Crossover.Type.N_POINT);

	}
	
}
