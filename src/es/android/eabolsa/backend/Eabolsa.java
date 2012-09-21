package es.android.eabolsa.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

import es.android.eabolsa.searchabletick.R;

import es.android.eabolsa.searchabletick.SearchableTickers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftTagTypes;
import net.htmlparser.jericho.PHPTagTypes;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.TextExtractor;

/**
 * url donde se obtiene info. de un valor
 * URL. de imagen con parametros de analisis de un valor, vista en "DETALLE"
 * http://chart.finance.yahoo.com/z?s=GOOG&amp;t=3m&amp;q=l&amp;l=off&amp;z=l&amp;p=e20,v&amp;a=fs&amp;lang=es-ES&amp;region=ES
 * URL. de imagen comparativa de dos indices de mercado pestanha "MERCADO" otro indice de mercado europeo (^STOXX50E)
 * http://finance.yahoo.com/q/ta?t=1d&s=^IBEX&l=on&z=l&q=l&c=^FTSE
 * 
 */
public class Eabolsa {
	private Ticker tick = new Ticker();
	private Ticker tickI = new Ticker();
	private Ticker tickF = new Ticker();
	private String tickerIBEX = null;
	private String tickerFTSE = null;
	private String ticker = null;
	private Cartera cartera = null;
	private static Context context = null;

	public Eabolsa(String tickerIBEX, String tickerFTSE){
		Source source = null;
		this.tickerIBEX = tickerIBEX;
		this.tickerFTSE = tickerFTSE;
		this.tickI = getDataYahooIndice(source, "class", "yfnc_tabledata1", tickerIBEX);
		this.tickF = getDataYahooIndice(source, "class", "yfnc_tabledata1", tickerFTSE);
	}

	public Eabolsa(String ticker){
		Source source = null;
		this.ticker = ticker;
		this.tick = getDataYahooTicker(source, "class", "yfnc_tabledata1", ticker);
	}
	public Eabolsa(){
		super();
	}

	public Ticker getTicker(){
		return tick;
	}

	public Ticker getTickerIBEX(){
		return tickI;
	}

	public Ticker getTickerFTSE(){
		return tickF;
	}

	public Cartera getCartera() {
		return cartera;}

	public void setCartera(Cartera aux){
		this.cartera = aux;
	}

	public static boolean isExternalStorageReadOnly() {  
		String extStorageState = Environment.getExternalStorageState();  
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {  
			return true;  
		}  
		return false;  
	}  

	public static boolean isExternalStorageAvailable() {  
		String extStorageState = Environment.getExternalStorageState();  
		if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {  
			return true;  
		}  
		return false;  
	} 

	public static Cartera readFile(String filename) {  
		ObjectInputStream ois = null;  
		Cartera result = null;  

		if (!isExternalStorageAvailable() || isExternalStorageReadOnly())  
		{  
			Log.w("FileUtils", "Storage not available or read only");  
			return null;  
		}  

		try  
		{  
			File ruta = Environment.getExternalStorageDirectory(); 
			File file = new File(ruta, filename);  
			FileInputStream fis = new FileInputStream(file);  
			ois = new ObjectInputStream(fis);  
			result = (Cartera) ois.readObject();  
			ois.close();  
		}  
		catch (Exception ex) {  
			Log.e("FileUtils", "failed to load file", ex);  
			return null;
		}  
		finally {  
			try {if (null != ois) ois.close();} catch (IOException ex) {}  
		}  
		return result;  
	}  

	public static boolean saveFile(String fileName, Cartera cartera){

		// check if available and not read only  
		if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {  
			Log.w("FileUtils", "Storage not available or read only");  
			return false;  
		}  

		// Create a path where we will place our List of objects on external storage  
		File ruta = Environment.getExternalStorageDirectory(); 
		File file = new File(ruta, fileName);  
		//File file = new File(context.getExternalFilesDir(null),fileName);  
		ObjectOutputStream oos = null;  
		boolean success = false;  

		try {  
			OutputStream os = new FileOutputStream(file);  
			oos = new ObjectOutputStream(os);  
			oos.writeObject(cartera);  
			success = true;  
		} catch (IOException e) {  
			Log.w("FileUtils",  
					"Error writing "  
					+ file, e);  
		} catch (Exception e) {  
			Log.w("FileUtils", "Failed to save file", e);  
		} finally {  
			try {  
				if (null != oos)  
					oos.close();  
			} catch (IOException ex) {  
			}  
		}  
		return success;  
	}
	

