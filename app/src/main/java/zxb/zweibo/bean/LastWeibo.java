package zxb.zweibo.bean;

/**
 * Created by rex on 15-8-26.
 */
public class LastWeibo {
    Long lastId;

    public LastWeibo(Long lastId) {
        this.lastId = lastId;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }
}
