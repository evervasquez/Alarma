package lockpanttern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class dialogos {

	public static Dialog Dialogo_Alerta(Context context, String mensaje) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Informacion");
		builder.setMessage(mensaje);
		builder.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create();
		return builder.show();
	}
}