	/**
	 * Analisis tecnico de tickers, basado en tendencias y opinion de analistas
	 * open=602.5 close=598.92 volume=9669; 603 599 9771;
	 * @param Ticker
	 */
	public static String techAnalytic(Ticker aux){
		int i=0;
		/*ANALISIS de recomendacion tecnica*/
		StringTokenizer st = new StringTokenizer(aux.getRecomendacion(), ";"); 
		double[] tech = new double[st.countTokens()];

		while(st.hasMoreTokens()) { 
			try {
				tech[i] = Double.parseDouble(st.nextToken());

			} catch (NumberFormatException doubleTry) {tech[i]=0;
			return "EAbolsa;EAB.;Análisis incompleto";}  
			i++;
		}
		if( (tech[i-2] != 0) && (tech[i-1] != tech[i-2]) && (tech[i-1] >= 1) && (tech[i-1] <= 5) ){
			if(tech[i-1] < tech[i-2])
				return tech[i-1]+" al alza;EAB.;CONDICIÓN FAVORABLE EN "+aux.getNombreVal();
			else
				return tech[i-1]+" se hunde;EAB.;CONDICIÓN ADVERSA EN "+aux.getNombreVal();
		}
		/*ANALISIS DE historico media/mediana*/
		//Para ir guardando los precios del historico y luego hacer calculos en el automata

		Vector<Double> histoClose = new Vector<Double>();
		double media = 0.0;
		double mediana = 0.0;
		double var = 0.0;
		int inicio=0;
		int fin=0;
		String resultHisto;
		StringTokenizer stHisto = new StringTokenizer(aux.getHistoData(), ";"); 
		String[] histo = new String[stHisto.countTokens()];
		i=0;
		while(stHisto.hasMoreTokens()) { 
			histo[i] = stHisto.nextToken(); 
			inicio = histo[i].indexOf(" ");
			fin = histo[i].indexOf(" ", inicio + 1);
			if(inicio != -1){
				resultHisto = histo[i].substring(inicio + 1, fin);
				/*anhadimos el cierre del dia anterior para procesarlo */
				var = Double.parseDouble(resultHisto);
				if(histoClose.isEmpty() && resultHisto != null) {
					histoClose.add(var);}
				else{
					if( (resultHisto != null) && (histoClose.lastElement() != var) )

						histoClose.add(var);
				}
				i++;
			}
		}
		int elementos = histoClose.size();
		if (elementos > 3){
			for(int j=0; j<elementos; j++){
				//Primero sumamos los precios del historico
				media=media+histoClose.elementAt(j);
			}
			//Y luego dividimos por el numero de elementos
			media=media/elementos;

			//Si hay un numero impar de elementos, la mediana...
			if(elementos%2==1) mediana = histoClose.elementAt((elementos+1)/2);
			//Si hay un numero par de elementos...
			if(elementos%2==0) mediana = (histoClose.elementAt(elementos/2) + histoClose.elementAt(elementos/2 + 1)) / 2;

			histoClose.clear();	
			/*logica del filtro*/
			if(media>=mediana) return aux.getNombreVal()+ "al alza;"+"EAB.;CONDICIÓN FAVORABLE EN "+aux.getNombreVal()+" (BUY)";
			else return aux.getNombreVal()+ "se hunde;"+"EAB.;CONDICIÓN ADVERSA EN "+aux.getNombreVal()+" (SELL)";	
		}
		return "EAbolsa;EAB.;Análisis incompleto";
		
	}

