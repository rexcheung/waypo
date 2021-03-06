package zxb.zweibo.common;

import android.content.Context;
import android.text.TextUtils;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.StatusesAPI;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import zxb.zweibo.GlobalApp;
import zxb.zweibo.bean.StatusContent;

/**
 * 用来发送WeiboAPI的类.
 * Created by rex on 15-8-9.
 */
public class WeiboAPIUtils extends StatusesAPI {

    /**
     * 获取当前登录用户及其所关注用户的最新微博的ID
     */
	private static final String WEIBO_URL = "https://api.weibo.com/2/";
    private static final String IDS = WEIBO_URL + "statuses/friends_timeline/ids.json";
    private static final String SHOW = WEIBO_URL + "statuses/show.json";
    private static final String EMOTIONS = WEIBO_URL + "emotions.json";
    private static final String COMMENT = WEIBO_URL + "comments/show.json";
    private static final String ADD_FAVORTIE = WEIBO_URL + "favorites/create.json";
    private static final String FORWARD = WEIBO_URL + "statuses/repost.json";
    private static final String GET_FAVORITE = WEIBO_URL + "favorites.json";
    private static final String GET_FAVORITE_IDS = WEIBO_URL + "favorites/ids.json";
    private static final String GET_USER_IDS = WEIBO_URL + "statuses/user_timeline/ids.json";
    private static final String GET_USER_WEIBO = WEIBO_URL + "statuses/user_timeline.json";
    private static final String AT_ME_IDS = WEIBO_URL + "statuses/mentions/ids.json";
    private static final String AT_ME = WEIBO_URL + "statuses/mentions.json";

	public static class WEIBO_KEY {
		public static final String PAGE = "page";
		public static final String COUNT = "count";
	}



    private static Oauth2AccessToken mAccessToken;
    private static WeiboAPIUtils instance;

    public static WeiboAPIUtils getInstance() {
		if (instance==null){
			initWeiboAPI();
		}
        return instance;
    }

    public static Oauth2AccessToken getAccessToken() {
        return mAccessToken;
    }

	/**
	 * 初始化微博SDK，这个需要在启动APP后第一个页面启用一次，
	 * 如果在Application里面调用，会在首次授权后进入App无法获取正确授权信息
	 * 所以在授权后的第一页面调用。
	 */
    public static void initWeiboAPI() {
        instance = new WeiboAPIUtils(GlobalApp.getInstance(),
                Constants.APP_KEY,
                AccessTokenKeeper.readAccessToken(GlobalApp.getInstance()));
    }

    /**
     * 构造函数，使用各个 API 接口提供的服务前必须先获取 Token。
     *
     * @param context     context
     * @param appKey      AppKey
     * @param accessToken accessToken
     */
    public WeiboAPIUtils(Context context, String appKey, Oauth2AccessToken accessToken) {
        super(context, appKey, accessToken);
        mAccessToken = accessToken;
        if (instance == null) {
            instance = this;
        }
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
    public void imageFTLIds(long since_id, long max_id, int count, int page, boolean base_app,
                            int featureType, RequestListener listener) {
        WeiboParameters params =
                buildTimeLineParamsBase(since_id, max_id, count, page, base_app, featureType);
        this.requestAsync(IDS, params, HTTPMETHOD_GET, listener);
    }

    /**
     * 过滤类型ID，
     * 0：全部、
     * 1：原创、
     * 2：图片、
     * 3：视频、
     * 4：音乐，
     * 默认为0。
     */
    class FilterType {
        public static final int ALL = 0;
        public static final int ORIGINAL = 1;
        public static final int PIC = 2;
        public static final int VIDEO = 3;
        public static final int MUSIC = 4;
    }

    /**
     * 返回最新的100条图片微博ID。
     *
     * @param listener 监听器。
     */
    public void reqNewIds(RequestListener listener) {
        WeiboParameters params =
                buildTimeLineParamsBase(0L, 0L, 100, 1, false, FilterType.PIC);
        this.requestAsync(IDS, params, HTTPMETHOD_GET, listener);
    }

    /**
     * 获取100条微博的ID， 微博的类型为图片
     *
     * @param since_id 若指定此参数，则返回ID比since_id大的微博（即比since_id时间晚的微博），默认为0。
     * @param max_id   若指定此参数，则返回ID小于或等于max_id的微博，默认为0。
     * @param listener 异步请求回调接口
     */
    public void imageFTLIds(long since_id, long max_id, RequestListener listener) {
        WeiboParameters params =
                buildTimeLineParamsBase(since_id, max_id, 100, 1, false, 2);
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
     * @param type     表情类别，face：普通表情、ani：魔法表情、cartoon：动漫表情，默认为face。
     * @param language 语言类别，cnname：简体、twname：繁体，默认为cnname。
     * @param listener 异步请求回调接口
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
     * @param id       该条微博号
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

    /**
     * https://api.weibo.com/2/comments/show.json
     * id	true	int64	需要查询的微博ID。
     * since_id	false	int64	若指定此参数，则返回ID比since_id大的评论（即比since_id时间晚的评论），默认为0。
     * max_id	false	int64	若指定此参数，则返回ID小于或等于max_id的评论，默认为0。
     * count	false	int	单页返回的记录条数，默认为50。
     * page	false	int	返回结果的页码，默认为1。
     * filter_by_author	false	int	作者筛选类型，0：全部、1：我关注的人、2：陌生人，默认为0。
     */
    public void requestCommentsById(long id, RequestListener listener) {
        WeiboParameters params = buildCommentParams(id);
        requestAsync(COMMENT, params, HTTPMETHOD_GET, listener);
    }

    /**
     * 除了ID，全部都是默认参数
     *
     * @param id ID
     * @return WeiboParameters
     */
    protected WeiboParameters buildCommentParams(long id) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("id", id);
        params.put("since_id", 0);
        params.put("max_id", 0);
        params.put("count", 50);
        params.put("page", 1);
        params.put("filter_by_author", 0);
        return params;
    }


    /**
     * 收藏一条微博.
     *
     * @param id true	int64	要收藏的微博ID。
     */
    public void addFavorites(Long id, RequestListener listener) {
        WeiboParameters params = buildFavoriteParams(id);
        requestAsync(ADD_FAVORTIE, params, HTTPMETHOD_POST, listener);
    }

    protected WeiboParameters buildFavoriteParams(long id) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("id", id);
        return params;
    }


