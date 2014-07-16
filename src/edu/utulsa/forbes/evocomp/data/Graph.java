package edu.utulsa.forbes.evocomp.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.utulsa.forbes.evocomp.GeneticAlgorithmEngine;

/**
 * 
 * @author Evan Forbes
 *
 */
public class Graph {
	private static final int CONNECTEDNESS_POLL_COUNT = 4;
	
	boolean[][] adjacencyMatrix; //triangular:
	/*
	 * \ 0 1 2 3 4
	 * 0 *
	 * 1 * *
	 * 2 * * *        -> if * is true, then the two nodes are adjacent
	 * 3 * * * * 
	 * 4 * * * * *
	 */
	
	/**
	 * initializes the triangular adjacency matrix with all false
	 * @param numberOfNodes the number of nodes in the graph
	 */
	public Graph(int numberOfNodes) {
		adjacencyMatrix = new boolean[numberOfNodes][];
		for (int i=0;i<numberOfNodes;i++) {
			adjacencyMatrix[i] = new boolean[i+1];
		}
	}
	
	/**
	 * Get whether two indexes are adjacent
	 * @param indexA
	 * @param indexB
	 * @return
	 */
	public boolean getAdjacency(int indexA, int indexB) {
		if(indexA>indexB) {
			return adjacencyMatrix[indexA][indexB];
		} else {
			return adjacencyMatrix[indexB][indexA];
		}
	}
	
	/**
	 * Set the adjacency between two indexes
	 * @param indexA
	 * @param indexB
	 * @param adjacent
	 */
	void setAdjacency(int indexA, int indexB, boolean adjacent) {
		if (indexA>indexB) {
			adjacencyMatrix[indexA][indexB] = adjacent;
		}
		else {
			adjacencyMatrix[indexB][indexA] = adjacent;
		}
	}
	
	/**
	 * 
	 * @param index
	 * @return a list of all indexes connected to the given index
	 */
	public List<Integer> getConnectedIndexes(int index) {
		List<Integer> connectedIndexes = new ArrayList<>();
		for(int i=0;i<adjacencyMatrix.length;i++) {
			if(getAdjacency(index, i)) {
				connectedIndexes.add(i);
			}
		}
		return connectedIndexes;
	}
	
	/**
	 * Checks to see if the graph represented by the adjacency matrix is connected
	 * @return true if connected, false otherwise
	 */
	public boolean isConnected() {
		boolean[] visited = new boolean[adjacencyMatrix.length];
		Queue<Integer> visitNext = new LinkedList<>();
		visitNext.add(0);
		while(!visitNext.isEmpty()) {
			int current = visitNext.poll();
			visited[current] = true;

			List<Integer> connectedNodes = getConnectedIndexes(current);
			for(int i=0;i<connectedNodes.size();i++) {
				if(!visited[connectedNodes.get(i)]) {
					visitNext.add(connectedNodes.get(i));
				}
			}
		}
		for(int i=0;i<visited.length;i++) {
			if(!visited[i]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return the string representation of the adjacency matrix associated with this graph
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(int a=0;a<adjacencyMatrix.length;a++) {
			for(int b=0;b<adjacencyMatrix[a].length;b++) {
				if(getAdjacency(a, b)){
					builder.append("1");
				} else builder.append("0");
			}
			builder.append("\n");
		}
		return builder.toString();
	}

	/**
	 * 
	 * @return a nondeterministic measure of connectedness for the graph. 1 is fully connected, higher values are less connected
	 */
	public float calculateConnectedness() {
		if(adjacencyMatrix.length==0) {
			//No selected indexes
			return 1;
		}
		float sumOfAvgs = 0;
		for(int a=0;a<CONNECTEDNESS_POLL_COUNT;a++) {
			int randomPoint = GeneticAlgorithmEngine.random.nextInt(adjacencyMatrix.length); //choose a random node to start at
			
			int visitedCount = 0;
			boolean[] visited = new boolean[adjacencyMatrix.length];
			Queue<Integer> visitNext = new LinkedList<>();
			visitNext.add(randomPoint);

			while(!visitNext.isEmpty()) {
				int current = visitNext.poll();
				
				if(visited[current]) continue; //already visited this node... move on
				
				visited[current] = true;
				visitedCount++;
				
				List<Integer> connectedNodes = getConnectedIndexes(current);
				for(int i=0;i<connectedNodes.size();i++) {
					if(!visited[connectedNodes.get(i)]) {
						//not yet visited, adding to queue
						visitNext.add(connectedNodes.get(i));
					}
				}
			}

			if (visitedCount == adjacencyMatrix.length)
				return 1; // this graph is fully connected
			
			float avg = (float) visitedCount / (float) (adjacencyMatrix.length);
			sumOfAvgs+=avg;
		}
		float avgPercentNodesReached = (float) sumOfAvgs / (float) CONNECTEDNESS_POLL_COUNT;
		return 1f / avgPercentNodesReached;
	}
	
}
