package edu.utulsa.forbes.evocomp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.utulsa.forbes.evocomp.data.Dataset;
import edu.utulsa.forbes.evocomp.data.Result;
import edu.utulsa.forbes.evocomp.logging.Log;
import edu.utulsa.forbes.evocomp.operators.Crossover;
import edu.utulsa.forbes.evocomp.operators.Mutation;
import edu.utulsa.forbes.evocomp.operators.Selection;
import edu.utulsa.forbes.evocomp.visual.DataViewerApplication;
import edu.utulsa.forbes.evocomp.visual.GAFrame;

/**
 * Engine for a simple genetic algorithm to solve the geometric connected dominating set problem
 * 
 * CS4623 Evolutionary Computation
 * Dr. Wainwright
 * Spring 2014
 * 
 * @author Evan Forbes
 * @version 0
 *
 */
public class GeneticAlgorithmEngine {
	
	public static final int DEFAULT_VISUALIZATION_UPDATE_PERIOD = 50;
	public static final boolean DEFAULT_LOGGING_ENABLED = true;
	
	private int populationSize;
	private float mutationRate;
	private float crossoverRate;
	protected Mutation.Type mutationType;
	private Crossover.Type crossoverType;
	private Selection.Type selectionType;
	private int maxGenerations;
	protected long maxMillis;
	private int stagnationTimeout;
	private float alpha;
	private float beta;
	private float gamma;
	
	protected int visualizationUpdatePeriod;
	private boolean loggingEnabled;
	
	protected GAFrame visualizationFrame;
	
	public Dataset dataset;
	
	private List<Chromosome> population;
	private int generation = 0;
	protected long startTimeMillis;
	
	private float historyFitness = Float.MAX_VALUE;
	private int historyGeneration;
	
	public boolean forceStop = false; //if this is set to true, the GA will end after the current generation
	
	Log log;
	public static Random random = new Random();
	
	protected DataViewerApplication viewApp = null;
	protected int viewIndex;
	
	String id; //a string identification used to identify this run of the GA
	
	/**
	 * Creates an engine with the default settings
	 */
	protected GeneticAlgorithmEngine(Dataset dataset, String id) {
		this.dataset = dataset;
		this.id = id;
		
		this.populationSize = (int) Parameter.POPULATION_SIZE.getDefaultValue();
		this.mutationRate = (float) Parameter.MUTATION_RATE.getDefaultValue();
		this.crossoverRate = (float) Parameter.CROSSOVER_RATE.getDefaultValue();
		this.selectionType = (Selection.Type) Parameter.SELECTION_TYPE.getDefaultValue();
		this.crossoverType = (Crossover.Type) Parameter.CROSSOVER_TYPE.getDefaultValue();
		this.mutationType = (Mutation.Type) Parameter.MUTATION_TYPE.getDefaultValue();
		this.maxGenerations = (int) Parameter.MAX_GENERATIONS.getDefaultValue();
		this.maxMillis = (int) Parameter.MAX_TIME.getDefaultValue();
		this.stagnationTimeout = (int) Parameter.STAGNATION_PARAMETER.getDefaultValue();
		this.alpha = (float) Parameter.ALPHA.getDefaultValue();
		this.beta = (float) Parameter.BETA.getDefaultValue();
		this.gamma = (float) Parameter.GAMMA.getDefaultValue();
		
		this.visualizationUpdatePeriod = DEFAULT_VISUALIZATION_UPDATE_PERIOD;
		this.loggingEnabled = DEFAULT_LOGGING_ENABLED;
		
		//TODO alpha, beta gamma
	}
	
	/**
	 * 
	 * @return a new engine with the default settings
	 */
	public static GeneticAlgorithmEngine create(String datasetName, String id) {
		Dataset dataset = Dataset.loadDatasetFromFile(datasetName);
		return new GeneticAlgorithmEngine(dataset, id);
	}
	
