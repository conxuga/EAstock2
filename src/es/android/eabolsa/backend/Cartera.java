package es.android.eabolsa.backend;

import java.io.Serializable;
import java.util.*;

public class Cartera implements Serializable{
	public List<Ticker> lista = new ArrayList<Ticker>();	
	static final long serialVersionUID = 2;

	/** numero de tickers en la cartera
	 * @return int tamanho cartera
	 */
	public int cuantosTicker() {
		return lista.size();
	}

	/** devuelve el objeto ticker al pasarle la posicion en la que se encuentra
	 * @return ticker
	 * @param int posicion
	 */
	public Ticker getTicker(int posicion) {
		Ticker tick = null;
		try{
			tick = lista.get(posicion);
		}catch(java.lang.ArrayIndexOutOfBoundsException EBE){}
		return tick; 
	}

	/** Anhade un ticker a nuestra cartera
	 * @param ticker
	 */
	public String add(Ticker ticker) {
		int comp = 1;
		Ticker aux = new Ticker();
		Iterator<Ticker> indice = iterator();
		while (indice.hasNext() && !isVacia()){
			aux = indice.next();
			comp = aux.getNombreVal().compareTo(ticker.getNombreVal()); 
		}
		if(comp != 0)
			lista.add(ticker);
		else
			return " ya se encuentra en la cartera";
		Collections.sort(lista);
		return " ha sido añadida a su cartera";
	}
	
	public void borrarTodo(){
		lista.clear();
	}

	/** elimina un ticker de la cartera al pasar su posicion (int) como parametro
	 * @param int
	 */
	public void borrar(int posicion) {
		if ( posicion <= lista.size() )
			lista.remove(posicion);
	}

	/** iterador, necesario para moverse en la lista.
	 * @return Iterator<contactoMsn>
	 */
	public Iterator<Ticker> iterator(){
		return lista.iterator();
	}

	/** devuelve true -> vacia, false -> no vacia. 
	 * @return boolean
	 */
	public boolean isVacia(){
		return lista.isEmpty();
	}
}
