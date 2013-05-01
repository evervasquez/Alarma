package es.ever.fisialarma;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.widget.Toast;

public class flash {
	private Camera dispCamara;
	Parameters parametrosCamara;
	boolean encendido = true;
	Context contexto;
	int delay = 600;
	int period = 600;
	int delay1 = 300;
	int period1 = 600;
	Timer timer, timer2;

	public flash(Context context) {
		if (dispCamara == null) {
			dispCamara = Camera.open();
		}
		contexto = context;

		if (timer == null & timer2 == null) {
			timer = new Timer();
			timer2 = new Timer();
		}

		timer.scheduleAtFixedRate(new TimerTask() {

			public void run() {
				// TODO Auto-generated method stub
				if (encendido) {
					encenderLinternaAndroid();
				} else {
					terminar();
				}
			}
		}, delay1, period1);

		timer2.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				apagarLinternaAndroid();
			}
		}, delay, period);
	}

	public void apagarLinternaAndroid() {
		if (dispCamara != null) {
			parametrosCamara = dispCamara.getParameters();
			parametrosCamara.setFlashMode(Parameters.FLASH_MODE_OFF);
			dispCamara.setParameters(parametrosCamara);
		} else {
			Toast.makeText(contexto,
					"No se ha podido acceder al Flash de la cámara",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void encenderLinternaAndroid() {
		// Toast.makeText(getApplicationContext(),
		// "Accediendo a la cámara", Toast.LENGTH_SHORT).show();

		if (dispCamara != null) {
			// Toast.makeText(getApplicationContext(),
			// "Cámara encontrada", Toast.LENGTH_SHORT).show();

			Parameters parametrosCamara = dispCamara.getParameters();

			// obtener modos de flash de la cámara
			List<String> modosFlash = parametrosCamara.getSupportedFlashModes();

			if (modosFlash != null
					&& modosFlash.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
				// establecer parámetro TORCH para el flash de la cámara
				parametrosCamara
						.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				parametrosCamara
						.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
				try {
					dispCamara.setParameters(parametrosCamara);
					dispCamara.startPreview();
				} catch (Exception e) {
					Toast.makeText(contexto, "Error al activar la linterna",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(contexto,
						"El dispositivo no tiene el modo de Flash Linterna",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(contexto,
					"No se ha podido acceder al Flash de la cámara",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void parar() {
		encendido = false;
	}

	public void terminar() {
		timer.cancel();
		timer2.cancel();
		dispCamara = null;
		// timer = null;
		// timer2 = null;
	}
}
