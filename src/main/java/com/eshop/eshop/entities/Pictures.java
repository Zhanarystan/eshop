package com.eshop.eshop.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "pictures")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pictures {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "added_date")
    private Date addedDate;

    @ManyToOne
    private Items item;
}
