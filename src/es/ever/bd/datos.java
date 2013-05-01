package es.ever.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class datos {

	public static final String ID_FILA = "_id";
	public static final String ID_ESTADO = "estado";
	public static final String ID_VECES = "veces";

	private static final String N_BD = "Alarma";
	private static final String N_TABLA = "tabla_alarma";
	private static final int VERSION_BD = 1;

	private BDHelper nHelper;
	private final Context nContexto;
	private SQLiteDatabase nBD;

	private static class BDHelper extends SQLiteOpenHelper {

		public BDHelper(Context context) {
			super(context, N_BD, null, VERSION_BD);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + N_TABLA + "(" + ID_FILA
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + ID_ESTADO
					+ " INTEGER NOT NULL DEFAULT 0, " + ID_VECES
					+ " INTEGER DEFAULT 0);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + N_TABLA);
			onCreate(db);
		}

	}

	public datos(Context c) {
		nContexto = c;
	}

	// metodo para abrir nuestra base de datos
	public datos abrir() throws Exception {
		nHelper = new BDHelper(nContexto);
		nBD = nHelper.getReadableDatabase();
		return this;
	}

	public void cerrar() {
		nHelper.close();
	}

	public long insertar(Integer id, Integer alarma, Integer evento) {
		ContentValues newValues = new ContentValues();
		newValues.put(ID_FILA, id);
		newValues.put(ID_ESTADO, alarma);
		newValues.put(ID_VECES, evento);
		return nBD.insert(N_TABLA, null, newValues);
		}

	public String seleccionar() {
		// TODO Auto-generated method stub
		String resultado = "";
		String[] columna = new String[] { ID_FILA,ID_ESTADO,ID_VECES };
		Cursor c = nBD.query(N_TABLA, columna, null, null, null, null, null);

		int iFila = c.getColumnIndex(ID_FILA);
		int iestado = c.getColumnIndex(ID_ESTADO);
		int iveces = c.getColumnIndex(ID_VECES);
		// resultado = new int[3];
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			resultado = c.getInt(iFila) + " - " + c.getInt(iestado) + " - "
					+ c.getInt(iveces);
		}
		return resultado;
	}

	// metodos para busqueda
	public int getEstado(long id) {
		String[] columna = new String[] { ID_FILA,ID_ESTADO,ID_VECES };
		Cursor c = nBD.query(N_TABLA, columna, ID_FILA + "="+ id, null, null, null, null);
		if(c !=null){
			c.moveToFirst();
			int estado = c.getInt(c.getColumnIndex(ID_ESTADO));
			return estado;
		}
		return (Integer) null;
	}

	public int getVeces(long id) {
		String[] columna = new String[] { ID_FILA,ID_ESTADO,ID_VECES };
		Cursor c = nBD.query(N_TABLA, columna, ID_FILA + "="+ id, null, null, null, null);
		if(c !=null){
			c.moveToFirst();
			int veces = c.getInt(2);
			return veces;
		}
		return (Integer) null;
		
	}

	//
	public boolean actualizar(Integer _rowIndex, Integer estado, Integer veces) {
		ContentValues newValues = new ContentValues();
		newValues.put(ID_ESTADO,estado);
		newValues.put(ID_VECES, veces);
		return nBD.update(N_TABLA, newValues, ID_FILA + "=" + _rowIndex, null) > 0;
		}
	
}
