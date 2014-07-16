package edu.utulsa.forbes.evocomp.visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.utulsa.forbes.evocomp.data.Result;

public class ChartGenerator {
	public static final int X_OFFSET = 30;
	public static final int Y_OFFSET = 30;
	public static final int LABEL_MARGIN = 10;
	
	public static final int BAR_WIDTH = 15;
	
	public static final int PLOT_ICON_SIZE = 10;
	
	public static final int MARGIN_BETWEEN_GROUPS = 50;
	public static final int MARGIN_BETWEEN_BARS = 5;
	
	public static final Color FEASIBLE_COLOR = new Color(0f,0f,.5f,.33f);
	public static final Color INFEASIBLE_COLOR = new Color(1f,0f,0f,.33f);
	public static final Color AVG_LINE_COLOR = new Color(0f,0f,0f,.5f);
	
	
	public static Image generateChart(List<Result> results, int width, int height, int xScroll) {
		Collections.sort(results);
		int chartHeight = height-Y_OFFSET*2;
		int chartWidth = width-X_OFFSET;
		
		Image image = new BufferedImage(X_OFFSET+chartWidth, Y_OFFSET*2+chartHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		Graphics2D g2 = (Graphics2D)g;
		
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		g2.addRenderingHints(new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
		
		g.setColor(Color.WHITE);
		g.fillRect(X_OFFSET, Y_OFFSET, chartWidth, chartHeight);
		
		List<List<Result>> groupedResults = getGroupedResults(results);
		int maxSelected = getMaxSelected(results);
		int lowerBound = getMinSelected(results)-1;
		if(lowerBound<0) lowerBound=0;
		int range = maxSelected-lowerBound;
		g.setColor(Color.BLACK);
		g.drawLine(X_OFFSET, Y_OFFSET, X_OFFSET, Y_OFFSET+chartHeight);
		g.drawLine(X_OFFSET, Y_OFFSET+chartHeight, X_OFFSET+chartWidth, Y_OFFSET+chartHeight);
		
		String label = maxSelected+"";
		g.drawString(label, (int) (X_OFFSET-g.getFontMetrics().getStringBounds(label, g).getWidth()-LABEL_MARGIN), Y_OFFSET);
		
		label = lowerBound+"";
		g.drawString(label, (int) (X_OFFSET-g.getFontMetrics().getStringBounds(label, g).getWidth()-LABEL_MARGIN), Y_OFFSET+chartHeight);
		
		int currentX = X_OFFSET + MARGIN_BETWEEN_GROUPS- xScroll;
		for(int i=0;i<groupedResults.size();i++) {
			List<Result> elements = groupedResults.get(i);
			int numberOfElements = elements.size();
			g.setColor(Color.BLACK);
			label = elements.get(0).getId().substring(0, elements.get(0).getId().length()-1);
			Rectangle2D bounds= g.getFontMetrics().getStringBounds(label, g);
			int labelX = (int) ((currentX+((numberOfElements*BAR_WIDTH)+(numberOfElements-1)*MARGIN_BETWEEN_BARS)/2)-(bounds.getWidth()/2));
			int labelY = (int) (Y_OFFSET+chartHeight+LABEL_MARGIN/2+bounds.getHeight());
			if(labelX>X_OFFSET)
				g.drawString(label, labelX, labelY);
			//g.setColor(new Color(Color.HSBtoRGB((float)Math.random(), 1, 1)));
			
			int drawCount = 0;
			int firstDrawX = 0;
			for(int j = 0; j<elements.size();j++) {
				int barHeight = (int) (chartHeight * ((double) elements.get(j).getNumberSelected()-lowerBound)/(double) range);
				if(currentX>X_OFFSET) {
					if(elements.get(j).isFeasible()) {
						g.setColor(Color.BLUE);
					} else {
						g.setColor(Color.RED);
					}
					g.fillRect(currentX, Y_OFFSET+chartHeight-barHeight, BAR_WIDTH, barHeight); 
					if(drawCount==0) {
						firstDrawX = currentX;
					}
					drawCount++;
				}
				currentX+=BAR_WIDTH;
				if(j!=elements.size()-1) {
					//if not the last bar
					currentX+=MARGIN_BETWEEN_BARS;
				}
			}
			if(drawCount>0) {
				double avg = calculateAverageSelected(elements);
				g.setColor(Color.BLACK);
				int lineY= (int) (Y_OFFSET+chartHeight-chartHeight*((avg-lowerBound)/(double) range));
				String avgLabel = formatDouble(avg);
				double lblWidth = g.getFontMetrics().getStringBounds(avgLabel, g).getWidth();
				g.drawString(avgLabel, firstDrawX + (currentX-firstDrawX)/2 - (int) (lblWidth/2), Y_OFFSET-LABEL_MARGIN/2);
				g.drawLine(firstDrawX, lineY, currentX, lineY);
			}
			currentX+=MARGIN_BETWEEN_GROUPS;
		}
		double avg = calculateAverageSelected(results);
		int lineY = (int) (chartHeight*(((double) avg-lowerBound)/(double) range));
		g.setColor(Color.GRAY);
		g.drawLine(X_OFFSET-LABEL_MARGIN, Y_OFFSET+chartHeight-lineY, X_OFFSET+chartWidth, Y_OFFSET+chartHeight-lineY);
		label = formatDouble(avg);
		g.drawString(label, (int) (X_OFFSET-g.getFontMetrics().getStringBounds(label, g).getWidth()-LABEL_MARGIN/2), Y_OFFSET+chartHeight-lineY);		
		return image;
	}
	
	public static Image generatePlot(List<Result> results, int width, int height, int xScroll) {
		Collections.sort(results);
		int chartHeight = height-Y_OFFSET*2;
		int chartWidth = width-X_OFFSET;
		
		Image image = new BufferedImage(X_OFFSET+chartWidth, Y_OFFSET*2+chartHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		Graphics2D g2 = (Graphics2D)g;
		
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		g2.addRenderingHints(new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
		
		g.setColor(Color.WHITE);
		g.fillRect(X_OFFSET, Y_OFFSET, chartWidth, chartHeight);
		
		List<List<Result>> groupedResults = getGroupedResults(results);
		int upperBound = getMaxSelected(results)+1;
		int lowerBound = getMinSelected(results)-1;
		if(lowerBound<0) lowerBound=0;
		int range = upperBound-lowerBound;
		g.setColor(Color.BLACK);
		g.drawLine(X_OFFSET, Y_OFFSET, X_OFFSET, Y_OFFSET+chartHeight);
		g.drawLine(X_OFFSET, Y_OFFSET+chartHeight, X_OFFSET+chartWidth, Y_OFFSET+chartHeight);
		
		String label = upperBound+"";
		g.drawString(label, (int) (X_OFFSET-g.getFontMetrics().getStringBounds(label, g).getWidth()-LABEL_MARGIN), Y_OFFSET);
		
		label = lowerBound+"";
		g.drawString(label, (int) (X_OFFSET-g.getFontMetrics().getStringBounds(label, g).getWidth()-LABEL_MARGIN), Y_OFFSET+chartHeight);
		
		int currentX = X_OFFSET + MARGIN_BETWEEN_GROUPS- xScroll;
		for(int i=0;i<groupedResults.size();i++) {
			List<Result> elements = groupedResults.get(i);
			g.setColor(Color.BLACK);
			label = elements.get(0).getId().substring(0, elements.get(0).getId().length()-1);
			Rectangle2D bounds= g.getFontMetrics().getStringBounds(label, g);
			int labelX = (int) (currentX-(bounds.getWidth()/2));
			int labelY = (int) (Y_OFFSET+chartHeight+LABEL_MARGIN/2+bounds.getHeight());
			if(labelX>X_OFFSET)
				g.drawString(label, labelX, labelY);

			int drawCount = 0;
			for(int j = 0; j<elements.size();j++) {
				int plotY = (int) (chartHeight * ((double) elements.get(j).getNumberSelected()-lowerBound)/(double) range);
				if(currentX>X_OFFSET) {
					if(elements.get(j).isFeasible()) {
						g.setColor(FEASIBLE_COLOR);
					} else {
						g.setColor(INFEASIBLE_COLOR);
					}
					g.fillRect(currentX, Y_OFFSET+chartHeight-plotY-(PLOT_ICON_SIZE/2), PLOT_ICON_SIZE, PLOT_ICON_SIZE); 
					drawCount++;
				}
			}
			if(drawCount>0) {
				double avg = calculateAverageSelected(elements);
				g.setColor(Color.BLACK);
				int lineY= (int) (Y_OFFSET+chartHeight-chartHeight*((avg-lowerBound)/(double) range));
				g.drawLine(currentX-PLOT_ICON_SIZE, lineY, currentX+PLOT_ICON_SIZE*2, lineY);
				
				double stdev = calculateStandardDeviation(elements, avg);
				int stdevY1 = (int) (Y_OFFSET+chartHeight-chartHeight*((avg+stdev-lowerBound)/(double) range));
				int stdevY2 = (int) (Y_OFFSET+chartHeight-chartHeight*((avg-stdev-lowerBound)/(double) range));
				g.drawLine(currentX+PLOT_ICON_SIZE/2, stdevY1, currentX+PLOT_ICON_SIZE/2, stdevY2);
				
				String avgLabel = formatDouble(avg);
				double lblWidth = g.getFontMetrics().getStringBounds(avgLabel, g).getWidth();
				g.drawString(avgLabel, currentX - (int) (lblWidth/2), Y_OFFSET-LABEL_MARGIN/2);
				
			}
			currentX+=MARGIN_BETWEEN_GROUPS;
		}
		double avg = calculateAverageSelected(results);
		int lineY = (int) (chartHeight*(((double) avg-lowerBound)/(double) range));
		g.setColor(AVG_LINE_COLOR);
		g.drawLine(X_OFFSET-LABEL_MARGIN, Y_OFFSET+chartHeight-lineY, X_OFFSET+chartWidth, Y_OFFSET+chartHeight-lineY);
		label = formatDouble(avg);
		g.drawString(label, (int) (X_OFFSET-g.getFontMetrics().getStringBounds(label, g).getWidth()-LABEL_MARGIN/2), Y_OFFSET+chartHeight-lineY);		
		return image;
	}
	
	public static List<List<Result>> getGroupedResults(List<Result> results) {
		List<List<Result>> masterList = new ArrayList<>();
		List<String> ids = new ArrayList<>();
		for(Result r: results) {
			String id = r.getId().substring(0, r.getId().length()-1);
			int index = ids.indexOf(id);
			if(index>=0) {
				//found
				List<Result> sublist = masterList.get(index);
				sublist.add(r);
			} else {
				//not found
				List<Result> newSubList = new ArrayList<Result>();
				newSubList.add(r);
				masterList.add(newSubList);
				ids.add(id);
			}
		}
		return masterList;
	}
	
	public static int getMaxSelected(List<Result> results) {
		if(results.size()==0) return 0;
		int max = Integer.MIN_VALUE;
		for(Result r: results) {
			if(r.getNumberSelected()>max) {
				max = r.getNumberSelected();
			}
		}
		return max;
	}
	
	public static int getMinSelected(List<Result> results) {
		if(results.size()==0) return 0;
		int min = Integer.MAX_VALUE;
		int count = 0;
		for(Result r: results) {
			if(r.isFeasible()) {
				count++;
				if(r.getNumberSelected()<min) {
					min = r.getNumberSelected();
				}
			}
		}
		if(count==0) return 0;
		return min;
	}
	
	public static double calculateAverageSelected(List<Result> results) {
		int sum = 0;
		int count = 0;
		for(Result r: results) {
			if(r.isFeasible()) {
				sum+=r.getNumberSelected();
				count++;
			}
		}
		if(count==0) return 0;
		return (double) sum / (double) count;
	}
	
	public static double calculateAverageGenerations(List<Result> results) {
		int sum = 0;
		int count = 0;
		for(Result r: results) {
			if(r.isFeasible()) {
				sum+=r.getNumberOfGenerations();
				count++;
			}
		}
		if(count==0) return 0;
		return (double) sum / (double) count;
	}
	
	public static String formatDouble(double d) {
		return String.format("%1.1f", d);
	}

	public static double calculateStandardDeviation(List<Result> results, double mean) {
		double sumOfSquaredDifferenceFromMean = 0;
		for(Result r: results) {
			if(r.isFeasible()) {
				sumOfSquaredDifferenceFromMean += Math.pow((r.getNumberSelected()-mean), 2);
			}
		}
		sumOfSquaredDifferenceFromMean*= (1.0/(double)(results.size()-1));
		return Math.sqrt(sumOfSquaredDifferenceFromMean);
	}
	
}
