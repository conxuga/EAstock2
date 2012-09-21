package es.android.eabolsa.searchabletick;

public class ItemMercado {

	private String nombre;
	private String valor;
	private String cambio;

	public ItemMercado(String nombre, String valor, String cambio) {
		this.nombre = nombre;
		this.valor = valor;
		this.cambio = cambio;
	}

	public String getNombre() {
		return nombre;
	}

	public String getValor() {
		return valor;
	}

	public String getCambio() {
		return cambio;
	}

}
