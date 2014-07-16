package edu.utulsa.forbes.evocomp.visual;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.BitSet;
import java.util.List;

import javax.swing.JFrame;

import edu.utulsa.forbes.evocomp.data.Dataset;
import edu.utulsa.forbes.evocomp.data.Point;

public class GAFrame extends JFrame implements KeyListener {

	private static final long serialVersionUID = 1L;
	public static final int OFFSET_X = 17;
	public static final int OFFSET_Y = 69;

	List<Point> points;
	float distance;

	int circleSize = 8;

	BitSet draw;
	String infoString;
	Dataset dataset;

	public GAFrame(Dataset dataset) {
		this(dataset, null, null);
	}
	
	public GAFrame(Dataset dataset, BitSet draw) {
		this(dataset, draw, null);
	}

	public GAFrame(Dataset dataset, BitSet draw, String infoString) {
		this.dataset = dataset;
		this.draw = draw;
		this.infoString = infoString;
		setSize((int)Dataset.MAX_X+OFFSET_X+18, (int)Dataset.MAX_Y+OFFSET_Y+18);
		setVisible(true);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		addKeyListener(this);
	}
	
	public void updateVisualization(BitSet newDraw, String newInfo) {
		draw = newDraw;
		infoString = newInfo;
		repaint();
	}

	public void paint(Graphics g) {
		Image image = ImageGenerator.generateImage(dataset, draw, infoString);
		g.drawImage(image, 8, 30, this);
	}
	
	public void update(Graphics g) {
		Graphics offgc;
		Image offscreen = null;
		Dimension d = getSize();

		offscreen = createImage(d.width, d.height);
		offgc = offscreen.getGraphics();

		offgc.setColor(getBackground());
		offgc.fillRect(0, 0, d.width, d.height);
		offgc.setColor(getForeground());

		paint(offgc);

		g.drawImage(offscreen, 0, 0, this);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(e.getKeyChar()==KeyEvent.VK_ENTER) { 
			//GeneticAlgorithmEngine.forceStop = true;
		}
		
	}
}
