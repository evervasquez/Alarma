package es.ever.fisialarma;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.widget.Toast;

public class PatronLock extends Activity implements OnCompletionListener {
	// An ID of the alarm dialog
	private static final int _ReqEnterLockPattern = 1;
	flash flashito;
	MediaPlayer player;
	// The alarm ID
	private int contadorValidas = 1;
	public static final int _tema = R.style.Alp_Theme_Light;

	// private final int _ReqCreateLockPattern = 0;
	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.alarma);
		crearPatron();
		flashito = new flash(getApplicationContext());
		AssetManager manager = this.getAssets();
		player = new MediaPlayer();

		try {
			AssetFileDescriptor descriptor = manager.openFd("musica.mp3");
			player.setDataSource(descriptor.getFileDescriptor(),
					descriptor.getStartOffset(), descriptor.getLength());
			player.prepare();

			// reproduccion
			// player.start();
			// player.setOnCompletionListener(this);

		} catch (Exception e) {

		}
		// Show the popup dialog
		// showDialog(DIALOG_ALARM);

	}
	 protected void finalize() throws Throwable {
		 flashito = null;
		 player = null;
		 contadorValidas = 1;
	 };
	public void crearPatron() {
		Intent i = new Intent(LockPatternActivity._ActionComparePattern, null,
				PatronLock.this, LockPatternActivity.class);
		i.putExtra(LockPatternActivity._Theme, _tema);
		i.putExtra(LockPatternActivity._StealthMode, false);
		i.putExtra(LockPatternActivity._EncrypterClass, LPEncrypter.class);
		i.putExtra(LockPatternActivity._AutoSave, true);
		i.putExtra(LockPatternActivity._MinWiredDots, 3);
		startActivityForResult(i, _ReqEnterLockPattern);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1 && contadorValidas == 3) {
			switch (requestCode) {
			case _ReqEnterLockPattern: {
				int msgId = 0;
				switch (resultCode) {
				case RESULT_OK:
					msgId = android.R.string.ok;
					flashito.parar();
					player.stop();
					contadorValidas = 0;
					this.finish();
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
				break;
			}// _ReqEnterLockPattern
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Le quedan " + (3 - contadorValidas) + " veces",
					Toast.LENGTH_SHORT).show();
			contadorValidas = contadorValidas + 1;
			crearPatron();
		}
	}// onActivityResult()

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		player.start();
		// player.stop();
	}

	public void onBackPressed() {
		Toast.makeText(getApplicationContext(),
				"Usted Tiene que elegir un Patrón", Toast.LENGTH_SHORT).show();
	}

}