package zxb.zweibo.bean;

/**
 * 新浪微博单个表情的JSON实体类
 * Created by rex on 15-8-15.
 */
public class EmotionBean {

    /**
     * phrase : [微笑]
     * Common : true
     * icon : http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/5c/huanglianwx_thumb.gif
     * type : face
     * hot : false
     * category :
     * value : [微笑]
     * url : http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/5c/huanglianwx_org.gif
     * picid :
     */
    private String phrase;
    private String type;
    private String url;
    private boolean hot;
    private boolean common;
    private String category;
    private String icon;
    private String value;
    private String picid;

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }

    public boolean isCommon() {
        return common;
    }

    public void setCommon(boolean common) {
        this.common = common;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPicid() {
        return picid;
    }

    public void setPicid(String picid) {
        this.picid = picid;
    }
}
