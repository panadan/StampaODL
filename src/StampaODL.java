import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import org.eclipse.wb.swing.FocusTraversalOnArray;


public class StampaODL {

	private JFrame frmStampaOdl;
	private DataManager dataMng;
	private JLabel lblConnessione;
	private JLabel lblInfo;
	private JFormattedTextField ftfDaOdl;
	private JFormattedTextField ftfAOdl;
	private JComboBox<String> cbTipoStampa;
	private JButton btnStampa;
	private JButton btnAnteprima;
	
	private Task task;
	  
	    class Task extends SwingWorker<Void, Void> {
	    	private int modo;
	    	
	        public Task(int modo) {
				
	        	super();
	        	this.modo=modo;
				
			}

			/*
	         * Main task. Executed in background thread.
	         */
	        @Override
	        public Void doInBackground() {
	    		lblInfo.setText("Elaborazione immagini disegni");
	    		if(!dataMng.preparaOdl(ftfDaOdl.getText(),ftfAOdl.getText(),cbTipoStampa.getSelectedIndex() )){
	    			JOptionPane.showMessageDialog(null, "Non ci sono Record da Stampare", "StampaODL: Verifica limiti di selezione", JOptionPane.INFORMATION_MESSAGE);
	    			return null;
	    		}
	    		lblInfo.setText("Elaborazione stampa");

	    		//se modalita Debug non lanciare nessun processo stampa/anteprima
	    		if (cbTipoStampa.getSelectedIndex() != 3){
	    			//stampa
	    			if(modo == 0){
	    				lblInfo.setText("In Stampa");
	    				new PrintReport("ODL_bck02.rpt",dataMng.getUrl(),ftfDaOdl.getText(),ftfAOdl.getText(), cbTipoStampa.getSelectedIndex(), dataMng.getPathPng(), dataMng.getStampanteFR());
	    			}
	    			//anteprima
	    			else if (modo == 1){
	    				lblInfo.setText("Generazione Anteprima");
	    				new SampleViewerFrame("ODL_bck02.rpt",dataMng.getUrl(),ftfDaOdl.getText(),ftfAOdl.getText(),cbTipoStampa.getSelectedIndex(), dataMng.getPathPng());
	    			}
	    		}
	    		
	    		if (cbTipoStampa.getSelectedIndex()==0)
	    			dataMng.aggiornaStato(ftfDaOdl.getText(),ftfAOdl.getText());
	    		
	    		lblInfo.setText(">");
	            return null;
	        }
	 
