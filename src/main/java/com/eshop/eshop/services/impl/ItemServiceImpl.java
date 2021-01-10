package com.eshop.eshop.services.impl;

import com.eshop.eshop.entities.*;
import com.eshop.eshop.repositories.*;
import com.eshop.eshop.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private BrandsRepository brandsRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private PicturesRepository picturesRepository;

    @Autowired
    private BasketItemsRepository basketItemsRepository;


    @Override
    public Items addItem(Items item) {
        return itemsRepository.save(item);
    }

    @Override
    public List<Items> getAllItems() {
        return itemsRepository.findAllByPriceGreaterThanOrderByAddedDateDesc(0);
    }

    @Override
    public Items getItem(Long id) {
        return itemsRepository.getOne(id);
    }

    @Override
    public void deleteItem(Items item) {
        itemsRepository.delete(item);
    }

    @Override
    public Items saveItem(Items item) {
        return itemsRepository.save(item);
    }

    @Override
    public List<Items> findAllByNameLikeOrderByPriceDesc(String name) {
        return itemsRepository.findAllByNameLikeOrderByPriceDesc("%"+name+"%");
    }

    @Override
    public List<Items> findAllByNameLikeOrderByPriceAsc(String name) {
        return itemsRepository.findAllByNameLikeOrderByPriceAsc("%"+name+"%");
    }

    @Override
    public List<Items> findAllByNameLikeAndPriceBetweenOrderByPriceAsc(String name, double price1, double price2) {
        return itemsRepository.findAllByNameLikeAndPriceBetweenOrderByPriceAsc("%"+name+"%",price1,price2);
    }

    @Override
    public List<Items> findAllByNameLikeAndPriceBetweenOrderByPriceDesc(String name, double price1, double price2) {
        return itemsRepository.findAllByNameLikeAndPriceBetweenOrderByPriceDesc("%"+name+"%",price1,price2);
    }

    @Override
    public List<Items> findAllByInTopPageOrderByAddedDateDesc(boolean inTopPage) {
        return itemsRepository.findAllByInTopPageOrderByAddedDateDesc(inTopPage);
    }


    @Override
    public List<Countries> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Countries addCountry(Countries country) {
        return countryRepository.save(country);
    }

    @Override
    public Countries saveCountry(Countries country) {
        return countryRepository.save(country);
    }

    @Override
    public void deleteCountry(Countries country) {
        countryRepository.delete(country);
    }

    @Override
    public Countries getCountry(Long id) {
        return countryRepository.getOne(id);
    }

    @Override
    public List<Brands> getAllBrands() {
        return brandsRepository.findAllByIdGreaterThanOrderByNameAsc(0L);
    }

    @Override
    public Brands addBrand(Brands brand) {
        return brandsRepository.save(brand);
    }

    @Override
    public Brands saveBrands(Brands brand) {
        return brandsRepository.save(brand);
    }

    @Override
    public void deleteBrand(Brands brand) {
        brandsRepository.delete(brand);
    }

    @Override
    public Brands getBrand(Long id) {
        return brandsRepository.getOne(id);
    }

    @Override
    public List<Items> findAllByBrandNameOrderByPriceAsc(String name) {
        return itemsRepository.findAllByBrandNameOrderByPriceAsc(name);
    }

    @Override
    public List<Items> findAllByBrandNameOrderByPriceDesc(String name) {
        return itemsRepository.findAllByBrandNameOrderByPriceDesc(name);
    }

    @Override
    public List<Items> findAllByNameLikeAndBrandNameEqualsAndPriceBetweenOrderByPriceAsc(String name, String brandname, double price1, double price2) {
        return itemsRepository.findAllByNameLikeAndBrandNameEqualsAndPriceBetweenOrderByPriceAsc("%"+name+"%",brandname,price1,price2);
    }

    @Override
    public List<Items> findAllByNameLikeAndBrandNameEqualsAndPriceBetweenOrderByPriceDesc(String name, String brandname, double price1, double price2) {
        return itemsRepository.findAllByNameLikeAndBrandNameEqualsAndPriceBetweenOrderByPriceDesc("%"+name+"%",brandname,price1,price2);
    }

    @Override
    public List<Items> findAllByNameLikeAndBrandNameEqualsOrderByPriceAsc(String name, String brandname) {
        return itemsRepository.findAllByNameLikeAndBrandNameEqualsOrderByPriceAsc("%"+name+"%",brandname);
    }

    @Override
    public List<Items> findAllByNameLikeAndBrandNameEqualsOrderByPriceDesc(String name, String brandname) {
        return itemsRepository.findAllByNameLikeAndBrandNameEqualsOrderByPriceDesc("%"+name+"%",brandname);
    }

    @Override
    public List<Items> findAllByNameLikeAndBrandNameEqualsAndPriceGreaterThanOrderByPriceDesc(String name, String brandname, double price) {
        return itemsRepository.findAllByNameLikeAndBrandNameEqualsAndPriceGreaterThanOrderByPriceDesc(name,brandname,0);
    }


    @Override
    public List<Items> findAllByNameLikeAndPriceGreaterThanEqualAndPriceLessThanEqualAndBrandNameLikeOrderByPriceAsc(String name, double price1, double price2, String brandname) {
        return itemsRepository.findAllByNameLikeAndPriceGreaterThanEqualAndPriceLessThanEqualAndBrandNameLikeOrderByPriceAsc("%"+name+"%",price1,price2,"%"+brandname+"%");
    }

    @Override
    public List<Items> findAllByNameLikeAndPriceGreaterThanEqualAndPriceLessThanEqualAndBrandNameLikeOrderByPriceDesc(String name, double price1, double price2, String brandname) {
        return itemsRepository.findAllByNameLikeAndPriceGreaterThanEqualAndPriceLessThanEqualAndBrandNameLikeOrderByPriceDesc("%"+name+"%",price1,price2,"%"+brandname+"%");
    }

    @Override
    public List<Categories> getAllCategories() {
        return categoriesRepository.findAll();
    }

    @Override
    public Categories addCategory(Categories category) {
        return categoriesRepository.save(category);
    }

    @Override
    public Categories saveCategory(Categories category) {
        return categoriesRepository.save(category);
    }

    @Override
    public void deleteCategory(Categories category) {
        categoriesRepository.delete(category);
    }

    @Override
    public Categories getCategory(Long id) {
        return categoriesRepository.getOne(id);
    }

    @Override
    public List<Pictures> getAllPicturesByItem(Items item) {
        return picturesRepository.findAllByItem(item);
    }

    @Override
    public List<Pictures> getAllPictures() {
        return picturesRepository.findAll();
    }

    @Override
    public Pictures addPicture(Pictures picture) {
        return picturesRepository.save(picture);
    }

    @Override
    public Pictures savePicture(Pictures picture) {
        return picturesRepository.save(picture);
    }

    @Override
    public void deletePicture(Pictures picture) {
        picturesRepository.delete(picture);
    }

    @Override
    public Pictures getPicture(Long id) {
        return picturesRepository.getOne(id);
    }

    @Override
    public List<BasketItems> getAllBasketItems() {
        return basketItemsRepository.findAll();
    }

    @Override
    public BasketItems addBasketItem(BasketItems basketItem) {
        return basketItemsRepository.save(basketItem);
    }

    @Override
    public List<Items> findAllByCategoriesOrderByPriceDesc(Categories category) {

        return itemsRepository.findAllByCategoriesOrderByPriceDesc(category);
    }


}
