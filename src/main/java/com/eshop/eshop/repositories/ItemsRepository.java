package com.eshop.eshop.repositories;

import com.eshop.eshop.entities.Categories;
import com.eshop.eshop.entities.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Id;
import java.util.List;

@Repository
@Transactional
public interface ItemsRepository extends JpaRepository<Items,Long> {

    List<Items> findAllByNameLikeOrderByPriceDesc(String name);

    List<Items> findAllByNameLikeOrderByPriceAsc(String name);

    List<Items> findAllByPriceGreaterThanOrderByAddedDateDesc(double price);

    List<Items> findAllByNameLikeAndPriceBetweenOrderByPriceAsc(String name, double price1, double price2);

    List<Items> findAllByNameLikeAndPriceBetweenOrderByPriceDesc(String name, double price1, double price2);

    List<Items> findAllByInTopPageOrderByAddedDateDesc(boolean inTopPage);

    List<Items> findAllByNameLikeAndBrandNameEqualsAndPriceGreaterThanOrderByPriceDesc(String name,String brandname,double price);

    List<Items> findAllByBrandNameOrderByPriceAsc(String name);
    List<Items> findAllByBrandNameOrderByPriceDesc(String name);
    List<Items> findAllByNameLikeAndBrandNameEqualsAndPriceBetweenOrderByPriceAsc(String name,String brandname, double price1, double price2);
    List<Items> findAllByNameLikeAndBrandNameEqualsAndPriceBetweenOrderByPriceDesc(String name,String brandname, double price1, double price2);
    List<Items> findAllByNameLikeAndBrandNameEqualsOrderByPriceAsc(String name,String brandname);
    List<Items> findAllByNameLikeAndBrandNameEqualsOrderByPriceDesc(String name,String brandname);

    List<Items> findAllByPriceGreaterThanOrderByPriceDesc(double price);

    List<Items> findAllByNameLikeAndPriceGreaterThanEqualAndPriceLessThanEqualAndBrandNameLikeOrderByPriceAsc(String name,double price1,double price2,String brandname);
    List<Items> findAllByNameLikeAndPriceGreaterThanEqualAndPriceLessThanEqualAndBrandNameLikeOrderByPriceDesc(String name,double price1,double price2,String brandname);

    List<Items> findAllByCategoriesOrderByPriceDesc(Categories category);
}
