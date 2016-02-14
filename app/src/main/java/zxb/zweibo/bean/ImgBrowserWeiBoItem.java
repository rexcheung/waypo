package zxb.zweibo.bean;

import java.io.Serializable;

/**
 * 从FriendsTimeLine页面点击图片后，把该条微博的JSON对象及被点击的图片的位置传给大图浏览的Activity
 * Created by rex on 15-8-14.
 */
public class ImgBrowserWeiBoItem implements Serializable{
    private static final long serialVersionUID = -1169516467401332331L;
    private StatusContent sc;
    private int position;

    public ImgBrowserWeiBoItem(StatusContent sc, int position) {
        this.sc = sc;
        this.position = position;
    }

    public StatusContent getSc() {
        return sc;
    }

    public void setSc(StatusContent sc) {
        this.sc = sc;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
