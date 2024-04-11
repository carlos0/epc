package bo.gob.ine.naci.epc.entidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by INE.
 */
//EN DB
public class DataBaseReader extends SQLiteOpenHelper {
    public DataBaseReader(Context context, String fileDB) {
        super(context, fileDB, null, DataBase.versionDB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
