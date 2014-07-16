package edu.utulsa.forbes.evocomp.visual;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import edu.utulsa.forbes.evocomp.data.DataCollector;
import edu.utulsa.forbes.evocomp.data.DataCollectorListener;
import edu.utulsa.forbes.evocomp.data.Dataset;
import edu.utulsa.forbes.evocomp.data.Result;
import edu.utulsa.forbes.evocomp.data.SummaryResult;

public class DataViewerApplication  implements DataCollectorListener {
	
	private JFrame frmSimpleGeneticAlgorithm;
	
	private DataCollector dataCollector;
	private List<Result> results;
	private Dataset dataset;
	
	JLabel lblChart;
	JScrollBar scrollBar;
	Image[] images;
	JLabel[] lblImage;
	JProgressBar progressBar;
	JPanel panelLiveImages;
	private JTable table;
	private JTable table_Summary;
	private JPanel panelCenter;
	private CardLayout cl_panelCenter;
	private JScrollPane scrollPaneSummary;
	private JLabel staticImageLabel;
	private JPanel panelStaticImage;
	private JPanel panelMain;
	private JPanel panelLeft;
	private JLabel lblTime;

	public boolean initialized = false;
	
	private int numberOfImages;
	
	private GeneticAlgorithmCreator parent;
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DataViewerApplication window = new DataViewerApplication();
					window.frmSimpleGeneticAlgorithm.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the application.
	 */
	public DataViewerApplication(DataCollector dataCollector, GeneticAlgorithmCreator parent) {
		initialized = false;
		this.parent = parent;
		this.dataCollector = dataCollector;
		this.results = dataCollector.getResults();
		dataCollector.registerListener(this);
		dataset = Dataset.loadDatasetFromFile(dataCollector.datasetName);
		numberOfImages = dataCollector.getNumberOfThreads()+1;
		initialize();
		initialized = true;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSimpleGeneticAlgorithm = new JFrame();
		frmSimpleGeneticAlgorithm.setTitle("Simple Genetic Algorithm: Geometric Connected Dominating Set Problem");
		frmSimpleGeneticAlgorithm.setBounds(10, 10, 900, 600);
		frmSimpleGeneticAlgorithm.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
            	//dataCollector.forceStop = true;
            	dataCollector.forceStop();
                parent.frame.setVisible(true);
                frmSimpleGeneticAlgorithm.dispose();
            }
        };
        frmSimpleGeneticAlgorithm.addWindowListener(exitListener);

		results = new ArrayList<Result>();
		
		frmSimpleGeneticAlgorithm.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							dataUpdate();
							updateImageSizes();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

			}

			@Override
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		panelMain = new JPanel();
		frmSimpleGeneticAlgorithm.getContentPane().add(panelMain, BorderLayout.CENTER);
		panelMain.setLayout(new BorderLayout(5, 5));
		
		JPanel panelTop = new JPanel();
		panelMain.add(panelTop, BorderLayout.NORTH);
		panelTop.setLayout(new BorderLayout(0, 5));
		
		JLabel lblSga = new JLabel("Dataset: "+ dataCollector.datasetName);
		lblSga.setFont(new Font("Source Sans Pro Semibold", Font.PLAIN, 18));
		lblSga.setHorizontalAlignment(SwingConstants.CENTER);
		panelTop.add(lblSga, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		panelTop.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel label = new JLabel("     ");
		panel.add(label);
		
		progressBar = new JProgressBar();
		panel.add(progressBar);
		progressBar.setFont(new Font("Source Sans Pro Semibold", Font.PLAIN, 18));
		progressBar.setStringPainted(true);
		progressBar.setMaximum(dataCollector.numberOfRuns); //FIXME
		
		JLabel label_1 = new JLabel("    ");
		panel.add(label_1);
		
		lblTime = new JLabel("Estimated time remaining: Unknown      ");
		lblTime.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblTime.setHorizontalAlignment(SwingConstants.TRAILING);
		panelTop.add(lblTime, BorderLayout.SOUTH);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setModel(new ResultsTableModel(results));
		table.setAutoCreateRowSorter(true);
		table.getSelectionModel().addListSelectionListener(new RowListener());
		JScrollPane panelRight = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		panelRight.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panelRight.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		panelMain.add(panelRight, BorderLayout.EAST);
		
		JPanel panelBottom = new JPanel();
		panelBottom.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelMain.add(panelBottom, BorderLayout.SOUTH);
		panelBottom.setLayout(new BorderLayout(0, 0));
		
		lblChart = new JLabel("");
		lblChart.setHorizontalAlignment(SwingConstants.CENTER);
		panelBottom.add(lblChart);
		lblChart.setText("No data");
		
		scrollBar = new JScrollBar();
		scrollBar.setUnitIncrement(1);
		scrollBar.setBlockIncrement(1);
		scrollBar.setMaximum(90);
		scrollBar.setOrientation(JScrollBar.HORIZONTAL);
		scrollBar.addAdjustmentListener(new AdjustmentListener() {
			
			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				
				dataUpdate();
				
			}
		});
		panelBottom.add(scrollBar, BorderLayout.SOUTH);
		
		panelCenter = new JPanel();
		panelMain.add(panelCenter, BorderLayout.CENTER);
		cl_panelCenter = new CardLayout(0, 0);
		panelCenter.setLayout(cl_panelCenter);
		
		panelLiveImages = new JPanel();
		panelCenter.add(panelLiveImages, "live");
		panelLiveImages.setLayout(new GridLayout(numberOfImages/2, 0, 5, 5));
		
		panelStaticImage = new JPanel();
		panelCenter.add(panelStaticImage, "static");
		
		staticImageLabel = new JLabel("");
		panelStaticImage.add(staticImageLabel);
		
		panelLeft = new JPanel();
		panelMain.add(panelLeft, BorderLayout.WEST);
		panelLeft.setLayout(new BorderLayout(0, 0));
		
		images = new Image[numberOfImages];
		lblImage = new JLabel[numberOfImages];
		for(int i=0;i<numberOfImages;i++) {
			lblImage[i] = new JLabel("");
			if(i==(numberOfImages-1)) lblImage[i].setText("Click on a row in the table to view it");
			panelLiveImages.add(lblImage[i]);
		}
		updateImageSizes();
		
		frmSimpleGeneticAlgorithm.setVisible(true);
	}

	@Override
	public void dataUpdate() {
		results = dataCollector.getResults();
		lblChart.setText("");
		lblChart.setIcon(new ImageIcon(ChartGenerator.generatePlot(results, frmSimpleGeneticAlgorithm.getWidth()-50, 300, scrollBar.getValue()*100)));
		((ResultsTableModel) table.getModel()).updateData(results);
		progressBar.setValue(results.size());
		lblTime.setText("Estimated time remaining: "+dataCollector.getEstimatedTimeRemaining()+"      ");
		frmSimpleGeneticAlgorithm.invalidate();
	}
	
	public void imageUpdate(int index, BitSet chromosome, String info) {
		int imgHeight = panelLiveImages.getHeight()/2;
		if(imgHeight==0) imgHeight=250;
		images[index] = ImageGenerator.generateImage(dataset,chromosome, info);
		lblImage[index].setIcon(new ImageIcon(images[index].getScaledInstance(-1, imgHeight, Image.SCALE_SMOOTH)));
		lblImage[index].setText("");
		if(index==(numberOfImages-1)) {
			imgHeight = panelStaticImage.getHeight();
			int imgWidth =  panelStaticImage.getWidth();
			staticImageLabel.setIcon(new ImageIcon(images[index].getScaledInstance(imgWidth, -1, Image.SCALE_SMOOTH)));
		}
		frmSimpleGeneticAlgorithm.invalidate();
	}
	
	public void updateImageSizes() {
		int imgHeight = panelLiveImages.getHeight()/2;
		if(imgHeight==0) imgHeight=250;
		for(int i=0;i<numberOfImages;i++) {
			if(images[i]!=null) {
				if(i==numberOfImages-1) {
					staticImageLabel.setIcon(new ImageIcon(images[i].getScaledInstance(-1, imgHeight, Image.SCALE_SMOOTH)));
				}
				lblImage[i].setIcon(new ImageIcon(images[i].getScaledInstance(-1, imgHeight, Image.SCALE_SMOOTH)));
			}
		}
		frmSimpleGeneticAlgorithm.invalidate();
	}
	
	 private class RowListener implements ListSelectionListener {
	        public void valueChanged(ListSelectionEvent event) {
	            if (event.getValueIsAdjusting()) {
	                return;
	            }
	            int index = table.getSelectionModel().getLeadSelectionIndex();
	            if(index!=-1) {
	            	imageUpdate(numberOfImages-1,results.get(index).getChromosome(),results.get(index).toString());
	            }
	            
	        }
	    }

	public void notifyFinished() {
		System.out.println("done");
		cl_panelCenter.show(panelCenter, "static");
		
		table_Summary = new JTable(new SummaryTableModel(dataCollector.getSummary()));
		table_Summary.setAutoCreateRowSorter(true);

		scrollPaneSummary = new JScrollPane(table_Summary);
		scrollPaneSummary.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneSummary.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		table_Summary.setFillsViewportHeight(true);
		panelLeft.add(scrollPaneSummary);
		
		updateImageSizes();
	}
	
}

