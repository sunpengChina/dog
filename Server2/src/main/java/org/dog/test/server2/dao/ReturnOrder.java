package org.dog.test.server2.dao;

import lombok.Data;
import org.dog.database.core.annotation.DogTable;
import org.dog.database.core.annotation.QueryArg;
import javax.persistence.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Data
@DogTable(tableName = "returnorder",dbName = "dbname")
@Entity
@Table(name="returnorder")
public class ReturnOrder implements Serializable {

	@QueryArg(argName = "ID")
    @Id
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

	public ReturnOrder() {

	}
}
