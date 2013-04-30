package es.ever.fisialarma;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class PatronLock extends Activity implements OnCompletionListener {
	// An ID of the alarm dialog
	public static final int DIALOG_SEPARATION_WARNING = 0,
			DIALOG_EXITED_HARD = 1;
	protected int mGridLength;
	protected int mPatternMin;
	protected int mPatternMax;
	public Button mGenerateButton;
	private ToggleButton mPracticeToggle;
	protected String mHighlightMode;
	protected boolean mTactileFeedback;
	private static final int DIALOG_ALARM = 0;
	flash flashito = null;
	MediaPlayer player;
	// The alarm ID
	private int m_alarmId;
	private String time = "";

	@SuppressWarnings({ "deprecation" })
	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);

		setContentView(R.layout.patronlock_activity);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		flashito = new flash(getApplicationContext());
		if (extras != null) {
			m_alarmId = extras.getInt("AlarmID", -1);
			time = extras.getString("AlarmTime");
		} else {
			m_alarmId = -1;
		}

		AssetManager manager = this.getAssets();
		player = new MediaPlayer();
		try {
			AssetFileDescriptor descriptor = manager.openFd("musica.mp3");
			player.setDataSource(descriptor.getFileDescriptor(),
					descriptor.getStartOffset(), descriptor.getLength());
			player.prepare();
			
	// reproduccion
			//player.start();
			//player.setOnCompletionListener(this);
			
		} catch (Exception e) {

		}
		// Show the popup dialog
		showDialog(DIALOG_ALARM);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// se envian los parametros por primera vez
		super.onResume();
	}


	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);

		// Build the dialog
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Alarm Received!!!");
		alert.setMessage("Its time for the alarm with ID: " + m_alarmId
				+ "\n\n Time :: " + time);
		alert.setCancelable(false);

		// metodo para el boton
		alert.setPositiveButton("Terminar",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						flashito.parar();
					}
				});

		// Create and return the dialog
		AlertDialog dlg = alert.create();
		return dlg;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		player.start();
		// player.stop();
	}

	public void onBackPressed() {
		super.onBackPressed();
	}
	
}