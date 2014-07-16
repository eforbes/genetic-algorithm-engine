package edu.utulsa.forbes.evocomp.visual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.util.List;

import edu.utulsa.forbes.evocomp.data.Dataset;
import edu.utulsa.forbes.evocomp.data.Point;

public class ImageGenerator {

	public static final int WIDTH = 819;
	public static final int HEIGHT = 499;
	public static final int OFFSET_X = 9;
	public static final int OFFSET_Y = 39;
	public static final int POINT_SIZE = 8;
	
	public static Image generateImage(Dataset dataset, BitSet selected, String infoString) {
		List<Point> points = dataset.getPoints();
		float distance = dataset.getMaxDistance();
		
		//If no bitset is provided, show all connections
		if(selected==null) {
			selected = new BitSet(points.size());
			selected.set(0, points.size());
		} 
		
		
		Image image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = image.getGraphics();
		Graphics2D g2 = (Graphics2D)g;
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 1000, 1000);
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		g2.addRenderingHints(new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
		
		for(int i=0;i<points.size();i++) {
			Point p = points.get(i);
			if(selected.get(i)){
				g.setColor(Color.RED); 
				g.fillRect((int)(p.x-POINT_SIZE/2)+OFFSET_X, (int)(p.y-POINT_SIZE/2)+OFFSET_Y, POINT_SIZE, POINT_SIZE);
			} else {
				g.setColor(Color.BLACK);
				g.fillOval((int)(p.x-POINT_SIZE/2)+OFFSET_X, (int)(p.y-POINT_SIZE/2)+OFFSET_Y, POINT_SIZE, POINT_SIZE);
			}
			
			if(selected.get(i)) {
				g.setColor(Color.BLUE);
				g.drawOval((int)(p.x-(distance))+OFFSET_X, (int)(p.y-(distance))+OFFSET_Y, (int)distance*2, (int)distance*2);


				g.setColor(Color.GRAY);
				for (int j = selected.nextSetBit(0); j >= 0; j = selected.nextSetBit(j+1)) {
					if(j!=i&& dataset.getGraph().getAdjacency(i, j)) {
						Point a = points.get(i);
						Point b = points.get(j);
						g.drawLine((int)a.x+OFFSET_X, (int)a.y+OFFSET_Y, (int)b.x+OFFSET_X, (int)b.y+OFFSET_Y);
					}
				}

			}
			//g.setColor(Color.BLACK);
			//g.drawString(""+i, (int)p.x+OFFSET_X, (int)p.y+OFFSET_Y);
		}
		if(infoString!=null) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, WIDTH, 30);
			
			float thickness = 5;
			Stroke oldStroke = g2.getStroke();
			g2.setStroke(new BasicStroke(thickness*2));
			g2.setColor(new Color(0,0,0,100));
			g2.drawRect((int)(OFFSET_X-thickness), (int)(OFFSET_Y-thickness), (int)(Dataset.MAX_X+(thickness*2)), (int)(Dataset.MAX_Y+thickness*2));
			g2.setStroke(oldStroke);
			
			g.setColor(Color.WHITE);
			g.setFont(new Font("Source Sans Pro SemiBold", Font.PLAIN, 20));
			g.drawString(infoString, OFFSET_X, 22);
			//setTitle(infoString);
		}
		return image;
	}
	
}
