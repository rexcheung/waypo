package zxb.zweibo.biz;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import zxb.zweibo.Utils.GsonUtils;
import zxb.zweibo.bean.FTLIds;
import zxb.zweibo.bean.FTimeLine;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.WeiboAPIUtils;

/**
 * Created by Rex.Zhang on 2016/2/22.
 */
public class RxAtBiz implements IRxAtBiz {

	private List<Long> mIds;
	private int page = 1;

	public static RxAtBiz newInstance() {
		RxAtBiz b = new RxAtBiz();
		b.mIds = new ArrayList<>();
		return b;
	}

	@Override
	public Observable<List<StatusContent>> getNextPage(long currentLast) {
		// 初始化
		if (mIds != null && mIds.size() == 0) {

			// 获取Ids Json
			return WeiboAPIUtils.getInstance().reqAtMeIds(page++)
					// json to object
					.map(new Func1<String, List<Long>>() {
						@Override
						public List<Long> call(String s) {
							return json2Ids(s);
						}
					})
					// 过滤
					.filter(new Func1<List<Long>, Boolean>() {
						@Override
						public Boolean call(List<Long> ids) {
							return ids != null;
						}
					})
					// compare list
					.map(new Func1<List<Long>, List<Long>>() {
						@Override
						public List<Long> call(List<Long> ids) {
							return replaceIds(ids);
						}
					})
					//请求JSON
					.map(new Func1<List<Long>, String>() {
						@Override
						public String call(List<Long> longs) {
							return requestAtMe(2);
						}
					})
					// JSON 转换为 List
					.map(new Func1<String, List<StatusContent>>() {
						@Override
						public List<StatusContent> call(String s) {
							FTimeLine ftl = GsonUtils.fromJson(s, FTimeLine.class);
							return ftl.getStatuses();
						}
					})
					// 过滤
					.filter(new Func1<List<StatusContent>, Boolean>() {
						@Override
						public Boolean call(List<StatusContent> statusContents) {
							return statusContents != null && statusContents.size() > 0;
						}
					});
		}

		return null;
	}

	@Override
	public Observable<List<StatusContent>> refresh() {
		return null;
	}

	private List<Long> json2Ids(String json) {
		FTLIds tempIds = GsonUtils.fromJson(json, FTLIds.class);
		if (tempIds != null) {
			if (tempIds.getStatuses().size() != 0) {
				return tempIds.getStatuses();
			}
		}
		return null;
	}

	private List<Long> replaceIds(List<Long> ids) {
		if (mIds.size() == 0) {
			mIds.addAll(ids);
			return mIds;
		}

		if (ids.get(0) == mIds.get(0)) {
			ids.clear();
			return mIds;
		} else {
			mIds.clear();
			mIds.addAll(ids);
		}
		return mIds;
	}

	private Observable<List<StatusContent>> getAtmeList(long lastId) {
		return WeiboAPIUtils.getInstance().reqAtme(1)
				.map(new Func1<String, List<StatusContent>>() {
					@Override
					public List<StatusContent> call(String s) {
						FTimeLine ftl = GsonUtils.fromJson(s, FTimeLine.class);
						List<StatusContent> tempList = ftl.getStatuses();
						return tempList;
					}
				});
	}

	/**
	 * 遍历ID列表，找到与lastId区的ID下标。
	 *
	 * @param lastId 当前页面最后的ID
	 *
	 * @return 匹配的下标。
	 */
	private int getStart(long lastId) {
		int start = 0;
		for (int i = 0; i < mIds.size(); i++) {
			if (lastId == mIds.get(i)) {
				start = i;
			}
		}
		return start;
	}

	final int COUNT = 50;

	private List<Long> questList(int currentId) {
		int pos = getStart(currentId);
		ArrayList<Long> ids = new ArrayList<>();
		for (int i = 0, p = pos + 1; i < COUNT; i++, p++) {
			ids.add(mIds.get(p));
		}
		return ids;
	}

	private List<StatusContent> getCaches(){
		return null;
	}

	private String requestAtMe(long currentId) {
		String json = WeiboAPIUtils.getInstance().syncAtMe(1);
		return json;
	}
}
