package edu.utulsa.forbes.evocomp.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.utulsa.forbes.evocomp.GeneticAlgorithmEngine;
import edu.utulsa.forbes.evocomp.HillClimbingEngine;
import edu.utulsa.forbes.evocomp.SimulatedAnnealingEngine;
import edu.utulsa.forbes.evocomp.operators.Crossover;
import edu.utulsa.forbes.evocomp.operators.Mutation;
import edu.utulsa.forbes.evocomp.operators.Selection;
import edu.utulsa.forbes.evocomp.visual.ChartGenerator;
import edu.utulsa.forbes.evocomp.visual.DataViewerApplication;

/**
 * A multi-threaded genetic algorithm data collector. Runs all combinations of SELECTIONS, CROSSOVERS, and MUTATIONS
 * @author Evan Forbes
 *
 */
public class DataCollector {

	private List<DataCollectorListener> listeners = new ArrayList<>();
	
	List<Result> results;
	int numberOfThreads;
	int numberOfTrials;
	int visualizationUpdatePeriod;
	public String datasetName;
	
	public int numberOfRuns;
	
	volatile int numberOfCompletedRuns = 0;
	volatile long sumMillis = 0;
	volatile int runningThreadCount = 0;
	Thread[] runningThreads;
	GARunnable[] runnables;
	
	public DataViewerApplication viewApp;
	
	List<GeneticAlgorithmEngine> runQueue;
	
	List<List<Object>> parameters;
	
	public boolean forceStop = false;
	
	public DataCollector(int numberOfThreads, int numberOfTrials, int visualizationUpdatePeriod, String datasetName, List<List<Object>> parameters, boolean combinationsMode, int type) {
		this.numberOfThreads = numberOfThreads;
		this.numberOfTrials = numberOfTrials;
		this.visualizationUpdatePeriod = visualizationUpdatePeriod;
		this.datasetName = datasetName;
		this.parameters = parameters;
		
		results = Collections.synchronizedList(new ArrayList<Result>());

		runQueue = Collections.synchronizedList(new ArrayList<GeneticAlgorithmEngine>());
		
		switch(type) {
		case 0:
			if(combinationsMode) setUpCombinationRuns(); 
			else setUpNormalRuns();
			break;
		case 1:
			setUpSimulatedAnnealing();
			break;
		case 2:
			setUpFoolishHillClimbing();
			break;
		}
		
		
		runningThreads = new Thread[numberOfThreads];
		runnables = new GARunnable[numberOfThreads];
		for(int i=0;i<numberOfThreads;i++) {
			runnables[i] = new GARunnable(i, this);
			runningThreads[i] = new Thread(runnables[i]);
			runningThreadCount++;
			runningThreads[i].start();
		}
	}
	
	private void setUpNormalRuns() {
		int numberOfRows = parameters.get(0).size();
		numberOfRuns = numberOfRows*numberOfTrials;
		for(int n=0;n<numberOfRows;n++) {
			for(int i=0;i<numberOfTrials;i++) {
				runQueue.add(GeneticAlgorithmEngine
						.create(datasetName, n+""+i)
						.setPopulationSize((int) parameters.get(0).get(n))
						.setCrossoverRate((float) parameters.get(1).get(n))
						.setMutationRate((float) parameters.get(2).get(n))
						.setSelectionType((Selection.Type) parameters.get(3).get(n))
						.setCrossoverType((Crossover.Type) parameters.get(4).get(n))
						.setMutationType((Mutation.Type) parameters.get(5).get(n))
						.setMaxGenerations((int) parameters.get(6).get(n))
						.setMaxMillis((int) parameters.get(7).get(n))
						.setStagnationTimeout((int) parameters.get(8).get(n))
						.setAlpha((float) parameters.get(9).get(n)) 
						.setBeta((float) parameters.get(10).get(n)) 
						.setGamma((float) parameters.get(11).get(n)) 
						.setVisualizationUpdatePeriod(visualizationUpdatePeriod)
						.setLoggingEnabled(false));				
			}			
		}
	}
	
