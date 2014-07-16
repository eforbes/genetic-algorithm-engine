package edu.utulsa.forbes.evocomp.visual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import edu.utulsa.forbes.evocomp.data.Dataset;

public class DatasetCreator extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField textFieldName;
	private JTextField textFieldPoints;
	private JTextField textFieldDistance;

	private GeneticAlgorithmCreator parent;
	/**
	 * Create the frame.
	 */
	public DatasetCreator(GeneticAlgorithmCreator parent) {
		this.parent = parent;
		setTitle("Dataset Creator");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 263, 152);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][grow]", "[][][][]"));
		
		JLabel lblDatasetName = new JLabel("Dataset name");
		contentPane.add(lblDatasetName, "cell 0 0,alignx trailing");
		
		textFieldName = new JTextField();
		contentPane.add(textFieldName, "cell 1 0,growx");
		textFieldName.setColumns(10);
		
		JLabel lblNumberOfPoints = new JLabel("Number of points");
		contentPane.add(lblNumberOfPoints, "cell 0 1,alignx trailing");
		
		textFieldPoints = new JTextField();
		contentPane.add(textFieldPoints, "cell 1 1,growx");
		textFieldPoints.setColumns(10);
		
		JLabel lblDistanceBonus = new JLabel("Distance bonus");
		contentPane.add(lblDistanceBonus, "cell 0 2,alignx trailing");
		
		textFieldDistance = new JTextField();
		contentPane.add(textFieldDistance, "cell 1 2,growx");
		textFieldDistance.setColumns(10);
		
		JButton btnCreate = new JButton("Create");
		btnCreate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				create();
				
			}
		});
		contentPane.add(btnCreate, "cell 1 3,alignx right");
		setVisible(true);
	}
	
	public void create() {
		int numberOfPoints = Integer.parseInt(textFieldPoints.getText());
		float distanceBonus = Float.parseFloat(textFieldDistance.getText());
		String datasetName = textFieldName.getText();
		Dataset.generateRandomDataset(numberOfPoints, datasetName, distanceBonus);
		parent.updateDatasetComboBox();
		this.dispose();
	}

}
