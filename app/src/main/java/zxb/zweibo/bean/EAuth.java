package zxb.zweibo.bean;

/**
 * Created by rex on 15-7-29.
 */
public class EAuth {

    public static int SUCCESS = 0x01;
    public static int FAIL = 0x02;

    public EAuth() {}

    public EAuth(int code) {
        this.code = code;
    }

    int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
