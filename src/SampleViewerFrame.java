/*
 * This sample code is an example of how to use the Business Objects APIs. 
 * Because the sample code is designed for demonstration only, it is 
 * unsupported.  You are free to modify and distribute the sample code as needed.   
 */


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.crystaldecisions.ReportViewer.ReportViewerBean;
import com.crystaldecisions.sdk.occa.report.application.DatabaseController;
import com.crystaldecisions.sdk.occa.report.application.OpenReportOptions;
import com.crystaldecisions.sdk.occa.report.application.ParameterFieldController;
import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.lib.IStrings;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;



/**
 * A sample report viewer class which can load a report and display it in 
 * a ReportViewerBean embedded in a JFrame.
 */
@SuppressWarnings("serial")
public class SampleViewerFrame extends JDialog
{
    public static final String FRAME_TITLE = "Sample Report Viewer";

    /** The report viewer bean instance. */ 
    private final ReportViewerBean reportViewer;

    /** The ReportClientDocument instance being used. 
     *  Set by loadReport(). */
    private ReportClientDocument reportClientDocument;

	private String filePath;
	private String daOdl;
	private String aOdl;
	private String pathPng;
	private int tipoStampa;
     
    public SampleViewerFrame(String filePath, String url, String daOdl, String aOdl, int tipoStampa, String pathPng) {
    	//super(frmStampaOdl,true);
    	
    	this.filePath = filePath; 
    	this.daOdl=daOdl;
    	this.aOdl=aOdl;
    	this.tipoStampa=tipoStampa;
    	this.pathPng=pathPng;
    	
        setTitle (FRAME_TITLE);
        
        this.reportViewer = new ReportViewerBean();
        reportViewer.init ();

        // A menu bar can be added here if desired
        
        // Handle closing of the viewer.
        addWindowListener (new WindowAdapter ()
        {
            public void windowClosing (WindowEvent e)
            {
                closeViewer ();
            }
        });
        
        getContentPane ().add (reportViewer, BorderLayout.CENTER);

        // Set to some default size
        Insets insets = getInsets ();
        System.out.println(insets.left + " - " + insets.right + " - " + insets.top + " - " + insets.bottom);

        
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
        setSize (dim.width/4*3, dim.height/4*3);
        
		// Determine the new location of the window
		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width-w)/2;
		int y = (dim.height-h)/2;
		 
		// Move the window
		setLocation(x, y);
        
        // Show in a sensible location for the platform.
        setLocationByPlatform (true);
        
        setVisible (true);
        
        // Start the viewer
        reportViewer.start ();
        
        //ReportParameter rp = reportViewer.getReportParameter();
          
        showReport ();
        
        //printreport(filePath);
	}

	

	/**
     * Create a new instance of this viewer class and show it.
     *
     * @return the new instance of this class that was created.
     */
    /*static SampleViewerFrame showViewerFrame () 
    {
        //SampleViewerFrame viewerFrame = new SampleViewerFrame();
        viewerFrame.setVisible (true);
        
        // Start the viewer
        viewerFrame.reportViewer.start ();
        
        return viewerFrame;
    }*/

    /**
     * Entry point for this class.
     * Create and show the report viewer frame, then bind a report to it so that it can be viewed. 
     */
