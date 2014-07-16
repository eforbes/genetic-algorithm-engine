package edu.utulsa.forbes.evocomp.data;

public class SummaryResult {
	private String id;
	private int minSelected;
	private double avgSelected;
	private double standardDeviation;
	private double avgGenerations;
	
	public SummaryResult(String id, int minSelected, double avgSelected, double standardDeviation,
			double avgGenerations) {
		this.id = id;
		this.minSelected = minSelected;
		this.avgSelected = avgSelected;
		this.standardDeviation = standardDeviation;
		this.avgGenerations = avgGenerations;
	}

	public String getId() {
		return id;
	}

	public int getMinSelected() {
		return minSelected;
	}

	public double getAvgSelected() {
		return avgSelected;
	}

	public double getStandardDeviation() {
		return standardDeviation;
	}
	
	public double getAvgGenerations() {
		return avgGenerations;
	}
	

}
