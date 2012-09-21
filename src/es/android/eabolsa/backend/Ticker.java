/**
 * __________Variables a tener en cuenta para cada valor-stock-accion
 * nombreValor GOOG
 * cotizacion 598.20
 * horaCotizacion 12:35PM
 * cambioValor + o - 2.80 0.47%
 * cierreAnterior 601.00
 * apertura 598.00
 * oferta XX x 100
 * demanda XX x 200
 * objetivo 1yr 680.52
 * rangoDiario xx - xx
 * rango52semanas (1anho) rango XX - XX
 * volumen 567,567
 * volumen 3meses 3,345,345
 * capitalMercado 191.20B
 * precBenef (pe)(precio/beneficio) 24.30
 * bpa(eps) 24.62
 * rendimiento N/A
 * ________________________KEY STATISTICS
 * precBenef1yr (peAnticipado 1anho) 17.84
 * precioVentas (ps)(precio/ventas) 6.97
 * dividendosFecha 16-Nov-95 o N/A
 * ________________________ANALYSTS
 * GananciaPorAccion (AnualEps) 28.83
 * BpaTrimestral (QuarterlyEps) 8.05
 * RecomendPromed (1.0 a 5.0) 1.8
 * RatioEpg5yr (5anhos) 1.18
 */

package es.android.eabolsa.backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import es.android.eabolsa.searchabletick.SearchableTickers;

public class Ticker implements Serializable, Comparable<Ticker>{	
	private static final long serialVersionUID = 1L;
	private String nombreValor;
	private String nombreEmpresa;
	private String cotizacion;
	private String horaCotizacion;
	private String cambioValor;
	private int color;
	private String cierreAnt;
	private String apertura;
	private String oferta;
	private String demanda;
	private String objetivo1yr;
	private String rangoDiario;
	private String rango52seman;
	private String vol;
	private String vol3mes;
	private String capitMercado;
	private String precBenef;
	private String bpa;
	private String rendimiento;

	private String precBenef1yr;
	private String precVentas;
	private String dividendosFecha;

	private String gananciaXaccion;
	private String bpaTrimestral;
	private String recomendPromedio;
	private String ratioEpg5yr;

	private String histoData;

	/*
	 * -1 si no se han comprado acciones
	 * XXXXX si se han comprado X acciones
	 */
	private int comprado; 
	private String mercado;
	List<String> lista = new ArrayList<String>();
	private int contador=0;

	public Ticker(){
		super();
	}

	/**
	 * Crea un objeto Ticker-Stock-Trade con los parametros pasados.
	 * @param nombreValor GOOG
	 * @param cotizacion 600.20
	 */
	public Ticker(String nombreValor, 
			String cotizacion,
			String horaCotizacion,
			String cambioValor,
			String cambioPorcentaje,
			String cierreAnt,
			String apertura,
			String oferta,
			String demanda,
			String objetivo1yr,
			String rangoDiario,
			String rango52seman,
			String vol,
			String vol3mes,
			String capitMercado,
			String precBenef,
			String bpa,
			String rendimiento,

			String precBenef1yr,
			String precVentas,
			String dividendosFecha,

			String gananciaXaccion,
			String bpaTrimestral,
			String recomendPromedio,
			String ratioEpg5yr,

			String graficaTicker,

			String histoData,
			String histoOpen,
			String histoHigh,
			String histoLow,
			String histoClose,
			String histoVolume,
			String histoCount,
			String histoWap,
			boolean histoGap,
			int comprado,
			int color,
			String mercado) {
		this.nombreValor = nombreValor;
		this.cotizacion = cotizacion;
		this.horaCotizacion = horaCotizacion;
		this.cambioValor = cambioValor;
		this.color = color;
		this.cierreAnt = cierreAnt;
		this.apertura = apertura;
		this.oferta = oferta;
		this.demanda = demanda;
		this.objetivo1yr = objetivo1yr;
		this.rangoDiario = rangoDiario;
		this.rango52seman = rango52seman;
		this.vol = vol;
		this.vol3mes = vol3mes;
		this.capitMercado = capitMercado;
		this.precBenef = precBenef;
		this.bpa = bpa;
		this.rendimiento = rendimiento;

		this.precBenef1yr = precBenef1yr;
		this.precVentas = precVentas;
		this.dividendosFecha = dividendosFecha;

		this.gananciaXaccion = gananciaXaccion;
		this.bpaTrimestral = bpaTrimestral;
		this.recomendPromedio = recomendPromedio;
		this.ratioEpg5yr = ratioEpg5yr;

		this.histoData = histoData;

		this.comprado = comprado;
		this.mercado = mercado;
	}

	/** M�todo que devuelve el n�mero de contactos en el almac�n
	 * @return contador 
	 */
	public int cuantosEnLista() {
		return contador;
	}

