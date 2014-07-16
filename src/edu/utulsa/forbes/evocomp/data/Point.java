package edu.utulsa.forbes.evocomp.data;

public class Point {
	public float x, y;
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float distanceTo(Point other) {
		return (float) Math.sqrt(Math.pow(x-other.x, 2)+Math.pow(y-other.y,2));
	}
}