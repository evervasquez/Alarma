package es.ever.fisialarma;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockActivity;

public class Main extends SherlockActivity {

	NotificationManager notificacion;
	static final int unico = 773672;
	TimePickerDialog tpd;
	Button boton;
	ImageButton inicia;
	int horas, minutos;
	private static final String ALARM_ACTION_NAME = "com.bytefoundry.broadcast.ALARM";
	private int m_alarmId = 0;
	boolean encendido = true;
	TextView hora;
	OnTimeSetListener mtpd;
	flash flashito;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		getSupportActionBar().setTitle("Despertador");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		notificacion = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		hora = (TextView) findViewById(R.id.textfecha);
		boton = (Button) findViewById(R.id.btncrear);
		
		
		boton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openTimePickerDialog(false);
			}
		});
		
		inicia = (ImageButton) findViewById(R.id.imageButton1);
		inicia.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getBaseContext(), Main.class);
				PendingIntent pi = PendingIntent.getActivity(getBaseContext(),
						0, in, 0);
				String cuerpo = "Alarma Iniciada";
				String titulo = "Despertador";

				Notification n = new Notification(R.drawable.ic_launcher,
						cuerpo, System.currentTimeMillis());
				n.setLatestEventInfo(getBaseContext(), titulo, cuerpo, pi);
				n.defaults = Notification.DEFAULT_ALL;
				notificacion.notify(unico, n);
				
				
				/*if (encendido) {
					if (flashito == null) {
						flashito = new flash(getApplicationContext());
					} else {
						flashito.encenderLinternaAndroid();
					}
					encendido = false;
				} else {
					flashito.apagarLinternaAndroid();
					encendido = true;
				}*/
				
				
			}
		});
	}
	
	private void openTimePickerDialog(boolean is24r) {
		Calendar calendar = Calendar.getInstance();

		tpd = new TimePickerDialog(Main.this,
				onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE)+1, is24r);
		tpd.setTitle("Escoja La Hora");

		tpd.show();

	}	
	
	OnTimeSetListener onTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			Calendar calNow = Calendar.getInstance();
			Calendar calSet = (Calendar) calNow.clone();

			m_alarmId++;
			calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calSet.set(Calendar.MINUTE, minute);
			calSet.set(Calendar.SECOND, 0);
			calSet.set(Calendar.MILLISECOND, 0);

			if (calSet.compareTo(calNow) <= 0) {
				// Today Set time passed, count to tomorrow
				calSet.add(Calendar.DATE, 1);
			}

			setAlarm(calSet);
		}
	};
	
	
	private void setAlarm(Calendar targetCal) {
		int hours = targetCal.get(Calendar.HOUR);
		int mnts = targetCal.get(Calendar.MINUTE);
		int ampm = targetCal.get(Calendar.AM_PM);

		String str = "AM";
		if (ampm == 0) {
			str = "AM";
		} else {
			str = "PM";
		}

		String time = hours + " : " + mnts + " : " + str;
		hora.setText(time);
		hora.setText("***\n" + "Alarma  "
				+ targetCal.getTime() + "\n" + "***\n");

		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent alarmIntent = new Intent(ALARM_ACTION_NAME);

		// Set the Alarm ID as extra data to be displayed in the popup
		alarmIntent.putExtra("AlarmID", m_alarmId);
		alarmIntent.putExtra("AlarmTime", time);

		// Create the corresponding PendingIntent object
		PendingIntent alarmPI = PendingIntent.getBroadcast(this, m_alarmId,
				alarmIntent, 0);

		// Register the alarm with the alarm manager
		alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
				alarmPI);
	}

}
