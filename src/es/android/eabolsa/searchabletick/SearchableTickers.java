package es.android.eabolsa.searchabletick;

/**
 * TIPS:
 * en android manifest, para evitar que se vuelva a recargar onCreate
 * android:configChanges="orientation|keyboardHidden"
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;


import es.android.eabolsa.searchabletick.R;

import es.android.eabolsa.backend.Cartera;
import es.android.eabolsa.backend.Eabolsa;
import es.android.eabolsa.backend.Ticker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.widget.TabHost.TabContentFactory;

/**
 * The main activity for the dictionary. Displays search results triggered by
 * the search dialog and handles actions from search suggestions.
 */
public class SearchableTickers extends TabActivity implements
OnTabChangeListener {

	public static int ORDEN = 1;

	private TextView mTextView;
	private ListView listaMercado;
	private ListView listaBuscador;

	private Resources mResources;

	// Tab about
	private LinearLayout linearLayoutAbout;
	private ImageView iAbout;

	// Tab mercados
	private LinearLayout linearLayoutMercados;
	private ImageView graficaMercados;


	// menu contextual
	public static final int DELETE_ID = Menu.FIRST;
	public static final int DELETE_TODO = 0x02;

	// dialogos
	private AlertDialog.Builder alt_todas;
	private AlertDialog.Builder alt_una;

	//vista en detalle
	ImageView imageView;

	//cartera
	String resultado;
	Cartera cart = new Cartera();
	Eabolsa eaC = null;
	Eabolsa ea;
	Ticker tickIBEX;
	Ticker tickFTSE;
	Ticker tick;

	//SearchProvider
	String nombreSearchProvider = null;

	private static final String PREF_STICKY_TAB = "stickyTab";

	private static final String LIST1_TAB_TAG = "Mercados";
	private static final String LIST2_TAB_TAG = "Cartera";
	private static final String TAB_TAG = "Acerca de..";
	private ListView listaCartera;
	private TabHost tabHost;
	private Bitmap loadedImage;
	private String imageHttpAddress = "http://chart.finance.yahoo.com/z?s=^IBEX&amp;t=1d&amp;q=l&amp;l=on&amp;z=l&amp;c=^FTSE&amp;p=s&amp;a=v&amp;p=s&amp;lang=en-US&amp;region=US.png";
	private Timer timer;

	public Bitmap downloadFile(String imageHttpAddress) {
		URL imageUrl = null;

		try {
			imageUrl = new URL(imageHttpAddress);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
			.openConnection();
			conn.connect();
			loadedImage = BitmapFactory.decodeStream(conn.getInputStream());
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(),
					"Error cargando la imagen: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		return loadedImage;
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getNetworkInfo(0).isConnectedOrConnecting())
			return true;
		if (cm.getNetworkInfo(1).isConnectedOrConnecting())
			return true;
		return false;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		timer = new Timer();
		//mercados
		mResources = getResources();
		// tab about
		linearLayoutAbout = new LinearLayout(this);
		iAbout = new ImageView(this);
		// tab mercados
		linearLayoutMercados = new LinearLayout(this);
		graficaMercados = new ImageView(this);
		tabHost = getTabHost();
		tabHost.setOnTabChangedListener((OnTabChangeListener) this);

		listaCartera = (ListView) findViewById(R.id.list2);
		if (isOnline() ){
			ea = new Eabolsa("^IBEX", "^FTSE");
			// configuramos la vista de la lista tab 1
			listaMercado = (ListView) findViewById(R.id.list1);
			// esta lista estara vacia inicialmente
			formarListaCartera();

			tabHost.addTab(tabHost
					.newTabSpec(LIST2_TAB_TAG)
					.setIndicator(LIST2_TAB_TAG,
							mResources.getDrawable(R.drawable.ic_tab_cartera))
							.setContent(new TabContentFactory() {
								public View createTabContent(String arg0) {
									return listaCartera;
								}
							}));

			// Anhadimos las vistas de mercados al tabhost
			tabHost.addTab(tabHost
					.newTabSpec(LIST1_TAB_TAG)
					.setIndicator(LIST1_TAB_TAG,
							mResources.getDrawable(R.drawable.ic_tab_mercados))
							.setContent(new TabContentFactory() {
								public View createTabContent(String arg0) {

									// Lista mercados con los dos item -> Anhadir
									return listaMercado;
								}
							}));
		}
		else{
			Toast.makeText(SearchableTickers.this,"Es necesaria una conexión de datos", Toast.LENGTH_SHORT).show();
			finish();
		}

		// listener TAB cartera, muestra la vista en detalle de un ticker 
		listaCartera.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view,
					int position, long id) {
				//Pasamos el nombre del ticker
				String tickerNombre=null;

				if(!cart.isVacia())
					tickerNombre=cart.getTicker(position).getNombreVal();
				else
					tickerNombre="GOOG";
				Intent intentDetalle = new Intent(SearchableTickers.this, VistaDetalle.class);
				intentDetalle.putExtra("tickerNombre",tickerNombre);
				startActivity(intentDetalle);
			}
		});

		tabHost.addTab(tabHost
				.newTabSpec(TAB_TAG)
				.setIndicator(TAB_TAG,
						mResources.getDrawable(R.drawable.ic_tab_about))
						.setContent(new TabContentFactory() {
							public View createTabContent(String arg0) {
								iAbout.setImageResource(R.drawable.fondo);
								iAbout.setAdjustViewBounds(true);
								iAbout.setLayoutParams(new Gallery.LayoutParams(
										LayoutParams.FILL_PARENT,
										LayoutParams.FILL_PARENT));
								linearLayoutAbout.addView(iAbout);

								linearLayoutAbout.setBackgroundColor(mResources
										.getColor(R.color.blanco));
								return (linearLayoutAbout);
							}
						}));
		Intent intent = getIntent();

		/*
		 * Accion sobre el elemento item de la lista del buscador, nos llevara
		 * al activity cartera
		 */
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Uri uri = getIntent().getData();
			Cursor cursor = managedQuery(uri, null, null, null, null);

			if (cursor == null) {
				finish();
			} else {
				cursor.moveToFirst();
				int wIndex = cursor.getColumnIndexOrThrow(TickerDatabase.KEY_WORD);
				int dIndex = cursor.getColumnIndexOrThrow(TickerDatabase.KEY_DEFINITION);

				//Obtenemos ticker y nombre empresa a partir del search provider
				this.nombreSearchProvider=cursor.getString(wIndex)+"\n"+cursor.getString(dIndex);
				BufferedReader reader = new BufferedReader(
						new StringReader(nombreSearchProvider));
				try{
					Eabolsa eaC = new Eabolsa(reader.readLine());
					tick = eaC.getTicker();
					tick.setNombreEmpresa(cursor.getString(dIndex));
					Cartera cartera ;
					cartera = Eabolsa.readFile("data.txt");
					if(cartera != null){
						resultado = cartera.add(tick);
						Eabolsa.saveFile("data.txt", cartera);
					}else{
						cartera = new Cartera();
						resultado = cartera.add(tick);
						Eabolsa.saveFile("data.txt", cartera);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
				Toast.makeText(SearchableTickers.this,
						cursor.getString(dIndex)+resultado, Toast.LENGTH_SHORT).show();
			}
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// handles a search query
			String query = intent.getStringExtra(SearchManager.QUERY);
			showResults(query);
		}

		// Menu contextual
		registerForContextMenu(listaCartera);
	}

	/**
	 * Upon being resumed we can retrieve the current state.  This allows us
	 * to update the state if it was changed at any time while paused.
	 */
	protected void onResume() {
		super.onResume();
		cart = Eabolsa.readFile("data.txt");
		if(cart != null && !cart.isVacia())
			System.out.println("ONRESUME: "+cart.cuantosTicker());
		formarListaCartera();
	}

	/**
	 * Any time we are paused we need to save away the current state, so it
	 * will be restored correctly when we are resumed.
	 */
	protected void onPause() {
		super.onPause();

		if(cart != null && !cart.isVacia()){
			Eabolsa.saveFile("data.txt", cart);
			System.out.println("ONPAUSE: cartera salvada "+cart.cuantosTicker());
		}
		else
			System.out.println("ONPAUSE: cartera no salvada, cart= NULL");    
	}

	private void formarListaCartera() {
		ItemCartera[] datos = null;
		if(eaC.readFile("data.txt") != null){
			datos = new ItemCartera[cart.cuantosTicker()];
			if((cart != null) && (cart.cuantosTicker() != 0)){
				int i=0;
				Iterator<Ticker> indice = cart.iterator();
				while (indice.hasNext() && !cart.isVacia()){
					tick = indice.next();
					datos[i] = new ItemCartera(tick.getNombreVal(), tick.getCotizacion(), 
							tick.getCambioValor(),tick.getNombreEmpresa(), tick.getColorCambio());
					i++;
				}
			}
		}

		else{
			datos = new ItemCartera[2];
			datos[0] = new ItemCartera("NOK", "8.74", "-0.33(3.92%)","Nokia Corp.", 0);
			datos[1] = new ItemCartera("TEF.MC", "17.11", "+0.19(1.13%)","Telefonica, S.A" , 1);
		}

		AdaptadorCartera adaptador = new AdaptadorCartera(this, datos);
		listaCartera.setAdapter(adaptador);
	}

	class AdaptadorCartera extends ArrayAdapter<Object> {

		Activity context;
		ItemCartera[] datos;

		AdaptadorCartera(Activity context, ItemCartera[] datos) {
			super(context, R.layout.list_item_cartera, datos);
			this.context = context;
			this.datos = datos;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View item = inflater.inflate(R.layout.list_item_cartera, null);

			TextView cartera_nombre = (TextView) item.findViewById(R.id.cartera_nombre);
			cartera_nombre.setText(datos[position].getNombre());

			TextView cartera_valor = (TextView) item.findViewById(R.id.cartera_valor);
			cartera_valor.setText(datos[position].getValor());

			TextView cartera_cambio = (TextView) item.findViewById(R.id.cartera_cambio);
			if(datos[position].getColor()==0)
				cartera_cambio.setTextColor(Color.rgb(150,8,8));
			if(datos[position].getColor()==1)
				cartera_cambio.setTextColor(Color.rgb(11, 97, 11));
			cartera_cambio.setText(datos[position].getCambio());

			TextView cartera_empresa = (TextView) item.findViewById(R.id.cartera_empresa);
			cartera_empresa.setText(datos[position].getEmpresa());

			return (item);
		}
	}

	private void formarListaMercados() {

		tickIBEX = ea.getTickerIBEX();

		ItemMercado[] datos = new ItemMercado[3];
		datos[0]= new ItemMercado(null,null, null);
		datos[1]= new ItemMercado(tickIBEX.getNombreVal(),tickIBEX.getCotizacion(),tickIBEX.getCambioValor());

		tickFTSE = ea.getTickerFTSE();

		datos[2]= new ItemMercado(tickFTSE.getNombreVal(),tickFTSE.getCotizacion(),tickFTSE.getCambioValor());
		AdaptadorMercados adaptador = new AdaptadorMercados(this, datos);
		adaptador.notifyDataSetChanged();
		listaMercado.setAdapter(adaptador);
	}

	class AdaptadorMercados extends ArrayAdapter<Object> {

		Activity context;
		ItemMercado[] datos;

		AdaptadorMercados(Activity context, ItemMercado[] datos) {
			super(context, R.layout.list_item_mercados, datos);
			this.context = context;
			this.datos = datos;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View item = inflater.inflate(R.layout.list_item_mercados, null);
			if (position == 0) {
				// Grafico de compraracion de mercados
				ImageView grafica = (ImageView) item
				.findViewById(R.id.grafica);
				grafica.setImageBitmap(downloadFile(imageHttpAddress));

			} else {

				TextView lblMercado = (TextView) item.findViewById(R.id.LblMercado);
				lblMercado.setText(datos[position].getNombre()+"   ");

				TextView lblValor = (TextView) item.findViewById(R.id.LblValor);

				TextView lblCambio = (TextView) item.findViewById(R.id.LblCambio);
				lblCambio.setText("   "+datos[position].getCambio());

				//color rojo o verde en el texto cambio de la lista mercados
				if(position==1){
					if(tickIBEX.getColorCambio()==0){
						lblCambio.setTextColor(Color.rgb(150,8,8));
					}if(tickIBEX.getColorCambio()==1){
						lblCambio.setTextColor(Color.rgb(11, 97, 11));
					}
					lblValor.setText("    "+datos[position].getValor()+" ");

				}if(position==2){
					if(tickFTSE.getColorCambio()==0){
						lblCambio.setTextColor(Color.rgb(150,8,8));
					}if(tickFTSE.getColorCambio()==1){
						lblCambio.setTextColor(Color.rgb(11, 97, 11));
					}
					lblValor.setText("      "+datos[position].getValor()+" ");
				}
			}
			return (item);
		}
	}

	/**
	 * Metodo que se utilizara cuando se cambie de tab, interesante para
	 * refrescar contenido de la pestanha no visible ej. actualizar mercados
	 * cuando estemos viendo el activity de cartera
	 */
	public void onTabChanged(String tabName) {
		if (tabName.equals(LIST2_TAB_TAG)) {
			//estoy en tab cartera, aprovecho para act. mercados
		} else if (tabName.equals(LIST1_TAB_TAG)) {
			//estoy en tab mercado, aprovecho para act. cartera
			formarListaMercados();
		}
	}

	/**
	 * Searches the dictionary and displays results for the given query.
	 * @param query, The search query
	 */
	private void showResults(String query) {

		Cursor cursor = managedQuery(TickerProvider.CONTENT_URI, null,
				null, new String[] { query }, null);

		if (cursor == null) {
			// There are no results
			mTextView.setText(getString(R.string.no_results,
					new Object[] { query }));
		} else {
			// Display the number of results
			int count = cursor.getCount();
			String countString = getResources().getQuantityString(
					R.plurals.search_results, count,
					new Object[] { count, query });
			mTextView.setText(countString);

			// Specify the columns we want to display in the result
			String[] from = new String[] { TickerDatabase.KEY_WORD,
					TickerDatabase.KEY_DEFINITION };

			// Specify the corresponding layout elements where we want the
			// columns to go
			int[] to = new int[] { R.id.word, R.id.definition };

			// Create a simple cursor adapter for the definitions and apply them
			// to the ListView
			SimpleCursorAdapter words = new SimpleCursorAdapter(this,
					R.layout.result, cursor, from, to);
			listaBuscador.setAdapter(words);

			// Define the on-click listener for the list items
			listaBuscador.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					//Pasamos el nombre del ticker
					String tickerNombre=null;

					if(!cart.isVacia())
						tickerNombre=cart.getTicker(position).getNombreVal();
					else
						tickerNombre="GOOG";
					Intent intentDetalle = new Intent(SearchableTickers.this, VistaDetalle.class);
					intentDetalle.putExtra("tickerNombre",tickerNombre);
					startActivity(intentDetalle);
				}
			});
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
			onSearchRequested();
			return true;
		case R.id.SubMnu_mayor:
			ORDEN = 1;
			cart = Eabolsa.readFile("data.txt");
			if(cart != null && !cart.isVacia()){
				Collections.sort(cart.lista);
			}
			formarListaCartera();
			return true;
		case R.id.SubMnu_menor:
			ORDEN = 2;
			cart = Eabolsa.readFile("data.txt");
			if(cart != null && !cart.isVacia()){
				Collections.sort(cart.lista);
			}
			formarListaCartera();
			return true;
		case R.id.SubMnu_defecto:
			ORDEN = 0;
			cart = Eabolsa.readFile("data.txt");
			if(cart != null && !cart.isVacia()){
				Collections.sort(cart.lista);
			}
			formarListaCartera();
			return true;
		case R.id.Menu_refresh:
			formarListaCartera();
			formarListaMercados();

			return true;
		default:
			return false;
		}
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, "Eliminar");
		menu.add(0, DELETE_TODO, 0, "Eliminar todo");
	}

	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case DELETE_ID:
			final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
			.getMenuInfo();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Seguro que desea borrar esta cotización?")
			.setTitle("Eliminar..")
			.setIcon(R.drawable.icon_interrogacion)
			.setCancelable(false)
			.setPositiveButton("Si",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int id) {
					if(cart != null && !cart.isVacia()){
						cart.borrar(info.position);
						Eabolsa.saveFile("data.txt", cart);
						formarListaCartera();
						Toast.makeText(
								getApplicationContext(),
								"Se ha eliminado correctamente de su cartera",
								Toast.LENGTH_LONG).show();
					}
					dialog.cancel();
				}
			})
			.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int id) {
					finish();
				}
			});

			AlertDialog alert = builder.create();
			alert.show();
			return true;
		case DELETE_TODO:
			AdapterContextMenuInfo infoT = (AdapterContextMenuInfo) item
			.getMenuInfo();

			AlertDialog.Builder builderT = new AlertDialog.Builder(this);
			builderT.setMessage(
			"Seguro que desea borrar la cartera por completo?")
			.setTitle("Eliminar..")
			.setIcon(R.drawable.icon_interrogacion)
			.setCancelable(false)
			.setPositiveButton("Si",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int id) {
					if(cart != null && !cart.isVacia()){
						cart.borrarTodo();
						Eabolsa.saveFile("data.txt", cart);
						formarListaCartera();
						Toast.makeText(
								getApplicationContext(),
								"Se han eliminado todas las cotizaciones de su cartera",
								Toast.LENGTH_LONG).show();
					}
					dialog.cancel();
				}
			})
			.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int id) {
					finish();
				}
			});

			AlertDialog alertT = builderT.create();
			alertT.show();

			return true;
		}
		return super.onContextItemSelected(item);
	}

}
