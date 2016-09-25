package br.pucminas.databaseproject.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by luket on 25-Sep-16.
 */
public class DataBase extends SQLiteOpenHelper {
    private static final String NAME_BD = "DataBase";
    private static final int VERSION_BD = 1;

    public DataBase(Context ctx){
        super(ctx, NAME_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase bd) {
        bd.execSQL(UserContract.CREATE_TABLE_SQL);
        Log.d("DataBase", UserContract.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase bd, int arg1, int arg2) {
    }

}
