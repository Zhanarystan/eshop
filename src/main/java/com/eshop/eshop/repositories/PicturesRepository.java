package com.eshop.eshop.repositories;

import com.eshop.eshop.entities.Items;
import com.eshop.eshop.entities.Pictures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
@Transactional
public interface PicturesRepository extends JpaRepository<Pictures, Long> {
    List<Pictures> findAllByItem(Items item);
}
