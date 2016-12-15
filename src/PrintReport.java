import javax.swing.JOptionPane;

import com.businessobjects.crystalreports.printer.bean.ReportPrinter;
import com.crystaldecisions.sdk.occa.report.application.DatabaseController;
import com.crystaldecisions.sdk.occa.report.application.OpenReportOptions;
import com.crystaldecisions.sdk.occa.report.application.ParameterFieldController;
import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.lib.IStrings;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKExceptionBase;


public class PrintReport {
	
    private ReportClientDocument reportClientDocument;
    private String printer;
//	public PrintReport(String filePath, String codice, String descrizione, String documento, String fornitore ) {
//		try
//        {
//            loadReport (filePath);
//        	//DatabaseController dbController= reportClientDocument.getDatabaseController();
//        	//IConnectionInfo connInfo = dbController.getConnectionInfos(null).getConnectionInfo(0);
//
//        	//PropertyBag propertyBag = connInfo.getAttributes();
//        	//propertyBag.put("URI", url);
//        	//connInfo.setAttributes(propertyBag);
//            //connInfo.setPassword("ccs");
//            //connInfo.setUserName("sa");
//            
//            //int replaceParams = DBOptions._ignoreCurrentTableQualifiers + DBOptions._doNotVerifyDB;
//            //reportClientDocument.getDatabaseController().replaceConnection(reportClientDocument.getDatabaseController().getConnectionInfos(null).getConnectionInfo(0), connInfo, null, replaceParams);
//        	//loadReport ();
//            
//            ParameterFieldController paramController;
//            paramController = reportClientDocument.getDataDefController().getParameterFieldController();
//            
//            //imposta parametri
//            paramController.setCurrentValue("", "Codice", codice);
//            paramController.setCurrentValue("", "Descrizione", descrizione);
//            paramController.setCurrentValue("", "Documento", documento);
//            paramController.setCurrentValue("", "Fornitore", fornitore);
//            
//        }
//        catch (ReportSDKException e)
//        {
//            String localizedMessage = e.getLocalizedMessage ();
//            int errorCode = e.errorCode ();
//            
//            String message = localizedMessage + "\nError code: " + errorCode;
//            JOptionPane.showMessageDialog(null, message, "InfoBox: Bippa", JOptionPane.INFORMATION_MESSAGE);
//        }
//        
//        
//        printreport();
//    }
	
    public PrintReport(String filePath, String url, String daOdl, String aOdl, int tipoStampa, String pathPng, String printer) {
        this.printer = printer;
    	
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
            ParameterFieldController paramController;
            paramController = reportClientDocument.getDataDefController().getParameterFieldController();
            
            //imposta parametri
            paramController.setCurrentValue("", "daOdl", Integer.valueOf(daOdl));
            paramController.setCurrentValue("", "aOdl", Integer.valueOf(aOdl));
            paramController.setCurrentValue("", "tipoStampa", Integer.valueOf(tipoStampa));
            paramController.setCurrentValue("", "pathPng", pathPng);
            
        }
        catch (ReportSDKException e)
        {
          String localizedMessage = e.getLocalizedMessage ();
          int errorCode = e.errorCode ();
          
          String message = localizedMessage + "\nError code: " + errorCode;
          JOptionPane.showMessageDialog(null, message, "InfoBox: StampaOdl", JOptionPane.INFORMATION_MESSAGE);
        }
    	    	
//    	try
//        {
//            loadReport (filePath);
//        	DatabaseController dbController= reportClientDocument.getDatabaseController();
//        	IConnectionInfo connInfo = dbController.getConnectionInfos(null).getConnectionInfo(0);
//
//        	PropertyBag propertyBag = connInfo.getAttributes();
//        	propertyBag.put("URI", url);
//        	connInfo.setAttributes(propertyBag);
//            connInfo.setPassword("ccs");
//            connInfo.setUserName("sa");
//            
//            int replaceParams = DBOptions._ignoreCurrentTableQualifiers + DBOptions._doNotVerifyDB;
//            reportClientDocument.getDatabaseController().replaceConnection(reportClientDocument.getDatabaseController().getConnectionInfos(null).getConnectionInfo(0), connInfo, null, replaceParams);
//        	//loadReport ();
//            
//            ParameterFieldController paramController;
//            paramController = reportClientDocument.getDataDefController().getParameterFieldController();
//            
//            //imposta parametri
//            paramController.setCurrentValue("", "daOdl", Integer.valueOf(daOdl));
//            paramController.setCurrentValue("", "aOdl", Integer.valueOf(aOdl));
//        }
//        
//        catch (ReportSDKException e)
//        {
//            String localizedMessage = e.getLocalizedMessage ();
//            int errorCode = e.errorCode ();
//            
//            String message = localizedMessage + "\nError code: " + errorCode;
//            JOptionPane.showMessageDialog(null, message, "InfoBox: StampaOdl", JOptionPane.INFORMATION_MESSAGE);
//        }
        
        
        printreport();
	}

	private void loadReport(String filePath) throws ReportSDKException {
        String reportFilePath = filePath;
        
       // Create a new client document and use it to open the desired report.
       reportClientDocument = new ReportClientDocument ();
       reportClientDocument.setReportAppServer(ReportClientDocument.inprocConnectionString);
       reportClientDocument.open (reportFilePath, OpenReportOptions._openAsReadOnly);
	}


	private void printreport() {
        ReportPrinter rp = new ReportPrinter();
        rp.setReportSource(reportClientDocument.getReportSource());
        try {
        	// TODO Definire Stampante
            rp.setPrinterName(printer);
            rp.print();
 	} catch (ReportSDKExceptionBase e) {
 		// TODO Auto-generated catch block
 		e.printStackTrace();
 	}		
	}
}
