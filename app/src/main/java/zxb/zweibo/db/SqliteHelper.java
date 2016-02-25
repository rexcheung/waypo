package zxb.zweibo.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import zxb.zweibo.GlobalApp;
import zxb.zweibo.Utils.EmotionUtil;
import zxb.zweibo.common.EmotionCache;

/**
 * 数据库OpenHelper
 * Created by rex on 15-12-19.
 */
public class SqliteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "weibo.db";

    private static SqliteHelper instance;

    public static SqliteHelper getInstance() {
        if (instance == null) {
            instance = new SqliteHelper();
        }
        return instance;
    }

    private SqliteHelper() {
        super(GlobalApp.getInstance(), DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(buildJsonCacheSQL());
        db.execSQL(buildEmoCacheSQL());
        db.execSQL(buildEmotionSQL());
        db.execSQL(buildFavoriteCacheSQL());
        db.execSQL(buildAtmeSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private String buildJsonCacheSQL() {
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE ");
        sql.append(JsonCacheDao.TABLE).append(" ( ");
        sql.append(JsonCacheDao.USER_ID).append(" VARCHAR(128) NOT NULL, ");
        sql.append(JsonCacheDao.WEIBO_ID).append(" INT(128) NOT NULL, ");
        sql.append(JsonCacheDao.JSON).append(" VARCHAR(5120) NOT NULL ");
        sql.append(" ); ");
        return sql.toString();
    }

    private String buildFavoriteCacheSQL() {
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE ");
        sql.append(FavoriteCacheDao.TABLE).append(" ( ");
        sql.append(FavoriteCacheDao.USER_ID).append(" VARCHAR(128) NOT NULL, ");
        sql.append(FavoriteCacheDao.WEIBO_ID).append(" INT(128) NOT NULL, ");
        sql.append(FavoriteCacheDao.JSON).append(" VARCHAR(5120) NOT NULL ");
        sql.append(" ); ");
        return sql.toString();
    }

    private String buildEmoCacheSQL() {
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE ");
        sql.append(EmotionCache.TABLE + " ( ");
        sql.append(EmotionCache.PHRASE + " VARCHAR(10) NOT NULL, ");
        sql.append(EmotionCache.TYPE + " type VARCHAR(10) NOT NULL, ");
        sql.append(EmotionCache.URL + " url VARCHAR(1024) NOT NULL, ");
        sql.append(EmotionCache.HOT + " hot VARCHAR(10) NOT NULL, ");
        sql.append(EmotionCache.COMMON + " common BOOLEAN(10) NOT NULL, ");
        sql.append(EmotionCache.CATEGORY + " category BOOLEAN(10) NOT NULL, ");
        sql.append(EmotionCache.ICON + " icon VARCHAR(1024) NOT NULL, ");
        sql.append(EmotionCache.VALUE + " value VARCHAR(10) NOT NULL, ");
        sql.append(EmotionCache.PICID + " picid VARCHAR(5120) NOT NULL ");
        sql.append(" ); ");
        return sql.toString();
    }

    private String buildEmotionSQL() {
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE ");
        sql.append(EmotionUtil.TABLE);
        sql.append(" (" + EmotionUtil.KEY + " VARCHAR(10) NOT NULL, ");
        sql.append(" " + EmotionUtil.FILE + " VARCHAR(10) NOT NULL, ");
        sql.append(" " + EmotionUtil.VALUE + " VARCHAR(1024) NOT NULL )");
        return sql.toString();
    }

	private String buildAtmeSQL(){
		StringBuilder sql = new StringBuilder();
		sql.append(" CREATE TABLE ");
		sql.append(AtDao.TABLE).append(" ( ");
		sql.append(AtDao.USER_ID).append(" VARCHAR(128) NOT NULL, ");
		sql.append(AtDao.WEIBO_ID).append(" INT(128) NOT NULL, ");
		sql.append(AtDao.JSON).append(" VARCHAR(5120) NOT NULL ");
		sql.append(" ); ");
		return sql.toString();
	}
}
