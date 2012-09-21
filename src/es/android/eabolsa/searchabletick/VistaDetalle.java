package es.android.eabolsa.searchabletick;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

import es.android.eabolsa.backend.Eabolsa;
import es.android.eabolsa.backend.Ticker;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VistaDetalle extends Activity {

	private String ticker=null;
	private Intent intent;
	private LinearLayout linLay;
	private Activity context;
	private final int UN_D = 0x01;
	private final int CINCO_D = 0x02;
	private final int TRES_M = 0x03;
	private final int SEIS_M = 0x04;
	private final int UN_Y = 0x05;
	private final int MAX = 0x06;
	private ImageView grafica_mercados;
	
	private String periodo;
	
	private Handler mHandler = new Handler();
	
	private Eabolsa eaB;
	
	//notificacion
	private static final int HELLO_ID = 1;
	
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.vista_en_detalle);
		
		context=this.context;
		// recupero el nomnbre del ticker
		super.onNewIntent(intent);
		Bundle extras = getIntent().getExtras();
		intent = new Intent(this, SearchableTickers.class);
        if (extras != null) {	
            ticker = (String)extras.get("tickerNombre");   
        }else{
            Toast.makeText(VistaDetalle.this,
    				"Lo siento, vuelva a intentarlo", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            
        }
  System.out.println(ticker);
		
		Toast.makeText(VistaDetalle.this,
				"TICKER: "+ticker, Toast.LENGTH_SHORT).show();
	
		eaB = new Eabolsa(ticker);
		
		periodo="1d";
		Ticker tick = eaB.getTicker();
		
		StringTokenizer st = new StringTokenizer(Eabolsa.techAnalytic(tick), ";"); 
		String[] histo = new String[st.countTokens()];
		int i=0;
		while(st.hasMoreTokens()) { 
			histo[i] = st.nextToken(); 
			System.out.println(histo[i]);
			i++;
			
		}
		

		/*NOTIFICACION*/
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		Vibrator v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);

		//Instantiate the Notification:
		int icon = R.drawable.notification_icon;
		CharSequence tickerText = histo[0];
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		//Define the Notification's expanded message and Intent:
		Context context = getApplicationContext();
		CharSequence contentTitle = histo[1];
		CharSequence contentText = histo[2];
		Intent notificationIntent = new Intent(this, SearchableTickers.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		//Pass the Notification to the NotificationManager:
		mNotificationManager.notify(HELLO_ID, notification);
		// Vibrar durante 1/2 segundo
		v.vibrate(500);
		
		formarContenido(eaB.getTicker());

		 mHandler.removeCallbacks(mMuestraMensaje);
	      mHandler.postDelayed(mMuestraMensaje, 15000);
		
	}
	
	 private Runnable mMuestraMensaje = new Runnable() {
         public void run() {
            Toast.makeText(VistaDetalle.this, "Refresh activado", Toast.LENGTH_LONG).show();
            mHandler.removeCallbacks(mMuestraMensaje);
            eaB = new Eabolsa(ticker);
            formarContenido(eaB.getTicker());
            mHandler.postDelayed(this, 15000);
         }
       };

	private void formarContenido(Ticker tick) {
		
		TextView v_nombre_valor = (TextView) findViewById(R.id.v_nombre_valor);
		v_nombre_valor.setText(tick.getNombreVal());
		
		TextView v_cambio = (TextView) findViewById(R.id.v_cambio_valor);
		v_cambio.setText(tick.getCambioValor());
		if(tick.getColorCambio()==0)
			v_cambio.setTextColor(Color.rgb(150,8,8));
		if(tick.getColorCambio()==1)
			v_cambio.setTextColor(Color.rgb(11, 97, 11));

		
		TextView v_cotizacion = (TextView) findViewById(R.id.v_cotizacion);
		v_cotizacion.setText(tick.getCotizacion());

	
		TextView v_hora_cotizacion = (TextView) findViewById(R.id.v_hora_cotizacion);
		v_hora_cotizacion.setText(tick.getHoraCot());
		

		TextView v_cierre = (TextView) findViewById(R.id.v_cierre_ant);
		v_cierre.setText(tick.getCierreAnt());
		
	
		TextView v_apertura = (TextView) findViewById(R.id.v_apertura);
		v_apertura.setText(tick.getApertura());
		

		TextView v_oferta = (TextView) findViewById(R.id.v_oferta);
		v_oferta.setText(tick.getOferta());
		

		TextView v_demanda = (TextView) findViewById(R.id.v_demanda);
		v_demanda.setText(tick.getDemanda());
		

		TextView v_rango_diario = (TextView) findViewById(R.id.v_rango_diario);
		v_rango_diario.setText(tick.getRangoDiario());
		

		TextView v_rango52sem = (TextView) findViewById(R.id.v_rango52sem);
		v_rango52sem.setText(tick.getRangoAnual());
		

		TextView v_vol = (TextView) findViewById(R.id.v_vol);
		v_vol.setText(tick.getVolumen());
		

		TextView v_vol3mes = (TextView) findViewById(R.id.v_vol3mes);
		v_vol3mes.setText(tick.getVol3mes());


		TextView v_ganancia_x_accion = (TextView) findViewById(R.id.v_ganancia_x_accion);
		v_ganancia_x_accion.setText(tick.getGananciaPorAccion());

		TextView v_rendimiento = (TextView) findViewById(R.id.v_rendimiento);
		v_rendimiento.setText(tick.getRendimiento());
		

		TextView v_recomend_promedio = (TextView) findViewById(R.id.v_recomend_promedio);
		StringTokenizer rec = new StringTokenizer(tick.getRecomendacion(), ";"); 
		String[] recomendacion = new String[rec.countTokens()];
		int i=0;
		while(rec.hasMoreTokens()){
			recomendacion[i] = rec.nextToken(); 
			i++;
		}
		v_recomend_promedio.setText(recomendacion[i-1]);
		
		
		ImageView im_graf = (ImageView) findViewById(R.id.grafica_detalle);
		im_graf.setImageBitmap(downloadFile(tick.getGraficaTicker(tick.getNombreVal(), periodo)));

		registerForContextMenu(im_graf);
	}

	/*protected void onPause() {
		super.onPause();
		startActivity(intent);

	}*/

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, UN_D, 0, "1 día");
		menu.add(0, CINCO_D, 0, "5 días");
		menu.add(0, TRES_M, 0, "3 meses");
		menu.add(0, SEIS_M, 0, "6 meses");
		menu.add(0, UN_Y, 0, "1 año");
		menu.add(0, MAX, 0, "Máximo");
	}

	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case UN_D:
			periodo = "1d";
			return true;
		case CINCO_D:
			periodo = "5d";
			return true;
		case TRES_M:
			periodo = "3m";
			return true;
		case SEIS_M:
			periodo = "6m";
			return true;
		case UN_Y:
			periodo = "1y";
			return true;
		case MAX:
			periodo = "my";
			return true;
		default:
			mHandler.removeCallbacks(mMuestraMensaje);
		    mHandler.postDelayed(mMuestraMensaje, 15000);
		}
		return super.onContextItemSelected(item);
	}
	
	public Bitmap downloadFile(String imageHttpAddress) {
		URL imageUrl = null;
		Bitmap loadedImage=null;
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
	
	protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mMuestraMensaje);
	}
}
