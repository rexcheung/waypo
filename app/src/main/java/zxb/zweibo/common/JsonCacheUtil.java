package zxb.zweibo.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.bean.JsonCache;
import zxb.zweibo.bean.StatusContent;


/**
 * Created by rex on 15-8-6.
 */
public class JsonCacheUtil {

    private Context mContext;
    private SQLiteOpenHelper mdbHelper;
    private SQLiteDatabase db;

    private int COL_ID = 0;
    private int COL_TIME = 1;
    private int COL_JSON = 2;
    private int HALFHOUR = 1800000;

    Gson gson;

    private String TAG = getClass().getSimpleName();

    public JsonCacheUtil(Context context) {
        this.mContext = context;
        initDB();
        gson = new Gson();
    }

    private void initDB(){
        mdbHelper = new DBHelper(mContext, "jsoncache", null, 1);
        db = mdbHelper.getWritableDatabase();
    }

    public void write(String id, List<StatusContent> jsonList) {
        if (jsonList == null || jsonList.size() == 0){
            Log.i(TAG, "没有Json数据可写入缓存");
        }
        initDB();

//        long timeMillis = System.currentTimeMillis();

        db.beginTransaction();
        for (StatusContent sc : jsonList){
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("time", System.currentTimeMillis());
            values.put("json", gson.toJson(sc));
            db.insert("jsonobject",null,values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();

        closeDB();
    }


    /**
     * 根据登陆用户ID，返回缓存对象.
     *
     * @param id UserId
     * @return 有则返回List, 无则为Null
     */
    public ArrayList<StatusContent> read(String id) {
        if (id.isEmpty() || id == null){
            return null;
        }

        initDB();

        ArrayList<StatusContent> jsonList = new ArrayList<>();
        Cursor cs = db.rawQuery("select * from jsonobject where id=?", new String[]{id});
        while (cs.moveToNext()) {
//            JsonCache jsonCache = new JsonCache();
//            jsonCache.setId(cs.getString(COL_ID));
//            jsonCache.setCreateTime(cs.getInt(COL_CREATE_TIME));
//            jsonCache.setJson(cs.getString(COL_JSON));

            StatusContent statusContent = gson.fromJson(cs.getString(COL_JSON), StatusContent.class);
            jsonList.add(statusContent);
        }
        cs.close();

        if (jsonList.size() == 0){
            return null;
        }

        closeDB();
        return jsonList;
    }

    public void closeDB(){
        if(db.isOpen()){
            db.close();
        }

        mdbHelper.close();
    }
}

class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = " create table jsonobject(id varchar(128) not null, " +
                " time int(128) not null ," +
                " json varchar(5120) not null);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
