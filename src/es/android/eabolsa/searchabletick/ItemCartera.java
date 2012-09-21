package es.android.eabolsa.searchabletick;

public class ItemCartera {

	private String nombre;
	private String valor;
	private String cambio;
	private String empresa;
	private int color;

	public ItemCartera(String nombre, String valor, String cambio, String empresa, int color) {
		this.nombre = nombre;
		this.valor = valor;
		this.cambio = cambio;
		this.empresa = empresa;
		this.color = color;
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
	
	public String getEmpresa() {
		return empresa;
	}
	
	public int getColor(){
		return color;
	}

}
