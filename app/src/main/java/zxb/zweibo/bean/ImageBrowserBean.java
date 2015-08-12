package zxb.zweibo.bean;

import zxb.zweibo.common.ImageUtil;

/**
 * Created by rex on 15-8-11.
 */
public class ImageBrowserBean {
    private String smallPic;
    private String middlePic;
    private ImageUtil imgUtil;

    public ImageBrowserBean() {
    }

    public ImageBrowserBean(String smallPic, String middlePic) {
        this.smallPic = smallPic;
        this.middlePic = middlePic;
    }

    public ImageUtil getImgUtil() {
        return imgUtil;
    }

    public void setImgUtil(ImageUtil imgUtil) {
        this.imgUtil = imgUtil;
    }

    public String getSmallPic() {
        return smallPic;
    }

    public void setSmallPic(String smallPic) {
        this.smallPic = smallPic;
    }

    public String getMiddlePic() {
        return middlePic;
    }

    public void setMiddlePic(String middlePic) {
        this.middlePic = middlePic;
    }
}
