package zxb.zweibo.bean;

import java.util.List;

/**
 * Created by rex on 15-8-9.
 */
public class FTLIds {

    /**
     * previous_cursor : 0
     * total_number : 150
     * next_cursor : 3873871701049103
     * ad : []
     * uve_blank : -1
     * hasvisible : false
     * advertises : []
     * statuses : ["3873880122836872","3873879652917655","3873879279553707","3873879225494917","3873879120289362","3873878973977500","3873878441190447","3873877631725379","3873877237321637","3873877031479826","3873876959822515","3873875785447600","3873875706168330","3873875248600674","3873875140450782","3873875119474005","3873874070006357","3873873478636784","3873872711393593","3873871939727588"]
     * interval : 0
     */
    public long previous_cursor;
    public int total_number;
    public long next_cursor;
//    public List<?> ad;
    public int uve_blank;
    public boolean hasvisible;
//    public List<?> advertises;
    public List<Long> statuses;
    public int interval;

    public List<Long> getStatuses() {
        return statuses;
    }
}
