package com.eshop.eshop.controllers;

import com.eshop.eshop.entities.*;
import com.eshop.eshop.services.ItemService;
import com.eshop.eshop.services.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.Role;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class HomeController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Value("${file.avatar.viewPath}")
    private String viewPath;

    @Value("${file.avatar.uploadPath}")
    private String uploadPath;

    @Value("${file.avatar.defaultPicture}")
    private String defaultPicture;


    @GetMapping(value = "/")
    public String index(Model model,HttpServletRequest request){
        model.addAttribute("currentUser",getUserData());
        List<Items> items = itemService.findAllByInTopPageOrderByAddedDateDesc(true);
        model.addAttribute("items",items);

        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands",brands);

        model.addAttribute("categories",itemService.getAllCategories());
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "index";
    }

    @GetMapping(value = "/brandsgroup")
    public String brandsGroup(Model model){

        List<Brands> brands=itemService.getAllBrands();
        model.addAttribute("brands",brands);

        List<Categories> categories = itemService.getAllCategories();
        model.addAttribute("categories",categories);

        return "layout/main";
    }

    @GetMapping(value = "/allitems/{id}")
    public String allitems(Model model,HttpServletRequest request,
                           @PathVariable(name = "id") Long id){

        model.addAttribute("currentUser",getUserData());
        List<Items> items = itemService.findAllByCategoriesOrderByPriceDesc(itemService.getCategory(id));
        model.addAttribute("items",items);
        model.addAttribute("categories",itemService.getAllCategories());

        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "allitems";
    }

    @GetMapping(value = "/item/{id}")
    public String item(Model model, @PathVariable(name = "id") Long id,
                       HttpServletRequest request){
        model.addAttribute("currentUser",getUserData());
        Items item=itemService.getItem(id);
        model.addAttribute("item",item);
        model.addAttribute("categories",itemService.getAllCategories());
        List<Pictures> pictures = itemService.getAllPicturesByItem(item);
        List<Comment> comments = userService.getAllCommentsByItem(item);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy - HH.mm");
        if(pictures!=null){
            model.addAttribute("pictures",pictures);
            int rand=(int)(Math.random()*pictures.size());
            model.addAttribute("randpic",rand);
        }
        if(comments!=null){
            model.addAttribute("comments",comments);
        }
        model.addAttribute("dateFormat",dateFormat);
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "item";
    }

    @GetMapping(value = "/search")
    public String search(Model model,
                         @RequestParam(name = "search_item_name", defaultValue = "") String name,
                         @RequestParam(name = "brandname", defaultValue = "") String brandName,
                         HttpServletRequest request){
        model.addAttribute("currentUser",getUserData());
        List <Brands> brands=itemService.getAllBrands();
        if(!brandName.equals("")){
            List<Items> items=itemService.findAllByBrandNameOrderByPriceAsc(brandName);
            model.addAttribute("items",items);
            model.addAttribute("brand",brandName);
        }
        if(!name.equals("")){
            List<Items> items=itemService.findAllByNameLikeOrderByPriceAsc(name);
            model.addAttribute("items",items);
            model.addAttribute("name",name);
        }
        model.addAttribute("categories",itemService.getAllCategories());
        model.addAttribute("brands",brands);
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);

        return "search";
    }

    @GetMapping(value = "/searching")
    public String search(Model model,
                         @RequestParam(name = "search_item_name",defaultValue = "") String name,
                         @RequestParam(name = "brandname",defaultValue = "") String brandname,
                         @RequestParam(name = "order", defaultValue = "asc") String order,
                         @RequestParam(name = "search_item_price_from", defaultValue = "0") int priceFrom,
                         @RequestParam(name = "search_item_price_to", defaultValue = "999999999") int priceTo,
                         HttpServletRequest request){
        model.addAttribute("currentUser",getUserData());
        List<Items> items;
        if(order.equals("desc")){
            items=itemService.findAllByNameLikeAndPriceGreaterThanEqualAndPriceLessThanEqualAndBrandNameLikeOrderByPriceDesc(name,priceFrom,priceTo,brandname);
        }
        else {
            items=itemService.findAllByNameLikeAndPriceGreaterThanEqualAndPriceLessThanEqualAndBrandNameLikeOrderByPriceAsc(name,priceFrom,priceTo,brandname);
        }
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("name",name);
        model.addAttribute("items",items);
        model.addAttribute("brands",brands);
        model.addAttribute("brand",brandname);
        model.addAttribute("categories",itemService.getAllCategories());
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "search";
    }

    @GetMapping(value = "/additem")
    public String addItem(){
        return "additem";
    }

    @PostMapping(value = "/add")
    public String add(@RequestParam(name="item_name") String name,
                      @RequestParam(name="item_description") String description,
                      @RequestParam(name="item_price") double price,
                      @RequestParam(name="item_stars") int stars,
                      @RequestParam(name = "item_in_top_page") String inTopPage,
                      @RequestParam(name = "item_brand_id", defaultValue = "0") Long id,
                      @RequestParam(name = "item_small_picture_url") MultipartFile smallPicURL,
                      @RequestParam(name = "item_large_picture_url") MultipartFile largePicURL){

        Brands brand = itemService.getBrand(id);
        if(brand!=null){
            Items item = new Items();
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setStars(stars);
            item.setInTopPage(Boolean.parseBoolean(inTopPage));
            item.setBrand(brand);
            if(smallPicURL.getContentType().equals("image/jpeg") || smallPicURL.getContentType().equals("image/png")){
                String picName = DigestUtils.sha1Hex("picture_"+smallPicURL.getOriginalFilename()+"_!Picture");

                try {
                    byte[] bytes = smallPicURL.getBytes();
                    Path path = Paths.get(uploadPath+picName+".jpg");
                    Files.write(path, bytes);
                    item.setSmallPicURL(picName);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(largePicURL.getContentType().equals("image/jpeg") || largePicURL.getContentType().equals("image/png")){
                String picName = DigestUtils.sha1Hex("picture_"+largePicURL.getOriginalFilename()+"_!Picture");

                try {
                    byte[] bytes = largePicURL.getBytes();
                    Path path = Paths.get(uploadPath+picName+".jpg");
                    Files.write(path, bytes);
                    item.setLargePicURL(picName);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            itemService.addItem(item);
        }
        return "redirect:/admin";
    }

    @PostMapping(value = "/addcountry")
    public String addCountry(@RequestParam(name = "country_name") String name,
                             @RequestParam(name = "country_code") String code){
        itemService.addCountry(new Countries(null,name,code));
        return "redirect:/admin-countries";
    }

    @PostMapping(value = "/addbrand")
    public String addBrand(@RequestParam(name = "brand_name") String name,
                           @RequestParam(name = "brand_country_id", defaultValue = "0") Long id){
        itemService.addBrand(new Brands(null,name,itemService.getCountry(id)));
        return "redirect:/admin-brands";
    }

    @PostMapping(value = "/addcategory")
    public String addCategory(@RequestParam(name = "category_name") String name,
                           @RequestParam(name = "category_logo_url") String logoURL){
        itemService.addCategory(new Categories(null,name,logoURL));
        return "redirect:/admin-categories";
    }

    @PostMapping(value = "adduser")
    public String addUser(@RequestParam(name = "user_email") String email,
                          @RequestParam(name = "user_full_name") String fullName,
                          @RequestParam(name = "user_password") String password){

            Users newUser = new Users();
            newUser.setFullName(fullName);
            newUser.setPassword(password);
            newUser.setEmail(email);
            if(userService.createUser(newUser)!=null){
                return "redirect:/admin-users";
            }

        return "redirect:/admin-users?error";
    }

    @GetMapping(value = "/admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String admin(Model model,HttpServletRequest request){
        model.addAttribute("currentUser",getUserData());
        List<Items> items = itemService.getAllItems();
        model.addAttribute("items",items);

        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands",brands);

        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("countries",countries);
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "admin-items";
    }

    @GetMapping(value="/admin-countries")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminCountries(Model model, HttpServletRequest request){
        model.addAttribute("currentUser",getUserData());
        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("countries",countries);
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "admin-countries";
    }

    @GetMapping(value="/admin-brands")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminBrands(Model model,HttpServletRequest request){
        model.addAttribute("currentUser",getUserData());
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands",brands);

        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("countries",countries);
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "admin-brands";
    }


    @GetMapping(value = "/admin-categories")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminCategories(Model model,HttpServletRequest request){
        model.addAttribute("currentUser",getUserData());
        List<Categories> categories = itemService.getAllCategories();
        model.addAttribute("categories",categories);
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "admin-categories";
    }

    @GetMapping(value = "/admin-users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminUsers(Model model,HttpServletRequest request){
        model.addAttribute("currentUser",getUserData());
        List<Users> users = userService.getAllUsers();
        model.addAttribute("users",users);
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);

        return "admin-users";
    }

    @GetMapping(value = "/admin-roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminRoles(Model model,HttpServletRequest request){
        model.addAttribute("currentUser",getUserData());
        List<Roles> roles = userService.getAllRoles();
        model.addAttribute("roles",roles);
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);

        return "admin-roles";
    }

    @PostMapping(value = "/addrole")
    public String addRole(@RequestParam(name = "role_role") String roleName,
                             @RequestParam(name = "role_description") String roleDesc){

        userService.createRole(new Roles(null,roleName,roleDesc));
        return "redirect:/admin-roles";
    }

    @GetMapping(value = "/edit-item/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editItem(Model model, @PathVariable(name = "id") Long id,
                           HttpServletRequest request){
        model.addAttribute("currentUser",getUserData());
        Items item=itemService.getItem(id);
        model.addAttribute("item",item);

        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands",brands);

        List<Categories> categories = itemService.getAllCategories();
        model.addAttribute("categories",categories);

        List<Pictures> pictures = itemService.getAllPicturesByItem(item);
        if(pictures!=null){
            model.addAttribute("pictures",pictures);
        }
        List<Comment> comments = userService.getAllCommentsByItem(item);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy - HH.mm");
        if(comments!=null){
            model.addAttribute("comments",comments);
        }
        model.addAttribute("dateFormat",dateFormat);
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "edit-item";
    }

    @GetMapping(value = "/edit-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editUser(Model model, @PathVariable(name = "id") Long id,
                           @RequestParam(name = "oldpassworderror", required = false) String oldPasswordError,
                           @RequestParam(name = "repassworderror",required = false) String rePasswordError,
                           HttpServletRequest request){

        if(oldPasswordError!=null){
            model.addAttribute("oldPasswordError",oldPasswordError);
        }
        if (rePasswordError!=null){
            model.addAttribute("rePasswordError",rePasswordError);
        }

        model.addAttribute("currentUser",getUserData());
        model.addAttribute("user",userService.getUserById(id));
        List<Roles> roles = userService.getAllRoles();
        model.addAttribute("roles",roles);
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "edit-user";
    }

    @PostMapping(value = "/update-item")
    public String updateItem(@RequestParam(name="item_id") Long id,
                         @RequestParam(name="item_name") String name,
                         @RequestParam(name="item_description") String description,
                         @RequestParam(name="item_price") double price,
                         @RequestParam(name="item_stars") int stars,
                         @RequestParam(name="item_small_picture_url") String smallPicURL,
                         @RequestParam(name="item_large_picture_url") String largePicURL,
                         @RequestParam(name = "item_in_top_page") String inTopPage,
                         @RequestParam(name = "item_brand_id") Long brandId,
                         HttpServletRequest request){
        if(request.getParameter("save")!=null){
            Items item=itemService.getItem(id);
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setStars(stars);
            item.setSmallPicURL(smallPicURL);
            item.setLargePicURL(largePicURL);
            item.setInTopPage(Boolean.parseBoolean(inTopPage));
            item.setBrand(itemService.getBrand(brandId));
            itemService.saveItem(item);
        }
        if(request.getParameter("delete")!=null){
            Items item=itemService.getItem(id);
            List<Pictures> pictures = itemService.getAllPicturesByItem(item);
            for(Pictures p:pictures){
                itemService.deletePicture(p);
            }
            itemService.deleteItem(item);
        }
        return "redirect:/admin";
    }

    @PostMapping(value = "/update-user")
    public String updateItem(@RequestParam(name="user_id") Long id,
                             @RequestParam(name="user_email") String email,
                             @RequestParam(name="old_user_password", required = false) String oldPassword,
                             @RequestParam(name="new_user_password", required = false) String newPassword,
                             @RequestParam(name="re_new_user_password", required = false) String reNewPassword,
                             @RequestParam(name="user_full_name") String fullName,
                             HttpServletRequest request){
        String error = "";
        if(request.getParameter("save")!=null){
            Users user = userService.getUserById(id);
            user.setEmail(email);
            user.setFullName(fullName);

            if(!oldPassword.equals("")){
                error = "?oldpassworderror";
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                if(encoder.matches(oldPassword,user.getPassword())){

                    error="?repassworderror";
                    if(newPassword.equals(reNewPassword)){
                        error="";
                        user.setPassword(newPassword);
                    }
                }
            }
            if(error.equals("")) {
                userService.updateUserByAdmin(user);
            }

        }
        if(request.getParameter("delete")!=null){
            Users user = userService.getUserById(id);
            userService.deleteUser(user);
        }
        return "redirect:/edit-user/"+String.valueOf(id)+error;
    }

    @PostMapping(value = "/update-country")
    public String updateCountry(@RequestParam(name = "country_id") Long id,
                                @RequestParam(name = "country_name") String name,
                                @RequestParam(name = "country_code") String code,
                                HttpServletRequest request){
        if(request.getParameter("save")!=null){
            Countries country = itemService.getCountry(id);
            country.setName(name);
            country.setCode(code);
            itemService.saveCountry(country);
        }
        if(request.getParameter("delete")!=null){
            Countries country=itemService.getCountry(id);
            itemService.deleteCountry(country);
        }
        return "redirect:/admin-countries";
    }

    @PostMapping(value = "/update-role")
    public String updateRole(@RequestParam(name = "role_id") Long id,
                                @RequestParam(name = "role_role") String roleName,
                                @RequestParam(name = "role_description") String roleDesc,
                                HttpServletRequest request){
        if(request.getParameter("save")!=null){
            Roles role = userService.getRoleById(id);
            role.setRole(roleName);
            role.setDescription(roleDesc);
            userService.createRole(role);
        }
        if(request.getParameter("delete")!=null){
            Roles role = userService.getRoleById(id);
            userService.deleteRole(role);
        }
        return "redirect:/admin-roles";
    }
    @PostMapping(value = "/update-brand")
    public String updateBrand(@RequestParam(name = "brand_id") Long id,
                              @RequestParam(name = "brand_name") String name,
                              @RequestParam(name = "brand_country_id") Long countryId,
                              HttpServletRequest request){
        if (request.getParameter("save")!=null) {
            Brands brand = itemService.getBrand(id);
            brand.setName(name);
            brand.setCountry(itemService.getCountry(countryId));
            itemService.saveBrands(brand);
        }
        if(request.getParameter("delete")!=null){
            Brands brand = itemService.getBrand(id);
            itemService.deleteBrand(brand);
        }
        return "redirect:/admin-brands";
    }

    @PostMapping(value = "/update-category")
    public String updateCategory(@RequestParam(name = "category_id") Long id,
                              @RequestParam(name = "category_name") String name,
                              @RequestParam(name = "category_logo_url") String logoURL,
                              HttpServletRequest request){
        if (request.getParameter("save")!=null) {
            Categories category = itemService.getCategory(id);
            category.setName(name);
            category.setLogoURL(logoURL);
            itemService.saveCategory(category);
        }
        if(request.getParameter("delete")!=null){
            Categories category = itemService.getCategory(id);
            itemService.deleteCategory(category);
        }
        return "redirect:/admin-categories";
    }

    @PostMapping(value = "/assign-category")
    public String assignCategory(@RequestParam(name = "item_id") Long itemId,
                                 @RequestParam(name = "category_id") Long categoryId){

        Categories cat=itemService.getCategory(categoryId);
        if(cat!=null){
            Items item = itemService.getItem(itemId);
            if(item!=null){
                List<Categories> categories = item.getCategories();
                if(categories==null){
                    categories=new ArrayList<>();
                }
                categories.add(cat);

                itemService.saveItem(item);
                return "redirect:/edit-item/"+itemId;
            }
        }

        return "redirect:/";
    }

    @PostMapping(value = "/assign-role")
    public String assignRole(@RequestParam(name = "user_id") Long userId,
                                 @RequestParam(name = "role_id") Long roleId){

        Roles role = userService.getRoleById(roleId);
        if(role!=null){
            Users user = userService.getUserById(userId);
            if(user!=null){
                List<Roles> roles = user.getRoles();
                if(roles==null){
                    roles=new ArrayList<>();
                }
                roles.add(role);

                userService.updateUser(user);
                return "redirect:/edit-user/"+userId;
            }
        }

        return "redirect:/";
    }

    @PostMapping(value = "/unsign-category")
    public String unsignCategory(@RequestParam(name = "item_id") Long itemId,
                                 @RequestParam(name = "category_id") Long categoryId){
        Categories cat = itemService.getCategory(categoryId);
        if(cat!=null){
            Items item = itemService.getItem(itemId);
            if(item!=null){
                item.getCategories().remove(cat);
                itemService.saveItem(item);
                return "redirect:/edit-item/"+itemId;
            }
        }
        return "redirect:/";
    }

    @PostMapping(value = "/unsign-role")
    public String unsignRole(@RequestParam(name = "user_id") Long userId,
                                 @RequestParam(name = "role_id") Long roleId){
        Roles role = userService.getRoleById(roleId);
        if(role!=null){
            Users user = userService.getUserById(userId);
            if(user!=null){
                user.getRoles().remove(role);
                userService.updateUser(user);
                return "redirect:/edit-user/"+userId;
            }
        }
        return "redirect:/";
    }

    @GetMapping(value = "/403")
    public String accessDenied(Model model,HttpServletRequest request){
        model.addAttribute("currentUser",getUserData());
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "403";
    }

    @GetMapping(value = "/login")
    public String login(Model model,HttpServletRequest request){
        model.addAttribute("currentUser",getUserData());
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "login";
    }

    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model,
                          @RequestParam(name = "oldpassworderror", required = false) String oldPasswordError,
                          @RequestParam(name = "repassworderror",required = false) String rePasswordError,
                          HttpServletRequest request){
        if(oldPasswordError!=null){
            model.addAttribute("oldPasswordError",oldPasswordError);
        }
        if(rePasswordError!=null){
            model.addAttribute("rePasswordError",rePasswordError);
        }
        model.addAttribute("categories",itemService.getAllCategories());

        model.addAttribute("currentUser",getUserData());
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "profile";
    }

    @PostMapping(value = "/edit-avatar")
    @PreAuthorize("isAuthenticated()")
    public String editAvatar(@RequestParam(name = "user_avatar") MultipartFile file){

        if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {

            Users currentUser = getUserData();

           String picName = DigestUtils.sha1Hex("avatar_"+currentUser.getId()+"_!Picture");

            try {

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName+".jpg");
                Files.write(path, bytes);

                currentUser.setPictureURL(picName);
                userService.updateUser(currentUser);

                return "redirect:/profile?success";

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "redirect:/profile";
        }

        return "redirect:/profile";
    }

    @PostMapping(value = "/add-picture")
    @PreAuthorize("isAuthenticated()")
    public String addPicture(@RequestParam(name = "item_id") Long itemId,
                             @RequestParam(name = "item_picture") MultipartFile file){

        if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")){
            String picName = DigestUtils.sha1Hex("picture_"+file.getOriginalFilename()+"_!Picture");

            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath+picName+".jpg");
                Files.write(path, bytes);
                Date date = new Date(System.currentTimeMillis());
                Items item = itemService.getItem(itemId);

                itemService.addPicture(new Pictures(null,picName,date,item));
                return  "redirect:/edit-item/"+itemId+"?success";
            }catch (Exception e){
                e.printStackTrace();
            }
            return "redirect:/edit-item/"+itemId;
        }


        return "redirect:/edit-item/"+itemId;
    }

    @PostMapping(value = "/delete-picture")
    @PreAuthorize("isAuthenticated()")
    public String deletePicture(@RequestParam(name = "picture_id") Long picId,
                                @RequestParam(name = "item_id") Long itemId){

        Pictures picture = itemService.getPicture(picId);
        if(picture!=null){
            itemService.deletePicture(picture);
        }

        return "redirect:/edit-item/"+itemId;
    }

    @PostMapping(value = "/edit-profile")
    @PreAuthorize("isAuthenticated()")
    public String editProfile(@RequestParam(name="user_id") Long id,
                              @RequestParam(name="user_email") String email,
                              @RequestParam(name="user_full_name") String fullName){

        String error = "";
        Users user = getUserData();
        user.setEmail(email);
        user.setFullName(fullName);

            userService.updateUserByAdmin(user);


        return "redirect:/profile";
    }

    @PostMapping(value = "/edit-password")
    @PreAuthorize("isAuthenticated()")
    public String editPassword(@RequestParam(name="old_user_password", required = false) String oldPassword,
                              @RequestParam(name="new_user_password", required = false) String newPassword,
                              @RequestParam(name="re_new_user_password", required = false) String reNewPassword){

        String error = "";
        Users user = getUserData();

        if(!oldPassword.equals("")){
            error = "?oldpassworderror";
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if(encoder.matches(oldPassword,user.getPassword())){

                error="?repassworderror";
                if(newPassword.equals(reNewPassword)){
                    error="";
                    user.setPassword(newPassword);
                }
            }
        }
        if(error.equals("")) {
            userService.updateUserByAdmin(user);
        }

        return "redirect:/profile"+error;
    }

    @GetMapping(value = "/viewphoto/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody byte[] viewProfilePhoto(@PathVariable(name = "url") String url) throws IOException{
        String pictureURL = viewPath+defaultPicture;

        if(url!=null && !url.equals("null")){
            pictureURL = viewPath+url+".jpg";
        }

        InputStream in;

        try {
            ClassPathResource resource = new ClassPathResource(pictureURL);
            in = resource.getInputStream();
        }catch (Exception e){
            ClassPathResource resource = new ClassPathResource(viewPath+defaultPicture);
            in = resource.getInputStream();
            e.printStackTrace();
        }
        return IOUtils.toByteArray(in);
    }

    private Users getUserData(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            User securityUser = (User)authentication.getPrincipal();
            Users myUser = userService.getUserByEmail(securityUser.getUsername());
            return myUser;
        }
        return null;
    }

    @GetMapping(value = "/registration")
    public String registration(Model model,
                               @RequestParam(name = "passworderror", required = false) String passwordError,
                               @RequestParam(name = "emailerror", required = false) String emailError,
                               HttpServletRequest request){

        if(passwordError!=null){
            model.addAttribute("passwordError",passwordError);
        }
        if(emailError!=null){
            model.addAttribute("emailError",emailError);
        }
        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "registration";
    }

    @PostMapping(value = "/registration")
    public String registration(@RequestParam(name = "user_email") String email,
                               @RequestParam(name = "user_password") String password,
                               @RequestParam(name = "re_user_password") String rePasswrod,
                               @RequestParam(name = "user_full_name") String fullName,
                               Model model){
        String error="?passworderror";

        if(password.equals(rePasswrod)){
            error="?emailerror";
            Users newUser = new Users();
            newUser.setFullName(fullName);
            newUser.setPassword(password);
            newUser.setEmail(email);
            if(userService.createUser(newUser)!=null){
                return "redirect:/login";
            }
        }
        return "redirect:/registration"+error;
    }

    public String extractCookie(HttpServletRequest req,String key){
        for(Cookie c: req.getCookies()){
            if(c.getName().equals(key)){
                return c.getValue();
            }
        }
        return null;
    }

    @GetMapping(value = "/basket")
    public String basket(HttpServletResponse response, HttpServletRequest request,Model model){
        Cookie[] cookies = request.getCookies();
        ArrayList<BasketItems> basket = new ArrayList<>();
        model.addAttribute("currentUser",getUserData());
        double totalPrice=0;
        if(cookies!=null) {
            for (Cookie c : cookies) {
                if (c.getName().contains("item_") && !c.getName().contains("_amount")) {
                    totalPrice += Double.parseDouble(extractCookie(request, "item_" + c.getValue() + "_amount")) * itemService.getItem(Long.parseLong(c.getValue())).getPrice();
                    Items item=itemService.getItem(Long.parseLong(c.getValue()));
                    basket.add(new BasketItems(item.getId(),item.getName(),item.getPrice(),Integer.parseInt(extractCookie(request,"item_"+c.getValue()+"_amount"))));
                }
            }
        }
        if (basket!=null){
            model.addAttribute("basket",basket);
            model.addAttribute("totalPrice",totalPrice);
        }
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands",brands);

        model.addAttribute("categories",itemService.getAllCategories());

        int amount=totalAmount(request);
        model.addAttribute("totalAmount",amount);
        return "basket";
    }

    public int totalAmount(HttpServletRequest request){
        int totalAmount=0;
        if(request.getCookies()!=null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().contains("amount")) {
                    if (c.getValue().equals("")) {
                        continue;
                    }
                    totalAmount = totalAmount + Integer.parseInt(c.getValue());
                }
            }
        }
        return totalAmount;
    }

    @GetMapping(value = "/navbar")
    public String navbar(HttpServletRequest request,Model model){
        int amount=6;
        model.addAttribute("totalAmount",amount);

        return "layout/navbar";
    }

    @PostMapping(value = "/add_to_basket")
    public String addToBasket(@RequestParam(name = "item_id") Long itemId,
                              HttpServletRequest request,
                              HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        Items item = itemService.getItem(itemId);

        if(extractCookie(request,"item_"+item.getId()+"_amount")!=null){
            int sum=Integer.parseInt(extractCookie(request,"item_"+item.getId()+"_amount"))+1;
            response.addCookie(new Cookie("item_"+item.getId()+"_amount",String.valueOf(sum)));
        }
        else {
            response.addCookie(new Cookie("item_"+item.getId(), String.valueOf(itemId)));
            response.addCookie(new Cookie("item_"+item.getId()+"_amount","1"));
        }


        return "redirect:/basket";
    }

    @PostMapping(value = "/remove_from_basket")
    public String removeFromBasket(@RequestParam(name = "item_id") Long id,
                                   HttpServletRequest request,
                                   HttpServletResponse response){
        if(Integer.parseInt(extractCookie(request,"item_"+String.valueOf(id)+"_amount"))==1){
            Cookie cookie=new Cookie("item_"+String.valueOf(id)+"_amount",null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            cookie=new Cookie("item_"+String.valueOf(id),null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        else {
            int amount=Integer.parseInt(extractCookie(request,"item_"+String.valueOf(id)+"_amount"))-1;
            response.addCookie(new Cookie("item_"+String.valueOf(id)+"_amount",String.valueOf(amount)));
        }

        return "redirect:/basket";
    }

    @PostMapping(value = "/clear_basket")
    public String clearBasket(HttpServletRequest request,
                              HttpServletResponse response,
                              @RequestParam(name = "in") String in){
        for(Cookie c:request.getCookies()){
            if(c.getName().contains("item")){
                c.setValue(null);
                c.setMaxAge(0);
                response.addCookie(c);
            }
        }
        return "redirect:/basket";
    }

    @PostMapping(value = "/pay")
    public String payItems(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cookies = request.getCookies();

        for(Cookie c: cookies){
            if(c.getName().contains("item_") && !c.getName().contains("_amount")){
                Items item=itemService.getItem(Long.parseLong(c.getValue()));
                BasketItems basketItems = new BasketItems(item.getId(),item.getName(),item.getPrice(),Integer.parseInt(extractCookie(request,"item_"+c.getValue()+"_amount")));
                basketItems.setSoldDate(new Date(System.currentTimeMillis()));
                basketItems.setId(null);
                itemService.addBasketItem(basketItems);
            }
        }
        for(Cookie c:request.getCookies()){
            if(c.getName().contains("item")){
                c.setMaxAge(0);
                response.addCookie(c);
            }
        }
        return "redirect:/basket";
    }

    @GetMapping(value = "/admin-sold-items")
    public String soldItems(Model model,HttpServletRequest request){
        List<BasketItems> items = itemService.getAllBasketItems();
        model.addAttribute("items",items);
        model.addAttribute("currentUser",getUserData());
        model.addAttribute("totalAmount",totalAmount(request));
        return "admin-sold-items";
    }

    @PostMapping(value = "/add-comment")
    @PreAuthorize("isAuthenticated()")
    public String addComment(@RequestParam(name = "item_id") Long itemId,
                             @RequestParam(name = "user_id") Long userId,
                             @RequestParam(name = "comment") String commentText){
        Items item = itemService.getItem(itemId);
        Users user = userService.getUserById(userId);
        Timestamp date = new Timestamp(System.currentTimeMillis());
        Comment comment = new Comment(null,commentText,date,item,user);
        userService.addComment(comment);

        return "redirect:/item/"+itemId;
    }

    @PostMapping(value = "/delete-comment")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@RequestParam(name = "item_id") Long itemId,
                                @RequestParam(name = "comment_id") Long commentId){

        Comment comment = userService.getComment(commentId);
        userService.deleteComment(comment);
        return "redirect:/item/"+itemId;
    }

    @PostMapping(value = "/delete_comment")
    @PreAuthorize("isAuthenticated()")
    public String delete_Comment(@RequestParam(name = "item_id") Long itemId,
                                @RequestParam(name = "comment_id") Long commentId){

        Comment comment = userService.getComment(commentId);
        userService.deleteComment(comment);
        return "redirect:/edit-item/"+itemId;
    }

    @PostMapping(value = "/edit-comment")
    @PreAuthorize("isAuthenticated()")
    public String editComment(@RequestParam(name = "item_id") Long itemId,
                                @RequestParam(name = "comment_id") Long commentId,
                                @RequestParam(name = "comment") String commentText){

        Comment comment = userService.getComment(commentId);
        comment.setComment(commentText);
        userService.saveComment(comment);
        return "redirect:/item/"+itemId;
    }
}
