package br.pucminas.databaseproject.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.pucminas.databaseproject.User;

/**
 * Created by luket on 25-Sep-16.
 */
public class UserDAO {
    private SQLiteDatabase bd;

    public UserDAO(Context context){
        DataBase auxBd = new DataBase(context);
        bd = auxBd.getWritableDatabase();
    }
    
    public void Create(User User){
        ContentValues values = new ContentValues();
        values.put(UserContract.NAME, User.getName());
        values.put(UserContract.EMAIL, User.getEmail());
        values.put(UserContract.PASSWORD, User.getPassword());
        bd.insert(
                // Table's name
                UserContract.TABLE_NAME,
                // Deal with null columns
                null,
                // Key/values pairs to insert
                values);
    }

    public void Update(User User){
        ContentValues values = new ContentValues();
        values.put(UserContract.NAME, User.getName());
        values.put(UserContract.EMAIL, User.getEmail());
        values.put(UserContract.PASSWORD, User.getPassword());
        bd.update(
                // Table's name
                UserContract.TABLE_NAME,
                // Key/values pairs to update
                values,
                // WHERE clause
                UserContract._ID + " = ?",
                // WHERE values
                new String[]{"" + User.getId()});
    }


    public void Delete(User User){
        bd.delete(
                // Table's name
                UserContract.TABLE_NAME,
                // WHERE clause
                UserContract._ID + " = ? ",
                // WHERE values
                new String[]{"" + User.getId()});
    }

    public List<User> Read(){
        List<User> list = new ArrayList<User>();
        String[] columns = new String[]{
                UserContract._ID,
                UserContract.NAME,
                UserContract.EMAIL,
                UserContract.PASSWORD};

        Cursor cursor = bd.query(
                // Table's Name
                UserContract.TABLE_NAME,
                // Columns that will be retrieved
                columns,
                // WHERE clause
                null,
                // WHERE values
                null,
                // GROUP BY clause
                null,
                // HAVING clause
                null,
                // ORDER BY clause
                UserContract.NAME +" ASC");

        if(cursor.getCount() > 0){
            // Go to the first result
            cursor.moveToFirst();
            do{
                User u = new User();
                u.setId(cursor.getLong(0));
                u.setName(cursor.getString(1));
                u.setEmail(cursor.getString(2));
                u.setPassword(cursor.getString(3));

                list.add(u);

            }while(cursor.moveToNext());
        }
        return(list);
    }
}