	public Result start() {
		log = new Log(Log.Level.INFO, true, loggingEnabled); //set up logger
		log.i("Engine created");
		log.i("GA started");
		startTimeMillis = System.currentTimeMillis();

		population = generateRandomPopulation(); //randomly create the initial population
		
		if(visualizationUpdatePeriod>0 && viewApp==null) {
			visualizationFrame = new GAFrame(dataset, null, dataset.name+" starting...");
		}
		
		try{
			while(!isDone()) {
				runGeneration();
			}
			
			return finish();	
			
		} catch(Exception e) {
			//In case anything goes wrong, log the error
			log.e("Exited with an error: "+e.getMessage());
			log.close();
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Run a single generation of the SGA including selection, crossover, mutation, and elitism
	 */
	private void runGeneration() {
		if(generation%100==0) {
			long elapsedTime = (System.currentTimeMillis()-startTimeMillis);
			float avgTimePerGeneration = ((float)elapsedTime/(float)generation);
			log.i("Generation # "+generation+" Time elapsed: "+elapsedTime+"ms Avg time/gen: "+avgTimePerGeneration+"ms Convergence: "+getConvergence());
		}
		
		//Elitism: save the best two chromosomes from the current generation to ensure they make it to the next
		List<Chromosome> elite = getEliteChromosomes();
		
		//Keep track of when the elite fitness value changes
		float currentBestFitness = elite.get(0).getFitness();
		if(currentBestFitness<historyFitness) {
			//elite fitness value has improved
			historyFitness = currentBestFitness;
			historyGeneration = generation;
		}
		
		//Update the live visualization (if enabled)
		if(visualizationUpdatePeriod>0 && generation%visualizationUpdatePeriod == 0) {
			String feasString = elite.get(0).isFeasible() ? "Feasible": "Not feasible";
			String infoString = "Dataset "+dataset.name+" ID "+id+" Gen "+generation+" Fit "+elite.get(0).getFitness()+" ("+feasString+", Selected "+elite.get(0).getNumberOfSelectedPoints()+") Running...";
			if(viewApp==null) visualizationFrame.updateVisualization(elite.get(0).chromosome, infoString);
			else viewApp.imageUpdate(viewIndex, elite.get(0).chromosome, infoString);
		}
		
		//Selection
		List<Chromosome> parentPool = Selection.select(population, selectionType);
		
		//Crossover
		List<Chromosome> childrenPool = performCrossover(parentPool);
		
		//Mutation
		List<Chromosome> mutatedChildrenPool = performMutation(childrenPool);
		
		//Add the elite chromosomes back in
		mutatedChildrenPool.addAll(elite); //Put elite chromosomes directly into the mutated children pool
		
		generation++;
		population = mutatedChildrenPool; //replace old population completely with the new one (generational)
	}
	
	/**
	 * Perform the crossover operation on the given parent pool, using the set crossoverRate
	 * @param parentPool
	 * @return childrenPool
	 */
	private List<Chromosome> performCrossover(List<Chromosome> parentPool) {
		List<Chromosome> childrenPool = new ArrayList<>();
		int upperBound = (populationSize-2)/2;
		for(int i=0;i<upperBound;i++) {
			int chromosomeIndexA = random.nextInt(parentPool.size());
			int chromosomeIndexB = random.nextInt(parentPool.size());
			
			Chromosome chromosomeA = parentPool.get(chromosomeIndexA);
			Chromosome chromosomeB = parentPool.get(chromosomeIndexB);
			
			List<Chromosome> chromosomes = new ArrayList<>();
			chromosomes.add(chromosomeA);
			chromosomes.add(chromosomeB);
			
			if(random.nextFloat()<crossoverRate) {
				//Crossover
				List<Chromosome> offspring = Crossover.crossover(chromosomes, crossoverType);
				childrenPool.addAll(offspring);
			} else {
				//No crossover, just put them in the children pool
				childrenPool.addAll(chromosomes);
			}
		}
		return childrenPool;
	}
	
	/**
	 * Perform the mutation operation on the given childrenPool, using the set mutationRate
	 * @param childrenPool
	 * @return mutatedChildrenPool
	 */
	private List<Chromosome> performMutation(List<Chromosome> childrenPool) {
		for(int i=0;i<childrenPool.size();i++) {
			if(random.nextFloat()<mutationRate) {
				//mutate
				Mutation.mutate(childrenPool.get(i), mutationType);
			} else {
				//no mutation
			}
		}
		return childrenPool;
	}
	
	/**
	 * Checks to see if the generation or time limit has been reached
	 * @return true if at least one of the stop conditions is met
	 */
	protected boolean isDone() {
		if(forceStop) return true;
		
		if(generation>maxGenerations) {
			log.i("Reached max generations: "+maxGenerations);
			return true;
		}
		if((System.currentTimeMillis()-startTimeMillis)>maxMillis) {
			log.i("Reached max time: "+maxMillis+"ms");
			return true;
		}
		if(historyGeneration> 0 && (generation-historyGeneration)>stagnationTimeout) {
			log.i("Population has stagnated after "+stagnationTimeout+" generations");
			return true;
		}
		return false;
	}
	
	/**
	 * Calculates a relative measure of how many unique chromosomes there are in the population.
	 * A value of 1 = every chromosome is unique
	 * Value approaches 0 as each chromosome is the same
	 * @return
	 */
	private float getConvergence() {
		Set<BitSet> populationSet = new HashSet<>();
		for(int i=0;i<populationSize;i++) {
			populationSet.add(population.get(i).chromosome);
		}
		return (float)populationSet.size()/(float)populationSize;
	}
	
	/**
	 * Finds the best 2 chromosomes from the population
	 * @return a list containing the best two chromosomes
	 */
	private List<Chromosome> getEliteChromosomes() {
		float bestA = Float.MAX_VALUE;
		float bestB = Float.MAX_VALUE;
		
		Chromosome chromosomeA = null;
		Chromosome chromosomeB = null;
		
		for(int i=0;i<populationSize;i++) {
			Chromosome current = population.get(i);
			float currentFitness = current.getFitness();
			if(currentFitness<bestA) {
				bestB = bestA;
				bestA = currentFitness;
				chromosomeB = chromosomeA;
				chromosomeA = current;
			} else if(currentFitness<bestB) {
				bestB = currentFitness;
				chromosomeB = current;
			}
		}
		
		return Arrays.asList(chromosomeA, chromosomeB);
	}
	
	public float getAlpha() {
		return alpha;
	}
	
	public float getBeta() {
		return beta;
	}
	
	public float getGamma() {
		return gamma;
	}
	
	/**
	 * Finishes running the GA
	 * @return the Result
	 */
	private Result finish() {
		Chromosome answer =  getEliteChromosomes().get(0);
		//System.out.println("[*] Fitness "+answer.getFitness());
		BitSet answerBitSet = getEliteChromosomes().get(0).chromosome;
		
		String feasString = answer.isFeasible() ? "Feasible": "Not feasible";
		String infoString = "Dataset "+dataset.name+" ID "+id+" Gen "+generation+" Fit "+answer.getFitness()+" ("+feasString+", Selected "+answer.getNumberOfSelectedPoints()+")";
		if(visualizationUpdatePeriod>0&&viewApp==null) {
			visualizationFrame.updateVisualization(answer.chromosome, infoString);
		} else if(viewApp!=null&& visualizationUpdatePeriod>0) {
			 viewApp.imageUpdate(viewIndex, answer.chromosome, infoString);
		}
		
		log.i("Answer: "+answerBitSet);
		log.d("End population: "+population);
		log.i("GA finished after "+(System.currentTimeMillis()-startTimeMillis)+"ms and "+(generation)+ " generations");
		log.close();
		return new Result(generation, answer, id);
	}
	
	/**
	 * Generates a random population
	 * @return a list of random chromosomes
	 */
	private List<Chromosome> generateRandomPopulation() {
		List<Chromosome> randomPopulation = new ArrayList<>();
		for(int i=0;i<populationSize;i++) {
			randomPopulation.add(Chromosome.generateRandomChromosome(this));
		}
		return randomPopulation;
	}
	
	
	
	//////////////////// Setters ////////////////////////
	
	/**
	 * set the population size
	 * @param populationSize Must be an event integer
	 */
	public GeneticAlgorithmEngine setPopulationSize(int populationSize) {
		if(populationSize%2!=0) throw new IllegalArgumentException("Population size must be an even integer");
		this.populationSize = populationSize;
		return this;
	}
	
	public GeneticAlgorithmEngine setMutationRate(float mutationRate) {
		this.mutationRate = mutationRate;
		return this;
	}
	
	public GeneticAlgorithmEngine setCrossoverRate(float crossoverRate) {
		this.crossoverRate = crossoverRate;
		return this;
	}
	
	public GeneticAlgorithmEngine setSelectionType(Selection.Type selectionType) {
		this.selectionType = selectionType;
		return this;
	}
	
	public GeneticAlgorithmEngine setCrossoverType(Crossover.Type crossoverType) {
		this.crossoverType = crossoverType;
		return this;
	}
	
	public GeneticAlgorithmEngine setMutationType(Mutation.Type mutationType) {
		this.mutationType = mutationType;
		return this;
	}
	
	public GeneticAlgorithmEngine setMaxGenerations(int maxGenerations) {
		this.maxGenerations = maxGenerations;
		return this;
	}
	
	public GeneticAlgorithmEngine setMaxMillis(long maxMillis) {
		this.maxMillis = maxMillis;
		return this;
	}
	
	public GeneticAlgorithmEngine setStagnationTimeout(int timeout) {
		this.stagnationTimeout = timeout;
		return this;
	}
	
	public GeneticAlgorithmEngine setLoggingEnabled(boolean enabled) {
		this.loggingEnabled = enabled;
		return this;
	}
	
	public GeneticAlgorithmEngine setViewApplication(DataViewerApplication viewApp, int viewIndex) {
		this.viewApp = viewApp;
		this.viewIndex = viewIndex;
		return this;
	}
	
	/**
	 * Set the visualization update period. if period==0, live visualization disabled
	 * @param period # of generations to update visualization
	 */
	public GeneticAlgorithmEngine setVisualizationUpdatePeriod(int period) {
		this.visualizationUpdatePeriod = period;
		return this;
	}
	
	public GeneticAlgorithmEngine setAlpha(float alpha) {
		this.alpha = alpha;
		return this;
	}
	
	public GeneticAlgorithmEngine setBeta(float beta) {
		this.beta = beta;
		return this;
	}
	
	public GeneticAlgorithmEngine setGamma(float gamma) {
		this.gamma = gamma;
		return this;
	}
	
	@SuppressWarnings("rawtypes")
	public enum Parameter {
		POPULATION_SIZE(Integer.class, 100),
		CROSSOVER_RATE(Float.class, 1f),
		MUTATION_RATE(Float.class, .01f),
		SELECTION_TYPE(Selection.Type.class, Selection.Type.ROULETTE),
		CROSSOVER_TYPE(Crossover.Type.class, Crossover.Type.UNIFORM),
		MUTATION_TYPE(Mutation.Type.class, Mutation.Type.SINGLE_INVERT),
		MAX_GENERATIONS(Integer.class, 10000),
		MAX_TIME(Long.class, 600000),
		STAGNATION_PARAMETER(Integer.class, 2000),
		ALPHA(Float.class, 1f),
		BETA(Float.class, 1f),
		GAMMA(Float.class, 1f);
	
		private Class type;
		private Object defaultValue;
		
		private Parameter(Class type, Object defaultValue) {
			this.type = type;
			this.defaultValue = defaultValue;
		}
		
		public Class getType() {
			return type;
		}
		
		public Object getDefaultValue() {
			return defaultValue;
		}
		
		public static String[] getParameterNames() {
			Parameter[] parameters = Parameter.values();
			String[] parameterNames = new String[parameters.length];
			for(int i=0;i<parameters.length;i++) {
				parameterNames[i] = parameters[i].name();
			}
			return parameterNames;
		}
		
		public static Class[] getParameterTypes() {
			Parameter[] parameters = Parameter.values();
			Class[] parameterTypes = new Class[parameters.length];
			for(int i=0;i<parameters.length;i++) {
				parameterTypes[i] = parameters[i].getType();
			}
			return parameterTypes;
		}
		
		public static Object[] getDefaults() {
			Parameter[] parameters = Parameter.values();
			Object[] parameterTypes = new Object[parameters.length];
			for(int i=0;i<parameters.length;i++) {
				parameterTypes[i] = parameters[i].getDefaultValue();
				if(i==3) {
					parameterTypes[i] = ((Selection.Type)parameters[i].getDefaultValue()).name();
				}
				if(i==4) {
					parameterTypes[i] = ((Crossover.Type)parameters[i].getDefaultValue()).name();
				}
				if(i==5) {
					parameterTypes[i] = ((Mutation.Type)parameters[i].getDefaultValue()).name();
				}
			}
			return parameterTypes;
		}

	}
	

}