	/** M�todo que pas�ndole la posici�n de un contacto, devuelve el objeto que contiene toda la informaci�n del contacto
	 * @return contador
	 * @param posicion 
	 */
	public String obtenerElementoLista(int posicion) {
		return lista.get(posicion); 
	}

	/** M�todo que pas�ndole un contacto, lo introduce en el almac�n e incrementa la cantidad de contactos
	 * @param usuario
	 */
	public void addToList(String aux) {
		lista.add(aux);
	}
	/** M�todo que pas�ndole un contacto, lo introduce en el almac�n e incrementa la cantidad de contactos
	 * @param usuario
	 */
	public void boorarList() {
		lista.clear();
	}

	public void setMercado(String mercad){
		this.mercado = mercad;
	}
	public String getMercado(){
		return mercado;
	}
	public void setComprado(int compra){
		this.comprado = compra;
	}
	public int getComprado(){
		return comprado;
	}

	public void setNombreVal(String nombre) {
		this.nombreValor = nombre;
	}

	public void setCotizacion(String cotizacion) {
		this.cotizacion = cotizacion;
	}
	public void setHoraCot(String hora) {
		this.horaCotizacion = hora;
	}
	public void setCambio(String cambio) {
		this.cambioValor = cambio;
	}
	public void setColorCambio(int color){
		this.color = color;
	}
	public void setCierreAnt(String cierre) {
		this.cierreAnt = cierre;
	}
	public void setApertura(String apert) {
		this.apertura = apert;
	}
	public void setOferta(String oferta) {
		this.oferta = oferta;
	}
	public void setDemanda(String demanda) {
		this.demanda = demanda;
	}
	public void setObjetiv1yr(String objetivo) {
		this.objetivo1yr = objetivo;
	}
	public void setRangoDiario(String rangoDiario) {
		this.rangoDiario = rangoDiario;
	}
	public void setRangoAnual(String rangoAnual) {
		this.rango52seman = rangoAnual;
	}
	public void setVolumen(String volumen) {
		this.vol = volumen;
	}
	public void setVol3mes(String vol3m) {
		this.vol3mes = vol3m;
	}
	public void setCapitMerc(String capital) {
		this.capitMercado = capital;
	}
	public void setPrecBenef(String pe) {
		this.precBenef = pe;
	}
	public void setBpa(String bpa) {
		this.bpa = bpa;
	}
	public void setRendimiento(String rendimiento) {
		this.rendimiento = rendimiento;
	}

	public void setPrecBenef1yr(String pe1yr) {
		this.precBenef1yr = pe1yr;
	}
	public void setPrecVentas(String ps) {
		this.precVentas = ps;
	}
	public void setDividendosFecha(String fechaDivid) {
		this.dividendosFecha = fechaDivid;
	}

	public void setGananciaPorAccion(String eps) {
		this.gananciaXaccion = eps;
	}
	public void setBpaTrimestral(String bpaTrimest) {
		this.bpaTrimestral = bpaTrimest;
	}
	public void setRecomendPromed(String recomendacion) {
		this.recomendPromedio=this.recomendPromedio+";"+recomendacion;
	}
	public void setRatioEpg5yr(String ratioEpg5yr) {
		this.ratioEpg5yr = ratioEpg5yr;
	}

	/**
	 * open=602.5 high=603.87 low=598.01 close=598.92 volume=9669
	 * @param histoData
	 */
	public void setHistoData(String histoData) {
		this.histoData = this.histoData+";"+histoData;
	}

	public String getNombreVal() {
		return nombreValor;
	}
	public String getCotizacion() {
		return cotizacion;
	}
	public String getHoraCot() {
		return horaCotizacion;
	}
	public String getCambioValor() {
		return cambioValor;
	}

	/**
	 * Devuelve un entero que identifica el color rojo(0) o verde(1) si el cambio del valor es positivo o negativo
	 * 0 - negativo -0.09
	 * 1 + positivo +1.6
	 * @return color 0 negativo, 1 positivo
	 */
	public int getColorCambio(){
		return color;
	}
	public String getCierreAnt() {
		return cierreAnt;
	}
	public String getApertura() {
		return apertura;
	}
	public String getOferta() {
		return oferta;
	}
	public String getDemanda() {
		return demanda;
	}
	public String getObjetivo1yr() {
		return objetivo1yr;
	}
	public String getRangoDiario() {
		return rangoDiario;
	}
	public String getRangoAnual() {
		return rango52seman;
	}
	public String getVolumen() {
		return vol;
	}
	public String getVol3mes() {
		return vol3mes;
	}
	public String getCapitMerc() {
		return capitMercado;}
	public String getPrecioBenef() {
		return precBenef;}
	public String getBpa() {
		return bpa;}
	public String getRendimiento() {
		return rendimiento;}
	public String getPrecBenef1yr() {
		return precBenef1yr;}
	public String getPrecioVentas() {
		return precVentas;}
	public String getDividFecha() {
		return dividendosFecha;
	}

