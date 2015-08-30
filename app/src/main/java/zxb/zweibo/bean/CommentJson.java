package zxb.zweibo.bean;

/**
 * Created by rex on 15-8-22.
 */
public class CommentJson {
        /**
         * idstr : 3877887620599923
         * source_allowclick : 1
         * floor_num : 19
         * created_at : Thu Aug 20 12:11:58 +0800 2015
         * mid : 3877887620599923
         * source_type : 1
         * id : 3877887620599923
         * text : 好像要//@小米手机:据说@雷军 的坐标已暴露~[偷乐]关注并转发，5台千元旗舰红米Note2疯狂送！
         * source : <a href="http://app.weibo.com/t/feed/3q8xS0" rel="nofollow">魅蓝 note2</a>
         * user : {}
         * status : {}
         */
        public String idstr;
        public int source_allowclick;
        public int floor_num;
        public String created_at;
        public String mid;
        public int source_type;
        public long id;
        public String text;
        public String source;
        public User user;
        public StatusContent status;

}