	/**
	 * Obtener datos del proveedor de contenidos financieros, crea un objeto Ticker
	 * @param Source, requisito htmlparser 
	 * @param string class
	 * @param string nombre class <td class="yfnc_tabledata1">
	 * @param url a parsear
	 * @param Ticker objeto aux de tipo Ticker
	 * @param string ticker ej. ^IBEX
	 */
	public Ticker getDataYahooIndice(Source source,String tipo, String key, String value) {
		tick = new Ticker();

		System.out.println("\nAplicando Jericho Indice:\n");
		MicrosoftTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
		MasonTagTypes.register();
		try {
			source = new Source(new URL("http://finance.yahoo.com/q?s="+value));
		} catch (MalformedURLException e) {
			System.out.println("\nERROR URL malformed Jericho:\n");
		} catch (IOException e) {
			System.out.println("\nERROR I/O Jericho:\n");
		}

		// Call fullSequentialParse manually as most of the source will be parsed.
		source.fullSequentialParse();

		for (int pos=0; pos<source.length();) {
			StartTag startTag=source.getNextStartTag(pos,tipo,key,false);
			if (startTag==null){
				tick.setNombreVal(value);
				tick.setCotizacion(tick.obtenerElementoLista(0));
				tick.setHoraCot(tick.obtenerElementoLista(1));
				tick.setCambio(tick.obtenerElementoLista(2));
				tick.setCierreAnt(tick.obtenerElementoLista(3));
				tick.setApertura(tick.obtenerElementoLista(4));
				tick.setRangoDiario(tick.obtenerElementoLista(5));
				tick.setRangoAnual(tick.obtenerElementoLista(6));
				return tick;
			}
			if (startTag.getName()==HTMLElementName.TD){
				TextExtractor extractor = new TextExtractor(startTag.getElement());
				String analCadena;
				analCadena = startTag.getElement().toString();
				if(-1 != analCadena.indexOf("yfi_quote_price")){
					if(-1 != analCadena.indexOf("yfi-price-change-up")){
						tick.addToList("+"+extractor.toString());
						tick.setColorCambio(1);
					}
					else{
						tick.addToList("-"+extractor.toString());
						tick.setColorCambio(0);
					}
				}
				else tick.addToList(extractor.toString());

			}
			pos=startTag.getEnd();
		}
		tick.setNombreVal(value);
		tick.setCotizacion(tick.obtenerElementoLista(0));
		tick.setHoraCot(tick.obtenerElementoLista(1));
		tick.setCambio(tick.obtenerElementoLista(2));
		tick.setCierreAnt(tick.obtenerElementoLista(3));
		tick.setApertura(tick.obtenerElementoLista(4));
		tick.setRangoDiario(tick.obtenerElementoLista(5));
		tick.setRangoAnual(tick.obtenerElementoLista(6));
		return tick;
	}

