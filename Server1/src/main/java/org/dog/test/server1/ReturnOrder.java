package org.dog.test.server1;

import java.io.Serializable;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.core.core.mapping.Document;

/**
 * @ClassName: ReturnOrder
 * @Description:
 * @Author: ZhangLingKe
 * @CreateDate: 2019/3/19 11:50
 * @Version: 1.0
 */

public class ReturnOrder implements Serializable {


	private String id;

	private String other;

	public ReturnOrder(String id, String other) {
		super();
		this.id = id;
		this.other = other;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}
}
