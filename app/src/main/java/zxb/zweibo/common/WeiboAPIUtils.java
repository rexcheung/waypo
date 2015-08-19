package zxb.zweibo.common;

import android.content.Context;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.StatusesAPI;

/**
 * 用来发送WeiboAPI的类.
 * Created by rex on 15-8-9.
 */
public class WeiboAPIUtils extends StatusesAPI {

    /**
     * 获取当前登录用户及其所关注用户的最新微博的ID
     */
    private String IDS = "https://api.weibo.com/2/statuses/friends_timeline/ids.json";
    private String SHOW = "https://api.weibo.com/2/statuses/show.json";
    private String EMOTIONS = "https://api.weibo.com/2/emotions.json";

    /**
     * 构造函数，使用各个 API 接口提供的服务前必须先获取 Token。
     *
     * @param context     context
     * @param appKey      AppKey
     * @param accessToken accessToken
     */
    public WeiboAPIUtils(Context context, String appKey, Oauth2AccessToken accessToken) {
        super(context, appKey, accessToken);
    }

    /**
     * 获取当前登录用户及其所关注用户的最新微博的ID.
     * 注意：是ID号，不是微博
     * http://open.weibo.com/wiki/2/statuses/friends_timeline/ids.
     *
     * @param since_id    若指定此参数，则返回ID比since_id大的微博（即比since_id时间晚的微博），默认为0。
     * @param max_id      若指定此参数，则返回ID小于或等于max_id的微博，默认为0。
     * @param count       单页返回的记录条数，最大不超过100，默认为20。
     * @param page        返回结果的页码，默认为1。
     * @param base_app    是否只获取当前应用的数据。0为否（所有数据），1为是（仅当前应用），默认为0。
     * @param featureType 过滤类型ID，0：全部、1：原创、2：图片、3：视频、4：音乐，默认为0。
     * @param listener    异步请求回调接口
     */
    public void friendsTimeLineIds(long since_id, long max_id, int count, int page, boolean base_app,
                                   int featureType, RequestListener listener) {
        WeiboParameters params =
                buildTimeLineParamsBase(since_id, max_id, count, page, base_app, featureType);
        requestAsync(IDS, params, HTTPMETHOD_GET, listener);
    }

    protected WeiboParameters buildTimeLineParamsBase(long since_id, long max_id, int count, int page,
                                                      boolean base_app, int featureType) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("since_id", since_id);
        params.put("max_id", max_id);
        params.put("count", count);
        params.put("page", page);
        params.put("base_app", base_app ? 1 : 0);
        params.put("feature", featureType);
        return params;
    }

    /**
     * 获取微博官方表情的详细信息.
     *
     * @param type 表情类别，face：普通表情、ani：魔法表情、cartoon：动漫表情，默认为face。
     * @param language 语言类别，cnname：简体、twname：繁体，默认为cnname。
     * @param listener    异步请求回调接口
     */
    public void getEmotions(String type, String language, RequestListener listener) {
        WeiboParameters params =
                buildEmotionsParams(type, language);
        requestAsync(EMOTIONS, params, HTTPMETHOD_GET, listener);
    }

    protected WeiboParameters buildEmotionsParams(String type, String language) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("type", type);
        params.put("language", language);
        return params;
    }

    /**
     * 发送单条微博的请求.
     * Note: 返回错误信息，因为隐私设置，唯有另外找方法获取单条微博的信息.
     * 原本是打算从这个方法获取该微博里面大图的URL，返回错误信息后，就改为用字符串拼接，
     * 发现原来大图和小图的URL仅是目录不同而已。
     *
     * @param id 该条微博号
     * @param listener 监听器
     */
    public void show(long id, RequestListener listener) {
        WeiboParameters params = buildShowParams(id);
        requestAsync(SHOW, params, HTTPMETHOD_GET, listener);
    }

    protected WeiboParameters buildShowParams(long id) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("id", id);
        return params;
    }
}
