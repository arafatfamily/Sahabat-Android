package com.sahabat.mobile.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by GILBERT on 30/07/2017.
 */

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "databases";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE `user` (`id` INTEGER PRIMARY KEY, `member_id` INTEGER , `membership_id` TEXT, `member_name` TEXT, `gender` TEXT, `birth_place` TEXT, `date_of_birth` TEXT, `is_married` TEXT, `blood_type` TEXT,  `occupation` TEXT,  `religion` TEXT,  `last_education_id` INTEGER,  `address` TEXT,  `home_number` TEXT,  `rt` TEXT, `rw` TEXT,  `sub_district_id` TEXT, `postal_code` INTEGER, `is_domisili` TEXT, `couple_name` TEXT, `children_name` TEXT, `cellular_phone_number` TEXT, `home_phone_number` TEXT, `email` TEXT UNIQUE, `facebook` TEXT, `twitter` TEXT, `registered_time` TEXT, `reference` TEXT, `last_print` TEXT, `is_have_position` TEXT, `is_other_position` TEXT)";
        db.execSQL(CREATE_LOGIN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }

    public void addUser(int member_id, String membership_id, String member_name, String gender, String birth_place, String date_of_birth, String is_married, String blood_type, String occupation, String religion, int last_education_id, String address, String home_number, String rt, String rw, String sub_district_id, int postal_code, String is_domisili, String couple_name, String children_name, String cellular_phone_number, String home_phone_number, String email, String facebook, String twitter, String registered_time, String reference, String last_print, String is_have_position, String is_other_position) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("member_id", member_id);
        values.put("membership_id", membership_id);
        values.put("member_name", member_name);
        values.put("gender", gender);
        values.put("birth_place", birth_place);
        values.put("date_of_birth", date_of_birth);
        values.put("is_married", is_married);
        values.put("blood_type", blood_type);
        values.put("occupation", occupation);
        values.put("religion", religion);
        values.put("last_education_id", last_education_id);
        values.put("address", address);
        values.put("home_number", home_number);
        values.put("rt", rt);
        values.put("rw", rw);
        values.put("sub_district_id", sub_district_id);
        values.put("postal_code", postal_code);
        values.put("is_domisili", is_domisili);
        values.put("couple_name", couple_name);
        values.put("children_name", children_name);
        values.put("cellular_phone_number", cellular_phone_number);
        values.put("home_phone_number", home_phone_number);
        values.put("email", email);
        values.put("facebook", facebook);
        values.put("twitter", twitter);
        values.put("registered_time", registered_time);
        values.put("reference", reference);
        values.put("last_print", last_print);
        values.put("is_have_position", is_have_position);
        values.put("is_other_position", is_other_position);

        long id = db.insert("user", null, values);
        db.close();
    }

    public HashMap<String, String> getUserData(String columns) {
        HashMap<String, String> column = new HashMap<String, String>();
        String selectQuery = "SELECT " + columns + "FROM user";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            column.put(columns, cursor.getString(1));
        }
        cursor.close();
        db.close();

        return column;
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM user";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("member_id", cursor.getString(1));
            user.put("membership_id", cursor.getString(2));
            user.put("member_name", cursor.getString(3));
            user.put("gender", cursor.getString(4));
            user.put("birth_place", cursor.getString(5));
            user.put("date_of_birth", cursor.getString(6));
            user.put("is_married", cursor.getString(7));
            user.put("blood_type", cursor.getString(8));
            user.put("occupation", cursor.getString(9));
            user.put("religion", cursor.getString(10));
            user.put("last_education_id", cursor.getString(11));
            user.put("address", cursor.getString(12));
            user.put("home_number", cursor.getString(13));
            user.put("rt", cursor.getString(14));
            user.put("rw", cursor.getString(15));
            user.put("sub_district_id", cursor.getString(16));
            user.put("postal_code", cursor.getString(17));
            user.put("is_domisili", cursor.getString(18));
            user.put("couple_name", cursor.getString(19));
            user.put("children_name", cursor.getString(20));
            user.put("cellular_phone_number", cursor.getString(21));
            user.put("home_phone_number", cursor.getString(22));
            user.put("email", cursor.getString(23));
            user.put("facebook", cursor.getString(24));
            user.put("twitter", cursor.getString(25));
            user.put("registered_time", cursor.getString(26));
            user.put("reference", cursor.getString(27));
            user.put("last_print", cursor.getString(28));
            user.put("is_have_position", cursor.getString(29));
            user.put("is_other_position", cursor.getString(30));
        }
        cursor.close();
        db.close();

        return user;
    }

    public void deleteUsers(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, null, null);
        db.close();
    }

}