	public String getGananciaPorAccion() {
		return gananciaXaccion;}
	public String getBpaTrimest() {
		return bpaTrimestral;}
	public String getRecomendacion() {
		return recomendPromedio;}
	public String getRatioEpg5yr() {
		return ratioEpg5yr;}

	/**
	 * http://chart.finance.yahoo.com/z?s=jaz.MC&amp;t=1m&amp;q=l&amp;l=off&amp;z=l&amp;p=e20,v&amp;a=fs&amp;lang=es-ES&amp;region=ES
	 *uri grafica de un ticker, los periodos son: 1d, 5d, 3m, 6m, 1y, my
	 *@param ticker
	 *@param periodo
	 * */
	public String getGraficaTicker(String ticker, String periodo) {
		return "http://chart.finance.yahoo.com/z?s="+ticker+"&amp;t="+periodo+"&amp;q=l&amp;l=off&amp;z=l&amp;p=e20,v&amp;a=fs&amp;lang=es-ES&amp;region=ES.png";
	}

	public String getHistoData() {
		return histoData;}

	/**
	 * Permite convertir en cadena y obtener datos significativos. 
	 * @return String
	 */
	public String toString(){
		return 
		"VALOR          : "+getNombreVal()+"\n"+
		"COTIZACION     : "+getCotizacion()+"\n"+
		"HORA COTIZ     : "+getHoraCot()+"\n"+
		"CAMBIO         : "+getCambioValor()+"\n"+
		"CIERRE ANTER   : "+getCierreAnt()+"\n"+
		"APERTURA       : "+getApertura()+"\n"+
		"OFERTA         : "+getOferta()+"\n"+
		"DEMANDA        : "+getDemanda()+"\n"+
		"RANGO DIARIO   : "+getRangoDiario()+"\n"+
		"RANGO ANUAL    : "+getRangoAnual()+"\n"+
		"VOLUMEN        : "+getVolumen()+"\n"+
		"VOLUMEN 3m     : "+getVol3mes()+"\n"+
		"GANANCIAxACCION: "+getGananciaPorAccion()+"\n"+
		"RENDIMIENTO    : "+getRendimiento()+"\n"+
		"RECOMENDACION  : "+getRecomendacion()+" 1.0 muy recomendado, 5.0 no recomendado\n";

	}

	/**
	 * M�todo que permite comparar si dos tickers son iguales segun su nombre ej. GOOG, pasamos como par�metro un objeto de tipo Ticker. 
	 * @param object
	 * @return boolean
	 */
	public boolean equals (Object valorAux){
		if (!(valorAux instanceof Ticker))
			return (false);
		Ticker ticker=(Ticker)valorAux;
		return(ticker.getNombreVal().equals(getNombreVal()));
	}

	public int hashCode(){
		return 31*getNombreVal().hashCode();
	}

	/**
	 * Permite ordenar por ticker. 
	 * @param Ticker objeto
	 * ORDEN: inverso = 2 para orden inverso del cambio (*-1), 0 orden natural nombre, 1 orden natural del cambio 
	 * @return int
	 */
	public int compareTo(Ticker aux) {
		int i = 0;
		StringTokenizer stCambio = new StringTokenizer(getCambioValor()," "); 
		StringTokenizer stCambioAux = new StringTokenizer(aux.getCambioValor()," ");
		switch(SearchableTickers.ORDEN)
		{
		case 1://orden=1 cambio valor positivo
			try{
				i = Double.compare(Double.parseDouble(stCambio.nextToken()), Double.parseDouble(stCambioAux.nextToken()))*-1;
				System.out.println("result comparacion doubles: "+i);
			}catch(java.lang.NullPointerException NPE){
				i=0;
			}
			try{ 
				if( i != 0)
					stCambio.nextToken().compareTo(stCambioAux.nextToken());
			}
			catch(java.lang.NullPointerException NPE){
				i=1;
			}
			break;

		case 2://orden=2 cambio valor negativo
			try{
				i = Double.compare(Double.parseDouble(stCambio.nextToken()), Double.parseDouble(stCambioAux.nextToken()));
				System.out.println("result comparacion doubles: "+i);
			}catch(java.lang.NullPointerException NPE){
				i=0;
			}
			try{ 
				if( i != 0){
					stCambio.nextToken().compareTo(stCambioAux.nextToken());
				}
			}
			catch(java.lang.NullPointerException NPE){
				i=1;
			}
			break;

		default://orden=0 nombre valor normal
			try{
				i = getNombreVal().compareTo(aux.getNombreVal());		
			}catch(java.lang.NullPointerException NPE){
				i=0;
			}
			try{ 
				if( i != 0)
					getNombreVal().compareTo(aux.getNombreVal());
			}
			catch(java.lang.NullPointerException NPE){
				i=1;
			}

		}; 
		return i;
	}

	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}

	public String getNombreEmpresa() {
		return nombreEmpresa;
	}
}




