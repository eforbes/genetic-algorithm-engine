package edu.utulsa.forbes.evocomp.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import edu.utulsa.forbes.evocomp.visual.GAFrame;

/**
 * This class contains the information about the datasets used in the SGA
 * @author Evan Forbes
 *
 */
public class Dataset {

	public static final float MAX_X = 800;
	public static final float MAX_Y = 450;
	
	public static int HYPERMUATATION_PARAMETER = 4;
	
	public String name;
	private List<Point> points;
	private Graph graph;
	private float maxDistance;
	
	static Random random = new Random();
	
	// adjacencyList.get(i) is the list of the indexes of all adjacent points to the point with index i
	private List<List<Integer>> adjacencyList;
	
	public int getNumberOfPoints() {
		return getPoints().size();
	}
	
	public Graph getGraph() {
		return graph;
	}
	
	public static Dataset loadDatasetFromFile(String datasetName) {
		Dataset newDataset = new Dataset();
		try {
			Scanner in = new Scanner(new File("datasets/"+datasetName+".txt"));
			newDataset.name = in.nextLine();
			int numberOfPoints = in.nextInt();
			newDataset.maxDistance = in.nextFloat();
			List<Point> points = new ArrayList<>();
			for(int i=0;i<numberOfPoints;i++) {
				Point p = new Point(in.nextFloat(),in.nextFloat());
				points.add(p);
			}
			newDataset.points = points;
			newDataset.graph = generateGraphFromPoints(points, newDataset.getMaxDistance());
			newDataset.adjacencyList = generateAdjacencyList(newDataset.graph, points);
			in.close();
			return newDataset;
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void generateRandomDataset(int numberOfPoints, String datasetName) {
		generateRandomDataset(numberOfPoints, datasetName, 0);
	}
	
	public static void generateRandomDataset(int numberOfPoints, String datasetName, float distanceBonus) {
		List<Point> points = new ArrayList<>();

		for(int i=0;i<numberOfPoints;i++) {
			Point newPoint = new Point(random.nextFloat()*MAX_X,random.nextFloat()*MAX_Y);
			points.add(newPoint);
		}
		
		float maxDistance = 0;
		for(int a=0;a<points.size();a++){
			float minDistance = -1;
			for(int b=0;b<points.size();b++) {
				float distance = points.get(b).distanceTo(points.get(a));
				if ((distance<minDistance||minDistance<0)&&a!=b) {
					minDistance = distance;
				}
			}
			if (minDistance>maxDistance&&minDistance>0) {
				maxDistance = minDistance;
			}		
		}
		
		//Increase the distance factor until the graph is connected
		Graph g = generateGraphFromPoints(points, maxDistance);
		while(!g.isConnected()) {
			maxDistance+=5;
			g = generateGraphFromPoints(points, maxDistance);
		}
		maxDistance+=distanceBonus;
		
		writeDatasetToFile(points, maxDistance, datasetName);
		
		Dataset newDataset = new Dataset();
		newDataset.name = datasetName;
		newDataset.points = points;
		newDataset.maxDistance = maxDistance;
		newDataset.graph = g;
		newDataset.adjacencyList = generateAdjacencyList(g, points);
		new GAFrame(newDataset);
	}
	
	public static Graph generateGraphFromPoints(List<Point> points, float maxDistance) {
		int numberOfPoints = points.size();
		Graph graph = new Graph(numberOfPoints);
		for(int a=0;a<numberOfPoints;a++) {
			for(int b=a;b<numberOfPoints;b++) {
				if(points.get(a).distanceTo(points.get(b))<=maxDistance) {
					graph.setAdjacency(a, b, true);
				}
			}
		}
		return graph;
	}
	
	public static List<List<Integer>> generateAdjacencyList(Graph graph, List<Point> points) {
		List<List<Integer>> adjacencyList = new ArrayList<>();
		for(int i=0;i<graph.adjacencyMatrix.length;i++) {
			List<Integer> connected = graph.getConnectedIndexes(i);
			if(connected.size()<=HYPERMUATATION_PARAMETER){
				adjacencyList.add(connected);
			} else{
				List<Integer> nearestN = new ArrayList<Integer>();
				for(int j=0;j<HYPERMUATATION_PARAMETER;j++) {
					float minDistance = Float.MAX_VALUE;
					int minIndex = -1;
					for(int k=0;k<connected.size();k++) {
						float dist = points.get(i).distanceTo(points.get(connected.get(k)));
						if(dist<minDistance) {
							minDistance = dist;
							minIndex = k;
						}
					}
					nearestN.add(connected.get(minIndex));
					connected.remove(minIndex);
					}
				//System.out.println("Nearest N: "+nearestN);
				adjacencyList.add(nearestN);
			}
			
		}
		return adjacencyList;
	}
	
	public static void writeDatasetToFile(List<Point> points, float maxDistance, String datasetName) {
		try {
			FileWriter output = new FileWriter("datasets/"+datasetName+".txt");
			BufferedWriter writer = new BufferedWriter(output);
			writer.append(datasetName+"\n"+points.size()+"\n"+maxDistance+"\n");
			for (int i=0;i<points.size();i++) {
				writer.append(points.get(i).x+" "+points.get(i).y+"\n");
			}
			writer.close();
			System.out.println("closed");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return name+": "+getNumberOfPoints()+", distance: "+getMaxDistance();
	}

	public List<Point> getPoints() {
		return points;
	}

	public float getMaxDistance() {
		return maxDistance;
	}
	
	public List<Integer> getNearestN(int i) {
		return adjacencyList.get(i);
	}
}
