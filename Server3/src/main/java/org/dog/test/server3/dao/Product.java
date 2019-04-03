package org.dog.test.server3.dao;

import lombok.Data;
import org.dog.database.core.annotation.DogTable;
import org.dog.database.core.annotation.QueryArg;

@Data
@DogTable(tableName = "Product",dbName = "dbname")
public class Product {

    @QueryArg(argName = "name")
    private  String name;

    @QueryArg(argName = "vender")
    private String vender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVender() {
        return vender;
    }

    public void setVender(String vender) {
        this.vender = vender;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    private int  price;

    public Product(String name, String vender, int price) {
        this.name = name;
        this.vender = vender;
        this.price = price;
    }
}
