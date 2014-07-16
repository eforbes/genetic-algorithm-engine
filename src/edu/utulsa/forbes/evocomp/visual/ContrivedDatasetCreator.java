package edu.utulsa.forbes.evocomp.visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import edu.utulsa.forbes.evocomp.data.Dataset;
import edu.utulsa.forbes.evocomp.data.Point;

public class ContrivedDatasetCreator extends JFrame implements MouseListener, MouseWheelListener, KeyListener{

	private static final long serialVersionUID = 1L;
	
	public static final int OFFSET_X = 25;
	public static final int OFFSET_Y = 50;
	
	List<Point> points;
	float distance;
	
	public ContrivedDatasetCreator() {
		this.points = new ArrayList<>();
		this.distance = 20;

		setSize((int)Dataset.MAX_X+OFFSET_X+25, (int)Dataset.MAX_Y+OFFSET_Y+25);
		setVisible(true);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		addMouseListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, (int)(Dataset.MAX_X+OFFSET_X*2),(int)( Dataset.MAX_Y+OFFSET_Y*2));
		for(int i=0;i<points.size();i++) {
			Point p = points.get(i);
			g.setColor(Color.BLACK);
			g.fillOval((int)(p.x-2)+OFFSET_X, (int)(p.y-2)+OFFSET_Y, 4, 4);
			g.setColor(Color.BLUE);
			g.drawOval((int)(p.x-(distance))+OFFSET_X, (int)(p.y-(distance))+OFFSET_Y, (int)distance*2, (int)distance*2);
			g.setColor(Color.BLACK);
			g.drawString(""+i, (int)p.x+OFFSET_X, (int)p.y+OFFSET_Y);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point newPoint = new Point(e.getX()-OFFSET_X, e.getY()-OFFSET_Y);
		points.add(newPoint);
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		distance-=e.getWheelRotation();
		if (distance<1) {
			distance = 1;
		}
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(e.getKeyChar()==KeyEvent.VK_ENTER) { 
			Dataset.writeDatasetToFile(points, distance, "contrived"+System.currentTimeMillis());
		}
		
		if(e.getKeyChar()==KeyEvent.VK_BACK_SPACE) {
			points.remove(points.size()-1);
			repaint();
		}
		
		if(e.getKeyChar()==KeyEvent.VK_DELETE) {
			points.clear();
			repaint();
		}
	}
}
