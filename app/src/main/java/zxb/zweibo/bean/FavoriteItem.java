package zxb.zweibo.bean;

import java.util.List;

/**
 * Created by rex on 15-8-28.
 */
public class FavoriteItem {

    /**
     * favorited_time : Fri Sep 21 13:42:00 +0800 2012
     * status : {}
     * tags : []
     */
    private String favorited_time;
    private StatusContent status;
    private List<Tag> tags;

    public String getFavorited_time() {
        return favorited_time;
    }

    public FavoriteItem setFavorited_time(String favorited_time) {
        this.favorited_time = favorited_time;
        return this;
    }

    public StatusContent getStatus() {
        return status;
    }

    public FavoriteItem setStatus(StatusContent status) {
        this.status = status;
        return this;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public FavoriteItem setTags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }
}
