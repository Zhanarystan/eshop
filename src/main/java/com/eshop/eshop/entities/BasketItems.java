package com.eshop.eshop.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "basket_items")
@Data
public class BasketItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "item_id")
    private Long item_id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_price")
    private double itemPrice;

    @Column(name = "amount")
    private int amount;

    @Column(name = "sold_date")
    private Date soldDate;

    public BasketItems(){}

    public BasketItems(Long item_id,String itemName,double itemPrice, int amount){
        this.item_id=item_id;
        this.itemName=itemName;
        this.itemPrice=itemPrice;
        this.amount=amount;
    }

}