	private void setUpSimulatedAnnealing() {
		List<Object> types = parameters.get(5);
		long maxMillis = (long) parameters.get(7).get(0);
		numberOfRuns= types.size()*numberOfTrials;
		Dataset d = Dataset.loadDatasetFromFile(datasetName);
		for(int i=0;i<types.size();i++) {
			for(int j=0;j<numberOfTrials;j++) {
				runQueue.add(new SimulatedAnnealingEngine(d, i+""+j)
				.setMutationType((Mutation.Type)types.get(i))
				.setVisualizationUpdatePeriod(visualizationUpdatePeriod)
				.setMaxMillis(maxMillis)
				.setLoggingEnabled(false));
			}
		}
	}
	
	private void setUpFoolishHillClimbing() {
		List<Object> types = parameters.get(5);
		long maxMillis = (long) parameters.get(7).get(0);
		numberOfRuns= types.size()*numberOfTrials;
		Dataset d = Dataset.loadDatasetFromFile(datasetName);
		for(int i=0;i<types.size();i++) {
			for(int j=0;j<numberOfTrials;j++) {
				runQueue.add(new HillClimbingEngine(d, i+""+j)
				.setMutationType((Mutation.Type)types.get(i))
				.setVisualizationUpdatePeriod(visualizationUpdatePeriod)
				.setMaxMillis(maxMillis)
				.setLoggingEnabled(false));
			}
		}
	}
	
	private void setUpCombinationRuns() {
		int numberOfCombinations = 1;
		boolean[] variableParameter = new boolean[parameters.size()];
		for(int i=0;i<parameters.size();i++) {
			if(parameters.get(i).size()>1) variableParameter[i] = true;
			numberOfCombinations *= parameters.get(i).size();
		}
		
		for(int n = 0;n<numberOfCombinations;n++) {
			int[] indexes = new int[parameters.size()];
			int nn= n;
			for(int i=0;i<parameters.size();i++) {	
				indexes[i] = nn % parameters.get(i).size();
				nn /= parameters.get(i).size();
			}
			String id = "";
			for(int i=0;i<indexes.length;i++) {
				if(variableParameter[i])
					id += indexes[i];
			}
			
			for(int i=0;i<numberOfTrials;i++) {
				runQueue.add(GeneticAlgorithmEngine
						.create(datasetName, id+i)
						.setPopulationSize((int) parameters.get(0).get(indexes[0]))
						.setCrossoverRate((float) parameters.get(1).get(indexes[1]))
						.setMutationRate((float) parameters.get(2).get(indexes[2]))
						.setSelectionType((Selection.Type) parameters.get(3).get(indexes[3]))
						.setCrossoverType((Crossover.Type) parameters.get(4).get(indexes[4]))
						.setMutationType((Mutation.Type) parameters.get(5).get(indexes[5]))
						.setMaxGenerations((int) parameters.get(6).get(indexes[6]))
						.setMaxMillis((int) parameters.get(7).get(indexes[7]))
						.setStagnationTimeout((int) parameters.get(8).get(indexes[8]))
						.setAlpha((float) parameters.get(9).get(indexes[9])) 
						.setBeta((float) parameters.get(10).get(indexes[10])) 
						.setGamma((float) parameters.get(11).get(indexes[11])) 
						.setVisualizationUpdatePeriod(visualizationUpdatePeriod)
						.setLoggingEnabled(false));				
			}

		}
		
		numberOfRuns = numberOfCombinations*numberOfTrials;
	}
	
	public void setViewApp(DataViewerApplication viewApp) {
		this.viewApp = viewApp;
	}
	
	public List<Result> getResults() {
		return new ArrayList<Result>(results);
	}
	
	public void forceStop() {
		forceStop = true;
		GARunnable.viewIndexCounter = 0;
		for(GARunnable e: runnables) {
			if(e!=null) {
				
				e.forceStop();
			}
		}
	}
	