	        /*
	         * Executed in event dispatching thread
	         */
	        @Override
	        public void done() {
	            Toolkit.getDefaultToolkit().beep();
	            frmStampaOdl.setEnabled(true);
	            frmStampaOdl.setCursor(null); //turn off the wait cursor
	            lblInfo.setText(">");
	        }
	    }

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
			        UIManager.setLookAndFeel(
			        		UIManager.getSystemLookAndFeelClassName());
					StampaODL window = new StampaODL();
					window.initDataMng();
					window.frmStampaOdl.setVisible(true);
					
				} catch (Exception e) {
					
					JOptionPane.showMessageDialog(null, e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public StampaODL() {
		initialize();
		//initDataMng();
		//dataMng.testOpenImg();
	}
	
	/**
	 * Initialize datamanager
	 */
	private void initDataMng(){
		dataMng = new DataManager(lblInfo);
		lblConnessione.setText(dataMng.connect());
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmStampaOdl = new JFrame();
		frmStampaOdl.setResizable(false);
		frmStampaOdl.setIconImage(Toolkit.getDefaultToolkit().getImage(StampaODL.class.getResource("/ico/Clipboard-icon.ico")));
		frmStampaOdl.setTitle("Stampa ODL");
		frmStampaOdl.setBounds(100, 100, 292, 198);
		frmStampaOdl.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{10, 0, 0, 0, 10, 0};
		gridBagLayout.rowHeights = new int[]{10, 0, 0, 20, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 20.0, 0.0, Double.MIN_VALUE};
		frmStampaOdl.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblTipoStampa = new JLabel("Tipo Stampa");
		GridBagConstraints gbc_lblTipoStampa = new GridBagConstraints();
		gbc_lblTipoStampa.insets = new Insets(0, 0, 5, 5);
		gbc_lblTipoStampa.anchor = GridBagConstraints.EAST;
		gbc_lblTipoStampa.gridx = 1;
		gbc_lblTipoStampa.gridy = 1;
		frmStampaOdl.getContentPane().add(lblTipoStampa, gbc_lblTipoStampa);
		
		cbTipoStampa = new JComboBox<String>();
		cbTipoStampa.setModel(new DefaultComboBoxModel(new String[] {"Definitiva", "Ristampa", "Prova", "Debug"}));
		GridBagConstraints gbc_cbTipoStampa = new GridBagConstraints();
		gbc_cbTipoStampa.gridwidth = 2;
		gbc_cbTipoStampa.insets = new Insets(0, 0, 5, 5);
		gbc_cbTipoStampa.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbTipoStampa.gridx = 2;
		gbc_cbTipoStampa.gridy = 1;
		frmStampaOdl.getContentPane().add(cbTipoStampa, gbc_cbTipoStampa);
		
		JLabel lblNumeroOdlDa = new JLabel("Numero da / a");
		GridBagConstraints gbc_lblNumeroOdlDa = new GridBagConstraints();
		gbc_lblNumeroOdlDa.insets = new Insets(0, 0, 5, 5);
		gbc_lblNumeroOdlDa.anchor = GridBagConstraints.EAST;
		gbc_lblNumeroOdlDa.gridx = 1;
		gbc_lblNumeroOdlDa.gridy = 2;
		frmStampaOdl.getContentPane().add(lblNumeroOdlDa, gbc_lblNumeroOdlDa);
		
		ftfDaOdl = new JFormattedTextField();
		GridBagConstraints gbc_ftfDaOdl = new GridBagConstraints();
		gbc_ftfDaOdl.insets = new Insets(0, 0, 5, 5);
		gbc_ftfDaOdl.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfDaOdl.gridx = 2;
		gbc_ftfDaOdl.gridy = 2;
		frmStampaOdl.getContentPane().add(ftfDaOdl, gbc_ftfDaOdl);
		
		ftfAOdl = new JFormattedTextField();
		GridBagConstraints gbc_ftfAOdl = new GridBagConstraints();
		gbc_ftfAOdl.insets = new Insets(0, 0, 5, 5);
		gbc_ftfAOdl.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfAOdl.gridx = 3;
		gbc_ftfAOdl.gridy = 2;
		frmStampaOdl.getContentPane().add(ftfAOdl, gbc_ftfAOdl);
		
		btnStampa = new JButton("Stampa");
		btnStampa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				stampa(0);
			}
		});
		GridBagConstraints gbc_btnStampa = new GridBagConstraints();
		gbc_btnStampa.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnStampa.insets = new Insets(0, 0, 5, 5);
		gbc_btnStampa.gridx = 2;
		gbc_btnStampa.gridy = 4;
		frmStampaOdl.getContentPane().add(btnStampa, gbc_btnStampa);
		
		btnAnteprima = new JButton("Anteprima");
		btnAnteprima.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				stampa(1);
			}
		});
		GridBagConstraints gbc_btnAnteprima = new GridBagConstraints();
		gbc_btnAnteprima.insets = new Insets(0, 0, 5, 5);
		gbc_btnAnteprima.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAnteprima.gridx = 3;
		gbc_btnAnteprima.gridy = 4;
		frmStampaOdl.getContentPane().add(btnAnteprima, gbc_btnAnteprima);
		
		lblInfo = new JLabel(">");
		GridBagConstraints gbc_lblInfo = new GridBagConstraints();
		gbc_lblInfo.anchor = GridBagConstraints.WEST;
		gbc_lblInfo.gridwidth = 3;
		gbc_lblInfo.insets = new Insets(0, 0, 5, 5);
		gbc_lblInfo.gridx = 1;
		gbc_lblInfo.gridy = 5;
		frmStampaOdl.getContentPane().add(lblInfo, gbc_lblInfo);
		
		lblConnessione = new JLabel(">");
		GridBagConstraints gbc_lblConnessione = new GridBagConstraints();
		gbc_lblConnessione.fill = GridBagConstraints.VERTICAL;
		gbc_lblConnessione.gridwidth = 3;
		gbc_lblConnessione.insets = new Insets(0, 0, 0, 5);
		gbc_lblConnessione.anchor = GridBagConstraints.EAST;
		gbc_lblConnessione.gridx = 1;
		gbc_lblConnessione.gridy = 6;
		frmStampaOdl.getContentPane().add(lblConnessione, gbc_lblConnessione);
		frmStampaOdl.getContentPane().setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{ftfDaOdl, ftfAOdl, btnStampa, btnAnteprima, cbTipoStampa, lblTipoStampa, lblNumeroOdlDa, lblInfo, lblConnessione}));
		frmStampaOdl.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{ftfDaOdl, ftfAOdl, btnStampa, btnAnteprima, cbTipoStampa, frmStampaOdl.getContentPane(), lblTipoStampa, lblNumeroOdlDa, lblInfo, lblConnessione}));
	}

	private void stampa(int modo) {
		if(!controllaLimiti()) return;

	    /**
	     * Invoked when the user presses the start button.
	     */
	    //public void actionPerformed(ActionEvent evt) {
		frmStampaOdl.setEnabled(false);
	    frmStampaOdl.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	        //Instances of javax.swing.SwingWorker are not reusuable, so
	        //we create new instances as needed.
        task = new Task(modo);
//        task.addPropertyChangeListener( new PropertyChangeListener() {
//			
//			@Override
//			public void propertyChange(PropertyChangeEvent evt) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
		task.execute();
    

	}

	private boolean controllaLimiti() {
		int numeroRecord = Integer.valueOf(ftfAOdl.getText()) - Integer.valueOf(ftfDaOdl.getText());
		if (numeroRecord > 80){
			if(JOptionPane.showConfirmDialog(null, "Attenzione " + numeroRecord + " ODL selezionati. Procedere?", "StampaODL: Verifica limiti di selezione", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION ){
				return false;
			}
			
		}
		return true;
		
	}
	
}
