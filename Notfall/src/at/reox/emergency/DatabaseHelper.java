package at.reox.emergency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "emergeData";

    private Context mCtx;

    public DatabaseHelper(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
	mCtx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
	String query = "CREATE TABLE icd (number text, name text)";

	db.execSQL(query);
	SQLiteStatement stmt = db.compileStatement("insert into icd values (?, ?)");

	// Fill up Database with Values...
	try {
	    InputStream input = mCtx.getAssets().open("icd10.txt");
	    BufferedReader br = new BufferedReader(new InputStreamReader(input, "ISO-8859-1"));

	    String line;
	    while ((line = br.readLine()) != null) {
		String[] icd = line.split("\\|");
		stmt.bindString(1, icd[0]);
		stmt.bindString(2, icd[1]);
		stmt.execute();
	    }

	    input.close();
	} catch (IOException e) {
	    Log.w("Database", "Error while filling ICD Values", e);
	}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
	if (oldV != newV) {
	    db.execSQL("DROP TABLE icd");

	    onCreate(db);
	}
    }

    public Cursor getICDCursor(String arg) {
	String query;
	if ((arg != null) && arg.matches("[A-Z][0-9]{2}.*")) {
	    query = "SELECT DISTINCT number as _id, number as name from icd where number like '"
		+ arg + "%' limit 50";

	} else {
	    query = "SELECT oid as _id, name from icd where name like '%" + arg + "%' limit 50";
	}
	return getReadableDatabase().rawQuery(query, null);
    }

    public String getICDName(String code) {
	Log.d("Database", "select name of code " + code);
	String query = "SELECT name from icd where number ='" + code + "' limit 1";
	SQLiteDatabase db = getReadableDatabase();
	Cursor c = db.rawQuery(query, null);
	c.moveToFirst();
	return c.getString(0);
    }

    public String getICDCode(String code) {
	Log.d("Database", "select number of code " + code);
	String query = "SELECT number from icd where name ='" + code + "' limit 1";
	SQLiteDatabase db = getReadableDatabase();
	Cursor c = db.rawQuery(query, null);
	c.moveToFirst();
	return c.getString(0);
    }

}