/*    public static void showViewer () 
    {
        SampleViewerFrame viewerFrame = showViewerFrame ();
        boolean success = viewerFrame.showReport ();
        if (!success) {
            viewerFrame.closeViewer ();
        }
    }*/
    
	/**
     * Close the viewer.
     */
    private void closeViewer ()
    {
        if (reportViewer != null)
        {
            reportViewer.stop ();
            reportViewer.destroy ();
        }
        
        removeAll ();
        dispose ();
        

    }
    
    /**
     * Load a report and show it in the viewer.
     * @return whether a report was successfully displayed.
     */
    private boolean showReport () 
    {
    	
        try
        {
            loadReport (filePath);
        	DatabaseController dbController= reportClientDocument.getDatabaseController();
            dbController.logon("sa", "ccs");
            
            //sottoreport
            IStrings subNames = reportClientDocument.getSubreportController().getSubreportNames();
            for (int subNum=0;subNum<subNames.size();subNum++){
            	System.out.println(subNames.getString(subNum));
            	dbController= reportClientDocument.getSubreportController().getSubreport(subNames.getString(subNum)).getDatabaseController();
                dbController.logon("sa", "ccs");
            	
            }
        	//loadReport ();
            
            ParameterFieldController paramController;
            paramController = reportClientDocument.getDataDefController().getParameterFieldController();
            
            //imposta parametri
            paramController.setCurrentValue("", "daOdl", Integer.valueOf(daOdl));
            paramController.setCurrentValue("", "aOdl", Integer.valueOf(aOdl));
            paramController.setCurrentValue("", "tipoStampa", Integer.valueOf(tipoStampa));
            paramController.setCurrentValue("", "pathPng", pathPng);
            
            
            if (reportClientDocument != null) {
                setDatabaseLogon ();
                setParameterFieldValues ();
                setReportSource ();
                
                return true;
            }
        }
        catch (ReportSDKException e)
        {
            String localizedMessage = e.getLocalizedMessage ();
            int errorCode = e.errorCode ();
            
            String title = "Problem showing report";
            String message = localizedMessage + "\nError code: " + errorCode;
            JOptionPane.showMessageDialog (SampleViewerFrame.this, message, title, JOptionPane.WARNING_MESSAGE);
        }
        return false;
    }
    
    private void loadReport(String filePath) throws ReportSDKException {
        String reportFilePath = filePath;
        
       // Create a new client document and use it to open the desired report.
       reportClientDocument = new ReportClientDocument ();
       reportClientDocument.setReportAppServer(ReportClientDocument.inprocConnectionString);
       reportClientDocument.open (reportFilePath, OpenReportOptions._openAsReadOnly);
       // reportClientDocument.SetDatabaseLogon("sa", "ccs");
	}

	/**
     * Determine which report to display, and use this to set the reportClientDocument field.
     * @throws ReportSDKException if there is a problem opening the specified document. 
     */
    /*
    private void loadReport () throws ReportSDKException
    {
        if (reportClientDocument == null) 
        {
            // Determine the report to display using a file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle ("Select a report to display");
            fileChooser.setFileFilter(new FileFilter() {

                @Override
                public boolean accept (File f)
                {
                    if (f != null) {
                        if (f.isDirectory()) {
                            return true;
                        }
                        return f.getName ().endsWith (".rpt");
                    }
                    return false;
                }

                @Override
                public String getDescription ()
                {
                    return "Crystal Reports (*.rpt)";
                }
            });

            int returnVal = fileChooser.showOpenDialog(SampleViewerFrame.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String reportFilePath = fileChooser.getSelectedFile().getAbsolutePath ();
               
               // Create a new client document and use it to open the desired report.
               reportClientDocument = new ReportClientDocument ();
               reportClientDocument.setReportAppServer(ReportClientDocument.inprocConnectionString);
               reportClientDocument.open (reportFilePath, OpenReportOptions._openAsReadOnly);
            }
        }
    }
    */
    
    /**
     * Set the database logon associated with the report document.
     * @throws ReportSDKException if there is a problem setting the database logon. 
     */
    private void setDatabaseLogon () throws ReportSDKException
    {
        // TODO Set up database logon here to have the report log onto the
    	// data sources defined in the report automatically, without prompting the
    	// user.  For more information about this feature, refer to the documentation.
    	// For example:
    	// CRJavaHelper.logonDataSource (reportClientDocument, "username", "password");
    	// will log onto the data sources defined in the report with username "username"
    	// and password "password".
    }
    
    /**
     * Set values for parameter fields in the report document.
     * @throws ReportSDKException if there is a problem setting parameter field values. 
     */
    private void setParameterFieldValues () throws ReportSDKException
    {
    	// TODO Populate the parameter fields in the report document here, to
    	// view the report without prompting the user for parameter values.  For
    	// more information about this feature, refer to the documentation.
    	// For example:
        //  CRJavaHelper.addDiscreteParameterValue(reportClientDocument, "subreportName", 
    	//			"parameterName", newValue);
    	// will populate the discrete parameter "parameterName" under the subreport
    	// "subreportName" with a new value (newValue).  To set a parameter in the main
    	// report, use an empty string ("") instead of "subreportName".
    }
    
    /**
     * Bind the report document to the report viewer so that the report is displayed.
     */
    private void setReportSource ()
    {
        reportViewer.setReportSource (reportClientDocument.getReportSource ());
    }
}
