package es.ever.fisialarma;

import java.util.ArrayList;
import java.util.List;
import lockpanttern.EmergencyExit;
import lockpanttern.LockPatternView;
import lockpanttern.PatternGenerator;
import lockpanttern.Point;
import lockpanttern.dialogos;
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
	protected LockPatternView mPatternView;
	protected PatternGenerator mGenerator;
	protected int mGridLength;
	protected int mPatternMin;
	protected int mPatternMax;
	dialogos dialogo;
	public Button mGenerateButton;
	private ToggleButton mPracticeToggle;
	protected String mHighlightMode;
	protected boolean mTactileFeedback;
	private List<Point> mEasterEggPattern;
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
		mGenerator = new PatternGenerator();

		final Thread.UncaughtExceptionHandler exceptionHandler = Thread
				.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {
				if (throwable instanceof OutOfMemoryError) {
					EmergencyExit.clearAndBail(PatronLock.this);
				}
				// punt if it's not an exception we can handle
				exceptionHandler.uncaughtException(thread, throwable);
			}
		});

		mEasterEggPattern = new ArrayList<Point>();
		mEasterEggPattern.add(new Point(0, 2));
		mEasterEggPattern.add(new Point(0, 1));
		mEasterEggPattern.add(new Point(0, 0));
		mEasterEggPattern.add(new Point(1, 1));
		mEasterEggPattern.add(new Point(2, 2));
		mEasterEggPattern.add(new Point(2, 1));
		mEasterEggPattern.add(new Point(2, 0));

		setContentView(R.layout.patronlock_activity);
		mPatternView = (LockPatternView) findViewById(R.id.pattern_view);
		mGenerateButton = (Button) findViewById(R.id.generate_button);
		mPracticeToggle = (ToggleButton) findViewById(R.id.practice_toggle);

		mGenerateButton.setText("Tienes 3 intentos");
		mGenerateButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				mPatternView.setPattern(mGenerator.getPattern());
				mPatternView.invalidate();
			}
		});
		mGenerateButton
				.setOnLongClickListener(new Button.OnLongClickListener() {
					@Override
					public boolean onLongClick(View view) {
						if (mPatternView.getGridLength() == 3) {
							LockPatternView.HighlightMode oldHighlight = mPatternView
									.getHighlightMode();
							mPatternView
									.setHighlightMode(new LockPatternView.NoHighlight());
							mPatternView.setPattern(mEasterEggPattern);
							mPatternView.setHighlightMode(oldHighlight, true);
						}
						mPatternView.invalidate();
						return true;
					}
				});

		mPracticeToggle
				.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						mGenerateButton.setEnabled(!isChecked);
						mPatternView.setPracticeMode(isChecked);
						mPatternView.invalidate();
					}
				});

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
		updateFromPrefs();
		super.onResume();
	}

	private void updateFromPrefs() {
		int gridLength = 3;
		int patternMin = 5;
		int patternMax = 5;
		String highlightMode = "first";
		boolean tactileFeedback = false;

		// only update values that differ
		if (gridLength != mGridLength) {
			setGridLength(gridLength);
		}
		if (patternMax != mPatternMax) {
			setPatternMax(patternMax);
		}
		if (patternMin != mPatternMin) {
			setPatternMin(patternMin);
		}
		if (!highlightMode.equals(mHighlightMode)) {
			setHighlightMode(highlightMode);
		}
		if (tactileFeedback ^ mTactileFeedback) {
			setTactileFeedback(tactileFeedback);
		}
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
	
	/*public boolean onTouchEvent(MotionEvent event){
    	int count = 0;
		switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            	
            case MotionEvent.ACTION_MOVE:


            case MotionEvent.ACTION_UP:

            	if(mPatternView.validacion && count == 0){
            	dialogos dialogo = new dialogos();
            	dialogo.Dialogo_Alerta(this, "correcto");
            	count = count +1;
            	}
            default:
                return super.onTouchEvent(event);
        }
		
	}*/
	private void setGridLength(int length) {
		mGridLength = length;
		mGenerator.setGridLength(length);
		mPatternView.setGridLength(length);
	}

	private void setPatternMin(int nodes) {
		mPatternMin = nodes;
		mGenerator.setMinNodes(nodes);
	}

	private void setPatternMax(int nodes) {
		mPatternMax = nodes;
		mGenerator.setMaxNodes(nodes);
	}

	private void setHighlightMode(String mode) {
		if ("no".equals(mode)) {
			mPatternView.setHighlightMode(new LockPatternView.NoHighlight());
		} else if ("first".equals(mode)) {
			mPatternView.setHighlightMode(new LockPatternView.FirstHighlight());
		} else if ("rainbow".equals(mode)) {
			mPatternView
					.setHighlightMode(new LockPatternView.RainbowHighlight());
		}

		mHighlightMode = mode;
	}

	private void setTactileFeedback(boolean enabled) {
		mTactileFeedback = enabled;
		mPatternView.setTactileFeedbackEnabled(enabled);
	}
	
	public void verdad(){
		mGenerateButton.setText("2");
	}
}