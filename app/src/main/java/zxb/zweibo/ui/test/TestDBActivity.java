package zxb.zweibo.ui.test;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.models.Tag;

import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.R;

/**
 * Created by rex on 15-8-6.
 */
public class TestDBActivity extends Activity{

    SQLiteOpenHelper sqliteHelper;

    Button webBtn;
    TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_auth_activity);

        init();

        content = (TextView) findViewById(R.id.tvText);

        webBtn = (Button) findViewById(R.id.web);
        webBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void init() {
        sqliteHelper = new DBHelper(this, "test", null, 1);
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();

        /*Person rex = new Person("Rex", 30);
        Person may = new Person("May", 26);

        Person[] persons = {rex, may};

        db.execSQL("insert into person(name, age) values(?,?)", persons);*/

//        insert(db);
//        db.query()

        Cursor cs = db.rawQuery("select * from jsonobject", null);
        if(cs.moveToNext()){
            String json = cs.getString(0);
            Log.i("TestDB", json);
        }
    }

    private void insert(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("name","xiazdong");
        values.put("age",20);
        db.insert("person",null,values);
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table jsonobject(json varchar(65535) not null);";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    class Person{
        String name;
        int age;

        public Person(String name, int age){
            this.name = name;
            this.age = age;
        }
    }

    class currsor implements SQLiteDatabase.CursorFactory {

        @Override
        public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
            return null;
        }
    }
}