    /**
     * 转发当前微博， 做了些默认的处理，就是没有传入的参数.
     *
     * @param id         true	int64	要转发的微博ID。
     * @param status     false	string	添加的转发文本，必须做URLencode，内容不超过140个汉字，不填则默认为“转发微博”。
     */
    public void forward(Long id, String status, RequestListener listener) {
        WeiboParameters params = buildForwardParams(id, status);
        requestAsync(FORWARD, params, HTTPMETHOD_POST, listener);
    }

    protected WeiboParameters buildForwardParams(long id, String status) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("id", id);   //要转发的微博ID
        if (!TextUtils.isEmpty(status)) {
            params.put("status", status);   //添加的转发文本
        }
//        params.put("is_comment", 0);   //是否在转发的同时发表评论
//        params.put("rip", "");   //开发者上报的操作用户真实IP
        return params;
    }

    /**
     * 请求收藏微博ID列表
     * @param listener 监听器。
     */
    public void reqFavoritesIds(RequestListener listener){
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("count", 100);   //单页返回的记录条数
        params.put("page", 1);
        requestAsync(GET_FAVORITE_IDS, params, HTTPMETHOD_GET, listener);
    }

    /**
     * 获取当前登录用户的收藏列表.
     *
     * @param count false	int	单页返回的记录条数，默认为50。
     * @param page  false	int	返回结果的页码，默认为1。
     */
    public void reqFavorites(int count, int page, RequestListener listener) {
        WeiboParameters params = buildFavoritesParams(count, page);
        requestAsync(GET_FAVORITE, params, HTTPMETHOD_GET, listener);
    }

    protected WeiboParameters buildFavoritesParams(int count, int page) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        if (count != 0) {
            params.put("count", count);   //单页返回的记录条数
        }
        params.put("page", page);   //返回结果的页码
        return params;
    }

    public void reqFTL(long since, RequestListener l){
        friendsTimeline(0, since, WayPoConstants.PER_PAGE_COUNT, 1, false, 2, false, l);
    }

	public String syncUserIds(String uid, String name){
		WeiboParameters params = buildUserIdsParams(uid, name);
		return requestSync(GET_USER_IDS, params, HTTPMETHOD_GET);
	}

	/**
	 * 请求当前最新微博的ID
	 * 现在只能用接收自己的最新微博，不能指定某些用户的。
	 * @param sc
	 * @return
	 */
	public Observable<String> reqUserIds(StatusContent sc){
		return Observable.just(sc)
				.map(new Func1<StatusContent, String>() {
					@Override
					public String call(StatusContent statusContent) {
//						String uid = statusContent.getUser().getId();
//						String screenName = statusContent.getUser().getScreen_name();
//						return syncUserIds(uid, screenName);
						return syncUserIds(getUserId(), "");
					}
				});
	}

	private WeiboParameters buildUserIdsParams(String uid, String screenName){
		WeiboParameters params = new WeiboParameters(mAppKey);
		params.put("uid", uid);
		params.put("screen_name", screenName);
		params.put("page", 2);

		return params;
	}

	public String syncUserWeibos(String uid, String name){
		WeiboParameters params = buildUserIdsParams(uid, name);
		return requestSync(GET_USER_WEIBO, params, HTTPMETHOD_GET);
	}

	public Observable<String> reqUserWeibos(StatusContent sc){
		return Observable.just(sc)
				.map(new Func1<StatusContent, String>() {
					@Override
					public String call(StatusContent statusContent) {
//						return syncUserWeibos(statusContent.getUser().getId(), statusContent.getUser().getScreen_name());
						return syncUserWeibos(getUserId(), "大雄Rex");
					}
				});
	}

	public String syncAtMeIds(int page){
		WeiboParameters params = new WeiboParameters(mAppKey);
		params.put(WEIBO_KEY.PAGE, page);
		params.put(WEIBO_KEY.COUNT, 200);
		return requestSync(AT_ME_IDS, params, HTTPMETHOD_GET);
	}

	public Observable<String> reqAtMeIds(final int page){
		return Observable.create(new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				String json = syncAtMeIds(page);
				if (TextUtils.isEmpty(json)){
					subscriber.onError(new RuntimeException("返回信息错误"));
				} else {
					subscriber.onNext(json);
				}
			}
		});
	}

	/**
	 * 获取@我的50条微博
	 * @param page 页数。
	 * @return
	 */
	public String syncAtMe(int page){
		WeiboParameters params = new WeiboParameters(mAppKey);
		params.put(WEIBO_KEY.PAGE, page);
		params.put(WEIBO_KEY.COUNT, 200);
		return requestSync(AT_ME, params, HTTPMETHOD_GET);
	}

	public Observable<String> reqAtme(int page){
		return Observable.just(page)
				.map(new Func1<Integer, String>() {
					@Override
					public String call(Integer integer) {
						return syncAtMe(integer);
					}
				});
	}

    public Oauth2AccessToken getmAccessToken() {
        return mAccessToken;
    }

    public static String getUserId(){
        return mAccessToken.getUid();
    }
}
