package zxb.zweibo.bean;

/**
 * Created by rex on 15-8-6.
 */
public class JsonCache {

    private String id;
    private double createTime;
    private String json;

    public JsonCache() {
    }

    public JsonCache(String id, double createTime, String json) {
        this.id = id;
        this.createTime = createTime;
        this.json = json;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getCreateTime() {
        return createTime;
    }

    public void setCreateTime(double createTime) {
        this.createTime = createTime;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
