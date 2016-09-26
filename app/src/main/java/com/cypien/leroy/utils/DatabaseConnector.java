package com.cypien.leroy.utils;/*
 * Created by Alex on 14.09.2016.
 */

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cypien.leroy.models.Message;
import com.cypien.leroy.models.Store;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseConnector extends SQLiteOpenHelper {

    @SuppressLint("SdCardPath")
    private static final String DB_PATH = "/data/data/com.cypien.leroy/databases/";
    private static final String DB_NAME = "leroy.sqlite";
    private static DatabaseConnector instance;
    private final Context myContext;

    private Cursor curs;
    private SQLiteDatabase database;

    private DatabaseConnector(Context context) {
        super(context, DB_NAME, null, 2);
        this.myContext = context;
    }

    public static synchronized DatabaseConnector getHelper(Context context) {
        if (instance == null)
            instance = new DatabaseConnector(context);

        return instance;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[2048];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private synchronized void open() throws Exception {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            database = getWritableDatabase();
        } else {
            String myPath = DB_PATH + DB_NAME;
            database = getWritableDatabase();
            copyDataBase();
            database = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
        }
    }

    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            Log.e("myPath", myPath);
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException ignored) {

        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }


    @Override
    public synchronized void close() {
        if (database != null)
            database.close();
        if (curs != null)
            curs.close();
        super.close();
    }

    @Override
    public synchronized void onCreate(SQLiteDatabase db) {


    }

    @Override
    public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseConnector.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        onCreate(db);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    //Allergies
    public synchronized void insertStore(Store store) {
        ContentValues newCon = new ContentValues();
        newCon.put("Id", store.getId());
        newCon.put("JSON", store.toJson());
        Log.e("insert", store.getName());
        try {
            open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        database.insert("Stores", null, newCon);
        close();
    }

    public synchronized void updateStore(Store store) {
        ContentValues editCon = new ContentValues();
        editCon.put("Id", store.getId());
        editCon.put("JSON", store.toJson());

        try {
            open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        database.update("Stores", editCon, "Id=\"" + store.getId() + "\"", null);
        close();
    }


    public synchronized void deleteStore(String id) {
        try {
            open();
        } catch (Exception e) {

            e.printStackTrace();
        }
        try {
            curs = database.rawQuery("delete from Stores where Id='" + id + "'", null);
            curs.moveToFirst();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        close();
    }

    public synchronized void deleteAllStores() {
        try {
            open();
        } catch (Exception e) {

            e.printStackTrace();
        }
        try {
            database.delete("Stores", null, null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        close();
    }

    public synchronized Store getStore(String id) {
        try {
            open();
        } catch (Exception e) {

            e.printStackTrace();
        }
        try {
            curs = database.rawQuery("select * from Stores where Id='" + id + "'", null);
            curs.moveToFirst();
            do {
                String myid = curs.getString(0);
                String JSON = curs.getString(1);
                JSONObject jsn = new JSONObject(JSON);
                Store store = new Store();
                store.setId(myid);
                store = store.fromJson(jsn);
                if (id.equals(myid))
                    return store;
            } while (curs.moveToNext());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        close();
        return null;
    }

    public synchronized ArrayList<Store> loadStores() {
        ArrayList<Store> stores = new ArrayList<>();
        try {
            open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            curs = database.rawQuery("select * from Stores", null);
            curs.moveToFirst();
            do {
                String JSON = curs.getString(1);
                JSONObject jsn = new JSONObject(JSON);
                Store store = new Store();
                store.setId(curs.getString(0));
                store = store.fromJson(jsn);
                stores.add(store);

            } while (curs.moveToNext());
            curs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        close();

        return stores;
    }

    //Messages
    public synchronized void insertMessage(Message message) {
        ContentValues newCon = new ContentValues();
        newCon.put("ID", message.getId());
        newCon.put("JSON", message.toJson());

        try {
            open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        database.insert("Messages", null, newCon);
        close();
    }

    public synchronized void updateMessage(Message message) {
        ContentValues editCon = new ContentValues();
        editCon.put("ID", message.getId());
        editCon.put("JSON", message.toJson());

        try {
            open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        database.update("Stores", editCon, "ID=\"" + message.getId() + "\"", null);
        close();
    }


    public synchronized ArrayList<Message> deleteMessagesOlder(String date) {
        ArrayList<Message> messages = loadMessages();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date dateObj = null;
        try {
            dateObj = sdf.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (Message message : messages) {
            Date obj = null;
            try {
                obj = sdf.parse(message.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert dateObj != null;
            assert obj != null;
            int days = (int) ((dateObj.getTime() - obj.getTime()) / (24 * 60 * 60 * 1000));
            if (days >= 31) {
                deleteStore(message.getId());
                messages.remove(message);
            }
        }
        return messages;
    }

    public synchronized ArrayList<Message> loadMessages() {
        ArrayList<Message> messages = new ArrayList<>();
        try {
            open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            curs = database.rawQuery("select * from Messages", null);
            curs.moveToFirst();
            do {
                String JSON = curs.getString(1);
                JSONObject jsn = new JSONObject(JSON);
                Message message = new Message();
                message.setId(curs.getString(0));
                message = message.fromJson(jsn);
                messages.add(message);

            } while (curs.moveToNext());
            curs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        close();

        return messages;
    }

}