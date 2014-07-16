package edu.utulsa.forbes.evocomp.visual;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;
import edu.utulsa.forbes.evocomp.GeneticAlgorithmEngine;
import edu.utulsa.forbes.evocomp.GeneticAlgorithmEngine.Parameter;
import edu.utulsa.forbes.evocomp.data.DataCollector;
import edu.utulsa.forbes.evocomp.operators.Crossover;
import edu.utulsa.forbes.evocomp.operators.Mutation;
import edu.utulsa.forbes.evocomp.operators.Selection;

public class GeneticAlgorithmCreator {
	
	public static final int DEFAULT_NUMBER_OF_TRIALS = 5;
	public static final int DEFAULT_NUMBER_OF_THREADS = 3;
	
	public static final String DATASETS_PATH = "datasets/";
	
	public JFrame frame;
	private JTable table;
	private JTextField textFieldTrials;
	private JTextField textFieldThreads;
	private JTextField textFieldVisualization;

	private JComboBox<String> comboBox;
	String[] fileNames;
	private JCheckBox chckbxCombinationsMode;
	
	private ButtonGroup typeGroup;
	private ArrayList<ButtonModel> typeButtons = new ArrayList<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GeneticAlgorithmCreator window = new GeneticAlgorithmCreator();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GeneticAlgorithmCreator() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Genetic Algorithm Creator");
		frame.setBounds(100, 100, 868, 518);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[][grow][]", "[][][][][][][grow][grow][][]"));
		
		JLabel lblDataset = new JLabel("Dataset");
		frame.getContentPane().add(lblDataset, "cell 0 0,alignx trailing");
		
		JLabel lblNumberOfTrials = new JLabel("Number of trials");
		frame.getContentPane().add(lblNumberOfTrials, "cell 0 1,alignx trailing");
		
		textFieldTrials = new JTextField();
		frame.getContentPane().add(textFieldTrials, "cell 1 1,alignx left");
		textFieldTrials.setColumns(10);
		textFieldTrials.setText(DEFAULT_NUMBER_OF_TRIALS+"");
		
		JLabel lblNumberOfThreads = new JLabel("Number of threads");
		frame.getContentPane().add(lblNumberOfThreads, "cell 0 2,alignx trailing");
		
		textFieldThreads = new JTextField();
		frame.getContentPane().add(textFieldThreads, "cell 1 2,alignx left");
		textFieldThreads.setColumns(10);
		textFieldThreads.setText(DEFAULT_NUMBER_OF_THREADS+"");
		
		JLabel lblVisualizationUpdatePeriod = new JLabel("Visualization update period");
		frame.getContentPane().add(lblVisualizationUpdatePeriod, "cell 0 3,alignx trailing");
		
		textFieldVisualization = new JTextField();
		frame.getContentPane().add(textFieldVisualization, "cell 1 3,alignx left");
		textFieldVisualization.setColumns(10);
		textFieldVisualization.setText(GeneticAlgorithmEngine.DEFAULT_VISUALIZATION_UPDATE_PERIOD+"");
		
		JLabel lblType = new JLabel("Type");
		lblType.setHorizontalAlignment(SwingConstants.RIGHT);
		frame.getContentPane().add(lblType, "cell 0 4,alignx right");
		
		
		typeGroup = new ButtonGroup();
		JRadioButton rdbtnGeneticAlgorithm = new JRadioButton("Genetic Algorithm");
		rdbtnGeneticAlgorithm.setSelected(true);
		typeButtons.add(rdbtnGeneticAlgorithm.getModel());
		typeGroup.add(rdbtnGeneticAlgorithm);
		frame.getContentPane().add(rdbtnGeneticAlgorithm, "flowx,cell 1 4");
		
		
		JLabel lblParameters = new JLabel("Parameters");
		frame.getContentPane().add(lblParameters, "cell 0 5,alignx left");
		
		chckbxCombinationsMode = new JCheckBox("Combinations Mode");
		chckbxCombinationsMode.setSelected(true);
		frame.getContentPane().add(chckbxCombinationsMode, "flowx,cell 1 5,alignx right");
		
