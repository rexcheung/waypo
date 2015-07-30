package zxb.zweibo.bean;

import java.io.Serializable;

/**
 * 微博的可见性及指定可见分组信息。该object中type取值，
 * 0：普通微博，1：私密微博，3：指定分组微博，4：密友微博；list_id为分组的组号
 * 从AiSen开源项目复制过来
 */
public class Visible implements Serializable {

	private static final long serialVersionUID = 7285113172667412284L;

	private String type;

	private String list_id;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getList_id() {
		return list_id;
	}

	public void setList_id(String list_id) {
		this.list_id = list_id;
	}

}