@SuppressWarnings("rawtypes")
class ResultsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	String[] columns =  new String[] {
			"ID", "Feasible", "Selected", "Generations" };
	
	List<Result> data;
	
	public ResultsTableModel(List<Result> results) {
		data=results;
	}
	
	public String getColumnName(int col) {
		return columns[col];
	}
	
	Class[] columnTypes = new Class[] {
			String.class, Boolean.class, Integer.class, Integer.class
	};
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}
	boolean[] columnEditables = new boolean[] {
			false, false, false, false
	};
	public boolean isCellEditable(int row, int column) {
		return columnEditables[column];
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public int getRowCount() {
		if(data==null||data.size()==0) return 0;
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if(data==null||data.size()==0) {
			//if (col==0) return "No data";
			return null;
		}
		Result element = data.get(row);
		switch(col) {
		case 0: return element.getId(); 
		case 1: return element.isFeasible();
		case 2: return element.getNumberSelected();
		case 3: return element.getNumberOfGenerations();
		}
		return null;
	}
	
	void updateData(List<Result> newData) {
		if(this.data.size()==newData.size()) return;
		int firstRow = this.data.size();
		int lastRow = newData.size()-1;
		this.data = newData;
		this.fireTableRowsInserted(firstRow, lastRow);
	}
}

@SuppressWarnings("rawtypes")
class SummaryTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	String[] columns =  new String[] {
			"ID", "Minimum selected","Average selected", "Average generations" };
	
	List<SummaryResult> data;
	
	public SummaryTableModel(List<SummaryResult> results) {
		data=results;
	}
	
	public String getColumnName(int col) {
		return columns[col];
	}
	
	Class[] columnTypes = new Class[] {
			String.class, Integer.class, Double.class, Double.class
	};
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public int getRowCount() {
		if(data==null||data.size()==0) return 0;
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		SummaryResult element = data.get(row);
		switch(col) {
		case 0: return element.getId(); 
		case 1: return element.getMinSelected();
		case 2: return element.getAvgSelected();
		case 3: return element.getAvgGenerations();
		}
		return null;
	}
}