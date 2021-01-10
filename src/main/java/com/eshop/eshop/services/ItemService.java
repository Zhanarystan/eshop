package com.eshop.eshop.services;

import com.eshop.eshop.entities.*;

import java.util.List;

public interface ItemService {

    Items addItem(Items item);
    List<Items> getAllItems();
    Items getItem(Long id);
    void deleteItem(Items item);
    Items saveItem(Items item);
    List<Items> findAllByNameLikeOrderByPriceDesc(String name);
    List<Items> findAllByNameLikeOrderByPriceAsc(String name);
    List<Items> findAllByNameLikeAndPriceBetweenOrderByPriceAsc(String name, double price1, double price2);
    List<Items> findAllByNameLikeAndPriceBetweenOrderByPriceDesc(String name, double price1, double price2);
    List<Items> findAllByInTopPageOrderByAddedDateDesc(boolean inTopPage);

    List<Items> findAllByBrandNameOrderByPriceAsc(String name);
    List<Items> findAllByBrandNameOrderByPriceDesc(String name);
    List<Items> findAllByNameLikeAndBrandNameEqualsAndPriceBetweenOrderByPriceAsc(String name,String brandname, double price1, double price2);
    List<Items> findAllByNameLikeAndBrandNameEqualsAndPriceBetweenOrderByPriceDesc(String name,String brandname, double price1, double price2);
    List<Items> findAllByNameLikeAndBrandNameEqualsOrderByPriceAsc(String name,String brandname);
    List<Items> findAllByNameLikeAndBrandNameEqualsOrderByPriceDesc(String name,String brandname);

    List<Items> findAllByNameLikeAndBrandNameEqualsAndPriceGreaterThanOrderByPriceDesc(String name,String brandname,double price);


    List<Items> findAllByNameLikeAndPriceGreaterThanEqualAndPriceLessThanEqualAndBrandNameLikeOrderByPriceAsc(String name, double price1, double price2, String brandname);
    List<Items> findAllByNameLikeAndPriceGreaterThanEqualAndPriceLessThanEqualAndBrandNameLikeOrderByPriceDesc(String name, double price1, double price2, String brandname);


    List<Countries> getAllCountries();
    Countries addCountry(Countries country);
    Countries saveCountry(Countries country);
    void deleteCountry(Countries country);
    Countries getCountry(Long id);

    List<Brands> getAllBrands();
    Brands addBrand(Brands brand);
    Brands saveBrands(Brands brand);
    void deleteBrand(Brands brand);
    Brands getBrand(Long id);

    List<Categories> getAllCategories();
    Categories addCategory(Categories category);
    Categories saveCategory(Categories category);
    void deleteCategory(Categories category);
    Categories getCategory(Long id);

    List<Pictures> getAllPicturesByItem(Items item);
    List<Pictures> getAllPictures();
    Pictures addPicture(Pictures picture);
    Pictures savePicture(Pictures picture);
    void deletePicture(Pictures picture);
    Pictures getPicture(Long id);

    List<BasketItems> getAllBasketItems();
    BasketItems addBasketItem(BasketItems basketItem);

    List<Items> findAllByCategoriesOrderByPriceDesc(Categories category);


}