		JButton button = new JButton("+");
		button.setFont(new Font("Tahoma", Font.PLAIN, 11));
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				((ParametersTableModel)table.getModel()).addRow(chckbxCombinationsMode.isSelected());
				
			}
		});
		frame.getContentPane().add(button, "cell 2 5,alignx right");
		
		table = new JTable(new ParametersTableModel());
		JScrollPane scrollPane = new JScrollPane(table);
		frame.getContentPane().add(scrollPane, "cell 0 6 3 1,grow");
		
		
		
		JButton btnStart = new JButton("Start");
		btnStart.setFont(new Font("Tahoma", Font.PLAIN, 24));
		btnStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				start();
				
			}
		});
		frame.getContentPane().add(btnStart, "cell 2 8,alignx right");
		
		JLabel lblCreatedByEvan = new JLabel("Created by Evan Forbes 2014");
		lblCreatedByEvan.setForeground(Color.GRAY);
		frame.getContentPane().add(lblCreatedByEvan, "cell 0 9 2 1,alignx center");
		
		JRadioButton rdbtnSimulatedAnnealing = new JRadioButton("Simulated Annealing");
		typeGroup.add(rdbtnSimulatedAnnealing);
		typeButtons.add(rdbtnSimulatedAnnealing.getModel());
		frame.getContentPane().add(rdbtnSimulatedAnnealing, "cell 1 4");
		
		JRadioButton rdbtnFoolishHillClimbing = new JRadioButton("Foolish Hill Climbing");
		typeGroup.add(rdbtnFoolishHillClimbing);
		typeButtons.add(rdbtnFoolishHillClimbing.getModel());
		frame.getContentPane().add(rdbtnFoolishHillClimbing, "cell 1 4");
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmGenerateRandomDataset = new JMenuItem("Generate random dataset");
		mntmGenerateRandomDataset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new DatasetCreator(getThis());
				
			}
		});
		mnFile.add(mntmGenerateRandomDataset);
		
		JMenuItem mntmContriveADataset = new JMenuItem("Contrive a dataset");
		mntmContriveADataset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ContrivedDatasetCreator();
				
			}
		});
		mnFile.add(mntmContriveADataset);
		
		JMenuItem mntmExit = new JMenuItem("Exit"); 
		mntmExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmHelp = new JMenuItem("Help");
		mnHelp.add(mntmHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		
		updateDatasetComboBox();
		setUpTable();
	}
	
	public void setUpTable() {
		JComboBox<String> selections = new JComboBox<>(Selection.getTypes());
		TableColumn selectionColumn = table.getColumnModel().getColumn(3);
		selectionColumn.setCellEditor(new DefaultCellEditor(selections));
		
		JComboBox<String> crossovers = new JComboBox<>(Crossover.getTypes());
		TableColumn crossoverColumn = table.getColumnModel().getColumn(4);
		crossoverColumn.setCellEditor(new DefaultCellEditor(crossovers));
		
		JComboBox<String> mutations = new JComboBox<>(Mutation.getTypes());
		TableColumn mutationColumn = table.getColumnModel().getColumn(5);
		mutationColumn.setCellEditor(new DefaultCellEditor(mutations));
		
	}
	
	public void updateDatasetComboBox() {
		File folder = new File(DATASETS_PATH);
		File[] listOfFiles = folder.listFiles();
		fileNames = new String[listOfFiles.length];
		for(int i=0;i<listOfFiles.length;i++) {
			String name = listOfFiles[i].getName();
			fileNames[i] = name.substring(0, name.length()-4);
		}
		if(comboBox==null) {
			comboBox = new JComboBox<>(fileNames);
			frame.getContentPane().add(comboBox, "cell 1 0,alignx left");
		} else {
			comboBox.setModel(new DefaultComboBoxModel<String>(fileNames));
		}
		
	}
	
	public void start() {
		int numberOfTrials = Integer.parseInt(textFieldTrials.getText());
		int numberOfThreads = Integer.parseInt(textFieldThreads.getText());
		int visualizationUpdatePeriod = Integer.parseInt(textFieldVisualization.getText());
		int type = typeButtons.indexOf(typeGroup.getSelection());
		String datasetName = fileNames[comboBox.getSelectedIndex()];
		boolean combinationsMode = chckbxCombinationsMode.isSelected();
		
		List<List<Object>> parameters = ((ParametersTableModel)table.getModel()).getParameterList();
		
		DataCollector dataCollector = new DataCollector(numberOfThreads, numberOfTrials, visualizationUpdatePeriod, datasetName, parameters, combinationsMode, type);
		
		dataCollector.setViewApp(new DataViewerApplication(dataCollector, this));
		
		frame.setVisible(false);			
	}
	
	public GeneticAlgorithmCreator getThis() {
		return this;
	}

}

@SuppressWarnings("rawtypes")
class ParametersTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	String[] columns;
	Class[] columnTypes;
	List<Object[]> data;
	
	public ParametersTableModel() {
		columns = GeneticAlgorithmEngine.Parameter.getParameterNames();
		columnTypes = GeneticAlgorithmEngine.Parameter.getParameterTypes();
		data = new ArrayList<>();
		data.add(Parameter.getDefaults());
	}
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	public String getColumnName(int col) {
		return columns[col];
	}
	
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		return data.get(row)[col];
	}
	 
	public void setValueAt(Object value, int row, int col) {
		data.get(row)[col] = value;
		fireTableCellUpdated(row, col);
	}
	
	public boolean isCellEditable(int row, int column) {
		return true;
	}
	
	public void addRow(boolean blank) {
		if(blank) {
			Object[] newRow = new Object[columns.length];
			data.add(newRow);
		}
		else data.add(Parameter.getDefaults());
		fireTableRowsInserted(data.size()-1, data.size()-1);
	}
	
	public List<List<Object>> getParameterList() {
		List<List<Object>> masterList = new ArrayList<>();
		for(int i=0;i<columns.length;i++) {
			masterList.add(new ArrayList<Object>());
		}
		
		for(int i=0;i<data.size();i++) {
			for(int j=0;j<data.get(i).length;j++) {
				Object element = data.get(i)[j];
				if(element!=null) {
					if(j==3) {
						//types
						element = Selection.Type.valueOf((String) element);
					}
					if(j==4) {
						//types
						element = Crossover.Type.valueOf((String) element);
					}
					if(j==5) {
						//types
						element = Mutation.Type.valueOf((String) element);
					}
					masterList.get(j).add(element);
				}
			}
		}
		
		return masterList;
	}
	
}
