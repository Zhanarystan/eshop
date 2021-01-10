package com.eshop.eshop.repositories;

import com.eshop.eshop.entities.Brands;
import com.eshop.eshop.entities.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface BrandsRepository extends JpaRepository<Brands,Long> {

    List<Brands> findAllByIdGreaterThanOrderByNameAsc(Long id);

}
