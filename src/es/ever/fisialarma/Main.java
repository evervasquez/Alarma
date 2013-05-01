package es.ever.fisialarma;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import de.ankri.views.Switch;
import es.ever.bd.datos;
import group.pals.android.lib.ui.lockpattern.LockPatternActivity;

public class Main extends SherlockActivity {

	NotificationManager notificacion;
	static final int unico = 773672;
	private final int _ReqCreateLockPattern = 0;
	private static final int _ReqEnterLockPattern = 1;
	TimePickerDialog tpd;
	Switch switchA;
	int horas, minutos;
	private static final String ALARM_ACTION_NAME = "com.bytefoundry.broadcast.ALARM";
	public static final int DIALOG_SEPARATION_WARNING = 0,
			DIALOG_EXITED_HARD = 1;
	private int m_alarmId = 0;
	int estado;
	TextView hora;
	datos info;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		getSupportActionBar().setTitle("Despertador");
		this.setTheme(R.style.AppThemeDark);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		notificacion = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		hora = (TextView) findViewById(R.id.textfecha);
		switchA = (Switch) findViewById(R.id.switch_a);
		try {
			info = new datos(this);
			info.abrir();
			info.insertar(1, 0, 0);
			// aqui todos los datos de la consulta
			long id = Long.parseLong("1");
			estado = info.getEstado(id);
			info.cerrar();
		} catch (Exception e) {
			Dialog d = new Dialog(this);
			d.setTitle("No Funciona");
			TextView tv = new TextView(this);
			tv.setText(e.toString() + " - " + estado);
			d.setContentView(tv);
			d.show();
			Toast.makeText(getApplicationContext(),
					"Hubo un Error en La Base de datos", Toast.LENGTH_SHORT)
					.show();
		}

		// inicia el patron
		if (estado == 0) {
			showDialog(DIALOG_SEPARATION_WARNING);
		}

		switchA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					enableBroadcastReceiver();
					openTimePickerDialog(false);
				} else {
					disableBroadcastReceiver();
					Toast.makeText(getApplicationContext(), "Alarma Cancelada", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void openTimePickerDialog(boolean is24r) {
		Calendar calendar = Calendar.getInstance();

		tpd = new TimePickerDialog(Main.this, onTimeSetListener,
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE) + 1, is24r);
		tpd.setTitle("Escoja La Hora");

		// Set the Cancel button
		tpd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_NEGATIVE) {
							switchA.setChecked(false);
						}
					}
				});
		tpd.show();
	}

	// crea la actividad del patron
	public void crearPatron() {
		Intent i = new Intent(LockPatternActivity._ActionCreatePattern, null,
				Main.this, LockPatternActivity.class);
		i.putExtra(LockPatternActivity._Theme, R.style.Alp_Theme_Light);
		i.putExtra(LockPatternActivity._StealthMode, false);
		i.putExtra(LockPatternActivity._EncrypterClass, LPEncrypter.class);
		i.putExtra(LockPatternActivity._AutoSave, true);
		i.putExtra(LockPatternActivity._MinWiredDots, 3);
		startActivityForResult(i, _ReqCreateLockPattern);
	}

	//metodo para cancelar el BroadcastReceiver
	 public void disableBroadcastReceiver(){
		    ComponentName receiver = new ComponentName(this, AlarmaReceiver.class);
		    PackageManager pm = this.getPackageManager();

		    pm.setComponentEnabledSetting(receiver,
		            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
		            PackageManager.DONT_KILL_APP);
		    Toast.makeText(this, "La Alarma cancelada", Toast.LENGTH_SHORT).show();
		 }   
	 
	 //metodo para iniciar denuevo el BroadcastReceiver
	 public void enableBroadcastReceiver(){
		    ComponentName receiver = new ComponentName(this, AlarmaReceiver.class);
		    PackageManager pm = this.getPackageManager();

		    pm.setComponentEnabledSetting(receiver,
		            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
		            PackageManager.DONT_KILL_APP);
		   }
	// boton Definir
	OnTimeSetListener onTimeSetListener = new OnTimeSetListener() {
		@SuppressWarnings("deprecation")
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
			Intent in = new Intent(getBaseContext(), Main.class);
			PendingIntent pi = PendingIntent.getActivity(getBaseContext(), 0,
					in, 0);
			String cuerpo = "Alarma Iniciada";
			String titulo = "Despertador";

			Notification n = new Notification(R.drawable.ic_launcher, cuerpo,
					System.currentTimeMillis());
			n.setLatestEventInfo(getBaseContext(), titulo, cuerpo, pi);
			n.defaults = Notification.DEFAULT_ALL;
			notificacion.notify(unico, n);
			setAlarm(calSet);
			switchA.setChecked(true);
		}

	};

	// boton cancel
	protected void finalize() throws Throwable {
		tpd = null;
		info = null;
		switchA = null;
		notificacion = null;
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
		hora.setText("***\n" + "Alarma  " + targetCal.getTime() + "\n"
				+ "***\n");

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

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		switchA.setChecked(false);
		Toast.makeText(getApplicationContext(), "Saliendo de la Aplicación", Toast.LENGTH_SHORT).show();
		this.finish();
		super.onRestart();
	}
	
	// /////////////////////////////
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		try {
			info = new datos(this);
			info.abrir();
			long id = Long.parseLong("1");
			estado = info.getEstado(id);
			info.cerrar();
		} catch (Exception e) {

		}
		if (estado == 0) {
			showDialog(DIALOG_SEPARATION_WARNING);
		}
		super.onRestart();
	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case _ReqCreateLockPattern: {
			if (resultCode == RESULT_OK) {
				setTitle(data.getStringExtra(LockPatternActivity._Pattern));
				try {
					info = new datos(this);
					info.abrir();
					boolean dato = info.actualizar(1, 1, 0);
					info.cerrar();
					if (dato) {
						Toast.makeText(getApplicationContext(),
								"El Patron Fue Guardado Con Exito",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(),
								"Intente Nuevamente", Toast.LENGTH_SHORT)
								.show();
					}
				} catch (Exception e) {
					e.getMessage();
				}
			} else {
				setTitle(R.string.app_name);
			}
			break;
		}// _ReqCreateLockPattern

		case _ReqEnterLockPattern: {
			int msgId = 0;

			switch (resultCode) {
			case RESULT_OK:
				msgId = android.R.string.ok;
				break;
			case RESULT_CANCELED:
				msgId = android.R.string.cancel;
				break;
			case LockPatternActivity._ResultFailed:
				msgId = R.string.failed;
				break;
			default:
				return;
			}

			String msg = String.format("%s (%,d tries)", getString(msgId),
					data.getIntExtra(LockPatternActivity._ExtraRetryCount, 0));

			Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();

			break;
		}// _ReqEnterLockPattern
		}
	}// onActivityResult()

	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder;
		switch (id) {
		case DIALOG_SEPARATION_WARNING:
			// set up the users ability to disable this reminder
			View disableView = getLayoutInflater().inflate(
					R.layout.separation_reminder_disable, null);

			builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.notice))
					.setMessage(getString(R.string.separation_warning))
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(disableView)
					.setCancelable(false)
					.setNegativeButton(R.string.cancelar,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									Main.this.finish();
								}
							})
					.setPositiveButton(getString(R.string.cont),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
									crearPatron();
								}
							});

			dialog = builder.create();

			break;
		default:
			return super.onCreateDialog(id);
		}
		return dialog;
	}

}
