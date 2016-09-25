package br.pucminas.databaseproject.bd;

import android.provider.BaseColumns;

/**
 * Created by luket on 25-Sep-16.
 */
public interface UserContract extends BaseColumns {
    String TABLE_NAME = "User";
    String NAME = "name";
    String EMAIL = "email";
    String PASSWORD = "password";
    String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME + " TEXT NOT NULL, " +
            EMAIL + " TEXT NOT NULL, " +
            PASSWORD + " TEXT NOT NULL );";

}
