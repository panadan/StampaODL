


import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class DataManager {
	private Connection connection;
	private String server;
	private String db;
	
	
	private String inPath;
	private String outPath;
	private String gsCMD;
	private String url;
	private String stampanteFR;
	private JLabel lblInfo;
	
 	public static void main(String[] args)
	 {
	   new DataManager(null).doTest();
	 }

	private void doTest() {
		connect();
		close();		
	}

	
	public DataManager(JLabel lblInfo){
		super();
//		try {
//			redirectSystemOut();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		this.lblInfo = lblInfo;
		readIni();
		//testOpenImg();
	}
	
	public void testOpenImg() {
		String imagePath = new String("c:/vert/StampaODL/").replace("/", "\\"); 
		BufferedImage img = null;
		File imageFile = new File(imagePath + "vuoto" +".png");
		
		if(!imageFile.exists()) JOptionPane.showMessageDialog(null, "Il file vuoto png non esiste", "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
		else JOptionPane.showMessageDialog(null, "Il file vuoto png esiste", "StampaODL: Ok!", JOptionPane.INFORMATION_MESSAGE);
		
		try {
			img = ImageIO.read(imageFile) ;
			img.flush();
		    img = null;
			JOptionPane.showMessageDialog(null, "Letta Immagine", "StampaODL: Ok!", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Problema immagine:" + e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Problema immagine eccezione generica:" + e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
		} finally {
			JOptionPane.showMessageDialog(null, "Problema immagine:" + "NESSUNA ECCEZIONE LANCIATA", "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);

		}
		
	}

	private void readIni() {
		Properties ini;
		ini = new Properties();
		//dafault settings
		server="win2008-sql";
		db="NUOVAOMEC";
		inPath = "f:\\scambio\\ut\\disegni_rilasciati\\";
		outPath = "c:\\tmp\\";
		gsCMD = "\"C:\\Program Files\\gs\\gs9.10\\bin\\gswin64c.exe\" -dSAFER -dBATCH -dNOPAUSE -sDEVICE=pngalpha -r300 -sOutputFile=";
		
		try {
			ini.load(new FileInputStream("StampaODL.ini"));
			server=ini.getProperty("server");
			db = ini.getProperty("db");
			inPath = ini.getProperty("CartellaPDF");
			outPath = ini.getProperty("CartellaPNG");
			gsCMD = ini.getProperty("GsCMD");
			stampanteFR= ini.getProperty("StampanteFR");
			System.out.println("server= "+ server);
			System.out.println("db= " + db);
			//System.out.println("server= " + ini.getProperty("server"));
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
		}		
	}
	
	
	public void redirectSystemOut() throws FileNotFoundException {
		//System.out.println("This goes to the console");
		PrintStream console = System.out;

		File file = new File("out.txt");
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
		System.setOut(ps);
	}
	
	public String getStampanteFR() {
		return stampanteFR;
	}

	public void close(){
		try {
			connection.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return connection;
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	    	return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	public String connect(){

	
		//Connection connection = null;
		try
		{
			// the sql server driver string
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			// the sql server url
			url = "jdbc:sqlserver://" + server + ";DatabaseName=" + db;

			// get the sql server database connection
			connection = DriverManager.getConnection(url,"sa", "ccs");
			return "svr: " + server + " / db: " + db;

		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
			System.exit(1);
			return "non connesso. problema con il driver";
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
			System.exit(2);
			return "non connesso";
		}
	}

	private boolean generaImmagini(String daOdl, String aOdl, int tipoStampa) {
		String codice;
		boolean nonVuoto;
		//elimina jpg obsoleti (piu vecchi di 1 giorno
		
		//interroga db per ottenere i codici articoli
		
		String query = "SELECT DO11_DOCUM_MG36, DO30_CODART_MG66, DO11_NUMDOC, DO30_PROGRIGA, DO30_DESCART, DO11_DATADOC, DO11_CLIFOR_CG44, DO11_NUMREG_CO99";
		query += " FROM   DO30_DOCCORPO INNER JOIN DO11_DOCTESTATA ON (DO30_DITTA_CG18=DO11_DITTA_CG18) AND (DO30_NUMREG_CO99=DO11_NUMREG_CO99)";
		//query += " LEFT JOIN DO46_DOCCORORDDET ON (DO30_DITTA_CG18=DO46_DITTA_CG18) AND (DO30_NUMREG_CO99=DO46_NUMREG_CO99) AND (DO30_PROGRIGA=DO46_PROGRIGA)";
		query += " WHERE DO11_DOCUM_MG36='P-ODL' AND DO11_NUMDOC BETWEEN " + daOdl + " AND " + aOdl;
		//verisone solo ordine a fornitore
		//query += " DO30_PROGRIGA=" + new Integer(rigaNum) + " AND DO11_NUMREG_CO99='" + docReg +  "' AND (DO11_DOCUM_MG36='F-OA' OR DO11_DOCUM_MG36='F-OPR')";
		
		//UPDATE DO46_DOCCORORDDET  SET DO46_FLGSTORDMONO = 1, DO46_INDSTATOORD = 1  
		//WHERE DO46_DITTA_CG18 = 1  AND DO46_NUMREG_CO99 = '201300048808' AND DO46_PROGRIGA = '1'
		
		String query_stampati = " AND exists(select 'A' from DO46_DOCCORORDDET";
		query_stampati += " WHERE DO30_DOCCORPO.DO30_DITTA_CG18 = DO46_DOCCORORDDET.DO46_DITTA_CG18";
		query_stampati += " AND DO30_DOCCORPO.DO30_NUMREG_CO99 = DO46_DOCCORORDDET.DO46_NUMREG_CO99";
		query_stampati += " AND DO30_DOCCORPO.DO30_PROGRIGA = DO46_DOCCORORDDET.DO46_PROGRIGA";
		
		//solo non ancora stampati
		if(tipoStampa == 0)
			query += query_stampati + " AND DO46_FLGSTORDMONO = 0)";
		//solo già ancora stampati
		if(tipoStampa == 1)
			query += query_stampati + " AND DO46_FLGSTORDMONO = 1)";
		
		System.out.println(query);
		
		
		Statement st;
		nonVuoto = false;
		try {
			st = connection.createStatement();
			ResultSet rs = st.executeQuery(query);
			
			while (rs.next())
			{
				nonVuoto = true;
				codice = rs.getString("DO30_CODART_MG66").trim();

				//per ogni codice articolo produci il jpg
				try {
					System.out.println("ODL num: " +  rs.getInt("DO11_NUMDOC"));
					System.out.println("f:\\scambio\\ut\\disegni_rilasciati\\" + codice + ".pdf");
					lblInfo.setText("Elaborazione disegno: " +codice);
					creaPngBianco(codice);
					pdfToPng(codice);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
					e.printStackTrace();
				}
				
			}
			lblInfo.setText(">");
			return nonVuoto;
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
			return nonVuoto;
		}
	}
	
	private void creaPngBianco(String codice) throws IOException {
		File cfgFilePath = new File("vuoto.png");
		
		Path from = cfgFilePath.toPath(); //convert from File to Path
		Path to = Paths.get(outPath + codice + ".png"); //convert from String to Path
		Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
		
	}

	private void pdfToPng(String codice){
	    String cmd = gsCMD + outPath + codice + ".png " + inPath + codice + ".pdf";
	    System.out.println(cmd);
	    
	    Runtime run = Runtime.getRuntime();
		Process pr;
		try {
			pr = run.exec(cmd);
	
			pr.waitFor();
			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			while ((line=buf.readLine())!=null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
		}   
		
		lblInfo.setText("Creato png: " +codice);
		
		try {
			String imagePath = outPath.replace("/", "\\"); 
			BufferedImage img = null;
			File imageFile = new File(imagePath + codice+".png");

			if(!imageFile.exists()) {JOptionPane.showMessageDialog(null, "funz2,2? IL file non esiste", "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);}
			else System.out.println(imageFile.getAbsolutePath());
			
			img = ImageIO.read(imageFile) ;
			
			lblInfo.setText("Rotazione " +codice + " inizio");
			img = createRotatedCopy(img);
			lblInfo.setText("Rotazione " +codice + " fine");
			

		    
		    if (img!=null){
			    try {
			        // retrieve image
			        ImageIO.write(img, "png", imageFile);
					img.flush();
				    img = null;
			    } catch (IOException e) {
			    	JOptionPane.showMessageDialog(null, e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
			    	e.printStackTrace();
			    
			    } catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
					e.printStackTrace();
				}	    
			  }
		    
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
		} finally {
			//JOptionPane.showMessageDialog(null, "funz5 finally?", "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public boolean preparaOdl(String daOdl, String aOdl, int tipoStampa) {
		EliminaFileVecchi();
		return generaImmagini(daOdl, aOdl, tipoStampa);
	}

	private void EliminaFileVecchi() {
		File fin = new File(outPath);
		for (File file : fin.listFiles()) {
			long diff = new Date().getTime() - file.lastModified();
			if (diff > 1 * 24 * 60 * 60 * 1000)
			    file.delete();
			
		}   
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		return url;
	}

	private BufferedImage createRotatedCopy(BufferedImage img) throws IOException {
		int w = img.getWidth();
	    int h = img.getHeight();

	    //se giè portrait esci
	    if (w<h) return null;
	    
	    
	    
	    BufferedImage rot = new BufferedImage(h, w, BufferedImage.TYPE_INT_RGB);
	    double theta;

//	        case CLOCKWISE:
//	            theta = Math.PI / 2;
//	            break;
//	        case COUNTERCLOCKWISE:
	            theta = -Math.PI / 2;
//	            break;
//	        default:
//	            throw new AssertionError();
//	    }

	    AffineTransform xform = new AffineTransform();
	    xform.translate(0.5*h, 0.5*w);
	    xform.rotate(theta);
	    xform.translate(-0.5*w, -0.5*h);
	    Graphics2D g = (Graphics2D) rot.createGraphics();
	    g.drawImage(img, xform, null);
	    g.dispose();

	    return rot;
	}

	public void aggiornaStato(String daOdl, String aOdl) {
		String query =  "UPDATE DO46_DOCCORORDDET  SET DO46_FLGSTORDMONO = 1, DO46_INDSTATOORD = 1";
		//String query =  "UPDATE DO46_DOCCORORDDET  SET DO46_FLGSTORDMONO = 1";
		query += " FROM   DO30_DOCCORPO INNER JOIN DO11_DOCTESTATA ON (DO30_DITTA_CG18=DO11_DITTA_CG18) AND (DO30_NUMREG_CO99=DO11_NUMREG_CO99)";
		query += " LEFT JOIN DO46_DOCCORORDDET ON (DO30_DITTA_CG18=DO46_DITTA_CG18) AND (DO30_NUMREG_CO99=DO46_NUMREG_CO99) AND (DO30_PROGRIGA=DO46_PROGRIGA)";
		query += " WHERE DO11_DOCUM_MG36='P-ODL' AND DO11_NUMDOC BETWEEN " + daOdl + " AND " + aOdl;
		query += " AND DO46_FLGSTORDMONO = 0";
		
		Statement st;
		try {
			st = connection.createStatement();
			st.executeUpdate(query);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "StampaODL: Errore!", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
		}
		
		//WHERE DO46_DITTA_CG18 = 1  AND DO46_NUMREG_CO99 = '201300048808' AND DO46_PROGRIGA = '1'
		
	}

	public String getPathPng() {
		return outPath;
	}

}
