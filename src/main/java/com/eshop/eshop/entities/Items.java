package com.eshop.eshop.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Items {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "stars")
    private int stars;

    @Column(name = "small_pic_url")
    private String smallPicURL;

    @Column(name = "large_pic_URL")
    private String largePicURL;

    @Column(name = "added_date")
    Timestamp addedDate;

    @Column(name = "in_top_page")
    boolean inTopPage;

    @ManyToOne(fetch = FetchType.LAZY)
    private Brands brand;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Categories> categories;


    public int getFullStars(){
        return (int)(stars);
    }

    public int getHalfStars(){
        return (stars-getFullStars() >= 0.5 ? 1 : 0);
    }

    public int getEmptyStars(){
        return 5 - getFullStars() - getHalfStars();
    }
}