	/**
	 * Obtener datos del proveedor de contenidos financieros, crea un objeto Ticker
	 * @param Source, requisito htmlparser 
	 * @param string class
	 * @param string nombre class <td class="yfnc_tabledata1">
	 * @param url a parsear
	 * @param Ticker objeto aux de tipo Ticker
	 * @param string ticker ej. GOOG
	 */
	public Ticker getDataYahooTicker(Source source,String tipo, String key, String value) {
		tick = new Ticker();
		MicrosoftTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister();
		MasonTagTypes.register();
		try {
			source = new Source(new URL("http://finance.yahoo.com/q?s="+value));
		} catch (MalformedURLException e) {
			System.out.println("\nERROR URL malformed Jericho:\n");
		} catch (IOException e) {
			System.out.println("\nERROR I/O Jericho:\n");
		}

		source.fullSequentialParse();

		for (int pos=0; pos<source.length();) {
			StartTag startTag=source.getNextStartTag(pos,tipo,key,false);
			if (startTag==null){
				tick.setNombreVal(value);
				tick.setCotizacion(tick.obtenerElementoLista(0));
				tick.setHoraCot(tick.obtenerElementoLista(1));
				tick.setCambio(tick.obtenerElementoLista(2));
				tick.setCierreAnt(tick.obtenerElementoLista(3));
				tick.setApertura(tick.obtenerElementoLista(4));
				tick.setOferta(tick.obtenerElementoLista(5));
				tick.setDemanda(tick.obtenerElementoLista(6));
				tick.setObjetiv1yr(tick.obtenerElementoLista(7));
				tick.setRangoDiario(tick.obtenerElementoLista(8));
				tick.setRangoAnual(tick.obtenerElementoLista(9));
				tick.setVolumen(tick.obtenerElementoLista(10));
				tick.setVol3mes(tick.obtenerElementoLista(11));
				tick.setCapitMerc(tick.obtenerElementoLista(12));
				tick.setPrecBenef(tick.obtenerElementoLista(13));
				tick.setBpa(tick.obtenerElementoLista(14));
				tick.setRendimiento(tick.obtenerElementoLista(15));
				tick.setPrecBenef1yr(tick.obtenerElementoLista(16));
				tick.setPrecVentas(tick.obtenerElementoLista(17));
				tick.setDividendosFecha(tick.obtenerElementoLista(18));

				tick.setGananciaPorAccion(tick.obtenerElementoLista(19));
				tick.setBpaTrimestral(tick.obtenerElementoLista(20));
				tick.setRecomendPromed(tick.obtenerElementoLista(21));
				tick.setRatioEpg5yr(tick.obtenerElementoLista(22));
				String histoData=tick.getApertura()+" "+tick.getCierreAnt()+" "+tick.getVolumen();
				tick.setHistoData(histoData);
				return tick;
			}
			if (startTag.getName()==HTMLElementName.TD){
				TextExtractor extractor = new TextExtractor(startTag.getElement());
				String analCadena;
				analCadena = startTag.getElement().toString();
				if(-1 != analCadena.indexOf("yfi_quote_price")){
					if(-1 != analCadena.indexOf("yfi-price-change-up")){
						tick.addToList("+"+extractor.toString());
						tick.setColorCambio(1);
					}
					else{
						tick.addToList("-"+extractor.toString());
						tick.setColorCambio(0);
					}
				}
				else tick.addToList(extractor.toString());

			}
			pos=startTag.getEnd();
		}
		tick.setCotizacion(tick.obtenerElementoLista(0));
		tick.setHoraCot(tick.obtenerElementoLista(1));
		tick.setCambio(tick.obtenerElementoLista(2));
		tick.setCierreAnt(tick.obtenerElementoLista(3));
		tick.setApertura(tick.obtenerElementoLista(4));
		tick.setOferta(tick.obtenerElementoLista(5));
		tick.setDemanda(tick.obtenerElementoLista(6));
		tick.setObjetiv1yr(tick.obtenerElementoLista(7));
		tick.setRangoDiario(tick.obtenerElementoLista(8));
		tick.setRangoAnual(tick.obtenerElementoLista(9));
		tick.setVolumen(tick.obtenerElementoLista(10));
		tick.setVol3mes(tick.obtenerElementoLista(11));
		tick.setCapitMerc(tick.obtenerElementoLista(12));
		tick.setPrecBenef(tick.obtenerElementoLista(13));
		tick.setBpa(tick.obtenerElementoLista(14));
		tick.setRendimiento(tick.obtenerElementoLista(15));
		tick.setPrecBenef1yr(tick.obtenerElementoLista(16));
		tick.setPrecVentas(tick.obtenerElementoLista(17));
		tick.setDividendosFecha(tick.obtenerElementoLista(18));

		tick.setGananciaPorAccion(tick.obtenerElementoLista(19));
		tick.setBpaTrimestral(tick.obtenerElementoLista(20));
		tick.setRecomendPromed(tick.obtenerElementoLista(21));
		tick.setRatioEpg5yr(tick.obtenerElementoLista(22));
		String histoData=tick.getApertura()+" "+tick.getCierreAnt()+" "+tick.getVolumen();
		tick.setHistoData(histoData);
		return tick;
	}
}