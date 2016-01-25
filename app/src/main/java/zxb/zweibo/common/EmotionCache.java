package zxb.zweibo.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import zxb.zweibo.bean.EmotionBean;
import zxb.zweibo.bean.EmotionsBean;
import zxb.zweibo.db.SqliteHelper;

/**
 * 表情的JSON缓存，
 * 新浪微博会一次把一个系列的表情都发过来，
 * 基本上头一次连网，之后就使用缓存就可以了。
 * Created by rex on 15-8-15.
 */
public class EmotionCache {

    public static final String TABLE = "emocache";
    public static final String PHRASE = "phrase";
    public static final String TYPE = "type";
    public static final String URL = "url";
    public static final String HOT = "hot";
    public static final String COMMON = "common";
    public static final String CATEGORY = "category";
    public static final String ICON = "icon";
    public static final String VALUE = "value";
    public static final String PICID = "picid";


    private Oauth2AccessToken mAccessToken;
    private Context mContext;
    private SQLiteOpenHelper mdbHelper;
    private SQLiteDatabase db;


    Gson gson;

    private String TAG = getClass().getSimpleName();

    public EmotionCache(Context context, Gson gson) {
        this.mContext = context;
        this.gson = gson;
//        mAccessToken = new Oauth2AccessToken();
        mAccessToken = AccessTokenKeeper.readAccessToken(context);
    }

    public void insert(EmotionBean[] emoArr) {
        if (emoArr == null || emoArr.length == 0) {
            return;
        }

        db.beginTransaction();

        int length = emoArr.length;
        for (int i = 0; i < length; i++) {
            EmotionBean emotionBean = emoArr[i];
            ContentValues values = new ContentValues();
            values.put("phrase", emotionBean.getPhrase());
            values.put("type", emotionBean.getType());
            values.put("url", emotionBean.getUrl());
            values.put("hot", emotionBean.isHot());
            values.put("common", emotionBean.isCommon());
            values.put("category", emotionBean.getCategory());
            values.put("icon", emotionBean.getIcon());
            values.put("value", emotionBean.getValue());
            values.put("picid", emotionBean.getPicid());
            db.insert(TABLE, null, values);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void getEmo(final String emo) {

        String emoUrl = getEmoFromCache(emo);
        if (TextUtils.isEmpty(emoUrl)) {
            //从网络加载
            WeiboAPIUtils mWeiboAPI = new WeiboAPIUtils(mContext, Constants.APP_KEY, mAccessToken);
            mWeiboAPI.getEmotions("face", "cnname", new RequestListener() {
                @Override
                public void onComplete(String jsonString) {
                    String json = jsonString;
                    EmotionBean[] emotionsBean = gson.fromJson(json, EmotionBean[].class);
                    if (emotionsBean != null || emotionsBean.length != 0) {
                        insert(emotionsBean);
                    }
                    Log.i("", "");
                }

                @Override
                public void onWeiboException(WeiboException e) {
                }
            });
        } else {

        }
    }

    public String getEmoFromCache(String emo) {
        String url = null;
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT url FROM");
        sql.append(TABLE);
        sql.append(" WHERE phrase = ?");

        Cursor cursor = db.rawQuery(sql.toString(), new String[]{emo});
        if (cursor.moveToNext()) {
            url = cursor.getString(0);
        }
        cursor.close();

        return url;
    }

    public void initDB() {
        Log.i(TAG, "+++初始化EmotionCache.db");
        if (mdbHelper == null) {
            mdbHelper = SqliteHelper.getInstance();
        }
        db = mdbHelper.getWritableDatabase();
    }

    public void closeDB() {
        if (db.isOpen()) {
            db.close();
        }

        mdbHelper.close();
        Log.i(TAG, "---关闭数据库EmotionCache.db");
    }


    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            StringBuilder sqlString = new StringBuilder();
            sqlString.append(" create table ");
            sqlString.append(TABLE);
            sqlString.append(" (phrase varchar(10) not null, ");
            sqlString.append(" type varchar(10) not null, ");
            sqlString.append(" url varchar(1024) not null, ");
            sqlString.append(" hot varchar(10) not null, ");
            sqlString.append(" common boolean(10) not null, ");
            sqlString.append(" category boolean(10) not null, ");
            sqlString.append(" icon varchar(1024) not null, ");
            sqlString.append(" value varchar(10) not null, ");
            sqlString.append(" picid varchar(5120) not null); ");
            db.execSQL(sqlString.toString());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
