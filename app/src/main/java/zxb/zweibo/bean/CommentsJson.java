package zxb.zweibo.bean;

import java.util.List;

/**
 * Created by rex on 15-8-20.
 */
public class CommentsJson {

    /**
     * previous_cursor : 0
     * total_number : 19
     * next_cursor : 0
     * comments : []
     * hasvisible : false
     * marks : []
     */
    public long previous_cursor;
    public long total_number;
    public long next_cursor;
    public List<CommentJson> comments;
    public boolean hasvisible;
    public List<?> marks;

}