	public void writeDataToFile() {
		Collections.sort(results);
		BufferedWriter writer = null;
		try {
			FileWriter output = new FileWriter("results/"+datasetName+"_"+System.currentTimeMillis()+".txt");
			writer = new BufferedWriter(output);
			writer.append(parameters.toString()+"\n");
			writer.append("ID MIN_SELECTED AVG_SELECTED STDDEV_SELECTED AVG_GENERATIONS\n");
			List<SummaryResult> summary = getSummary();
			for(SummaryResult l: summary) {
				writer.append(l.getId()+" "+l.getMinSelected()+" "+l.getAvgSelected()+ " "+l.getStandardDeviation()+" "+ l.getAvgGenerations()+"\n");
			}
			writer.append("\n\n\nID NUM_SELECTED NUM_GENERATIONS FEASIBLE\n");
			for(Result r:results) {
				writer.append(r.getId()+" "+r.getNumberSelected()+" "+r.getNumberOfGenerations()+" "+r.isFeasible()+"\n");
			}
			writer.close(); 
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<SummaryResult> getSummary() {
		List<SummaryResult> output = new ArrayList<>();
		List<List<Result>> groupedResults = ChartGenerator.getGroupedResults(results);
		for(List<Result> l: groupedResults) {
			int min = ChartGenerator.getMinSelected(l);
			double avg = ChartGenerator.calculateAverageSelected(l);
			double avgGen = ChartGenerator.calculateAverageGenerations(l);
			double standardDeviation = ChartGenerator.calculateStandardDeviation(l, avg);
			
			String id = l.get(0).getId();
			output.add(new SummaryResult(id.substring(0, id.length()-1),min,avg,standardDeviation, avgGen));
		}
		return output;
	}
	
	public void registerListener(DataCollectorListener l) {
		listeners.add(l);
	}
	
	void notifyListeners() {
		for(DataCollectorListener l : listeners) {
			l.dataUpdate();
		}
	}
	
	synchronized GeneticAlgorithmEngine getNextRun() {
		if(runQueue.isEmpty()) return null;
		int randomIndex = GeneticAlgorithmEngine.random.nextInt(runQueue.size());
		GeneticAlgorithmEngine nextRun = runQueue.get(randomIndex);
		runQueue.remove(randomIndex);
		return nextRun;
		
	}
	
	synchronized void decrementRunningThreadCount() {
		runningThreadCount--;
		if(runningThreadCount==0) {
			writeDataToFile();
			viewApp.notifyFinished();
		}
	}
	
	public String getEstimatedTimeRemaining() {
		if(numberOfCompletedRuns==0) return "Unknown";
		long average = sumMillis/numberOfCompletedRuns;
		long millis = average*((numberOfRuns-numberOfCompletedRuns)/numberOfThreads);
		return String.format("%d hours %d minutes %d seconds", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

	public int getNumberOfThreads() {
		return numberOfThreads;
	}
}

class GARunnable implements Runnable {

	static int viewIndexCounter = 0;
	int x;
	DataCollector dataCollector;
	
	int viewIndex;
	
	GeneticAlgorithmEngine nextRun;
	
	public GARunnable(int x, DataCollector dataCollector) {
		this.x = x;
		this.dataCollector = dataCollector;
		viewIndex = viewIndexCounter;
		viewIndexCounter++;
	}
	
	public void forceStop() {
		if(nextRun!=null)
			nextRun.forceStop = true;
	}

	@Override
	public void run() {
		nextRun = null;
		//Wait until the view app has initialized
		while(dataCollector.viewApp==null||!dataCollector.viewApp.initialized) {
			try{
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while(!dataCollector.forceStop && (nextRun=dataCollector.getNextRun())!=null) {
			long startMillis = System.currentTimeMillis();
			Result result = nextRun.setViewApplication(dataCollector.viewApp, viewIndex).start();
			//SAVE RESULTS
			if(result!=null) {
				dataCollector.results.add(result);
				System.out.println(result.getId() + " " + result.isFeasible() + " "
						+ result.getNumberSelected()+" "+result.getNumberOfGenerations());
			}
			
			long endMillis = System.currentTimeMillis();
			dataCollector.numberOfCompletedRuns++;
			dataCollector.sumMillis += (endMillis-startMillis);
			dataCollector.notifyListeners();
		}
		dataCollector.decrementRunningThreadCount();
	}
	
}