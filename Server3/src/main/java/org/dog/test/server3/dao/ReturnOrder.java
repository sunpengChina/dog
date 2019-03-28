package org.dog.test.server3.dao;

import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @ClassName: ReturnOrder
 * @Description:
 * @Author: ZhangLingKe
 * @CreateDate: 2019/3/19 11:50
 * @Version: 1.0
 */
@Data
public class ReturnOrder {


	private String id;
	
	private String other;

	public ReturnOrder(String id, String other) {
		super();
		this.id = id;
		this.other = other;
	}

}
