package ca.shopify.inventorytrackerchallenge.controllers;

import ca.shopify.inventorytrackerchallenge.beans.*;
import ca.shopify.inventorytrackerchallenge.database.DatabaseAccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class HomeController.
 *
 * @author Marcin Koziel
 */
@Controller
public class HomeController {

    /**
     * *****************************************
     * Initalize class
     * *****************************************
     */

    /** Encoding string values, in this case passwords. */
    @Autowired
    @Lazy
    private BCryptPasswordEncoder passwordEncoder;

    /** Used to add valid users into the database. */
    @Autowired
    private JdbcUserDetailsManager jdbcUserDetailsManager;

    private DatabaseAccess da;

    /**
     * Instantiates a new Home controller.
     *
     * @param da the da, using a singleton approach
     */
    public HomeController(DatabaseAccess da) {
        this.da = da;
    }

    /**
     * Go home/items string.
     *
     * @param model the model give thymeleaf meaningful attributes
     * @return      the string
     */
    @GetMapping({"/", "/index", "/home", "/items"})
    public String goToPageHome(Model model) {

        List<Item> itemList = da.getAllItems();

        model
            .addAttribute("title", "Inventory Items")
            .addAttribute("itemList", itemList);
        return "index";
    }

    /**
     * Go login string.
     *
     * @param model the model give thymeleaf meaningful attributes
     * @return      the string
     */
    @GetMapping("/login")
    public String goToPageLogin(Model model) {
        model.addAttribute("title", new String("Login"));
        return "login";
    }

    /**
     * Go register string.
     *
     * @param model the model give thymeleaf meaningful attributes
     * @return      the string
     */
    @GetMapping("/login/register")
    public String goToPageRegister(Model model){
        model.addAttribute("title", "Register");
        return "register";
    }

    /**
     * Add user string.
     *
     * @param username the username input into the register form
     * @param password the password input into the register form
     * @param model    the model give thymeleaf meaningful attributes
     * @return         the string
     */
    @PostMapping("/login/register_status")
    public String databaseAddUser(@RequestParam String username, @RequestParam String password, Model model) {

        if (da.isUsernameTaken(username)) {
            // Username provided is taken
            model
                .addAttribute("title", "Register")
                .addAttribute("status", "taken");
            return "register";
        } else {

            // Creating a new User
            List<GrantedAuthority> authorityList = new ArrayList<>();

            authorityList.add(new SimpleGrantedAuthority(new String("ROLE_USER")));

            String encodedPassword = passwordEncoder.encode(password);

            User user = new User(username, encodedPassword, authorityList);

            jdbcUserDetailsManager.createUser(user);

            model
                .addAttribute("title", "Login")
                .addAttribute("status", "registered");
            return "login";
        }
    }

    /**
     * Go to add item page string.
     *
     * @param model the model give thymeleaf meaningful attributes
     * @return      the string
     */
    @GetMapping("/admin/items/add")
    public String goToPageItemAdd(Model model, Principal principal) {

        Item item = new Item();

        MultipleSelect<Supplier> multipleSelectSupplier = new MultipleSelect<>(da.getAllSuppliers(), new ArrayList<>());
        MultipleSelect<Product> multipleSelectProduct = new MultipleSelect<>(da.getAllProducts(), new ArrayList<>());
        MultipleSelect<Brand> multipleSelectBrand = new MultipleSelect<>(da.getAllBrands(), new ArrayList<>());

        model
            .addAttribute("title", "Add Item")
            .addAttribute("item", item)
            .addAttribute("multipleSelectSupplier", multipleSelectSupplier)
            .addAttribute("multipleSelectProduct", multipleSelectProduct)
            .addAttribute("multipleSelectBrand", multipleSelectBrand)
            .addAttribute("conditionList", Item.Condition.values());

        return "admin/add-item";
    }

    /**
     * Go to edit item page.
     *
     * @param model the model give thymeleaf meaningful attributes
     * @return      the string
     */
    @GetMapping("/admin/items/edit/{id}")
    public String goToPageItemsEdit(@PathVariable Long id, Model model) {

        Item item = da.getItemById(id);

        List<String> multipleSelectedIdListSupplier = new ArrayList<>();
        List<String> multipleSelectedIdListProduct = new ArrayList<>();
        List<String> multipleSelectedIdListBrand = new ArrayList<>();

        multipleSelectedIdListSupplier.add(item.getSupplier().getId().toString());
        multipleSelectedIdListProduct.add(item.getProduct().getId().toString());
        multipleSelectedIdListBrand.add(item.getBrand().getId().toString());

        MultipleSelect<Supplier> multipleSelectSupplier = new MultipleSelect<>(da.getAllSuppliers(), multipleSelectedIdListSupplier);
        MultipleSelect<Product> multipleSelectProduct = new MultipleSelect<>(da.getAllProducts(), multipleSelectedIdListProduct);
        MultipleSelect<Brand> multipleSelectBrand = new MultipleSelect<>(da.getAllBrands(), multipleSelectedIdListBrand);

        model
                .addAttribute("title", "Add Item")
                .addAttribute("item", item)
                .addAttribute("multipleSelectSupplier", multipleSelectSupplier)
                .addAttribute("multipleSelectProduct", multipleSelectProduct)
                .addAttribute("multipleSelectBrand", multipleSelectBrand)
                .addAttribute("conditionList", Item.Condition.values());

        return "admin/add-item";
    }

    /**
     * Add item string.
     *
     * @param item              the item object will be inserted into database
     * @param multipleSelect    class specialized for working with multiple select/options in forms
     * @return                  the modelandview string
     */
    @PostMapping("/admin/items/submit")
    public ModelAndView databaseUpdateItem(
            @ModelAttribute Item item
            , @ModelAttribute MultipleSelect<Object> multipleSelect
            , Principal principal
            , BindingResult bindingResult) {

        Long productId = Long.parseLong(multipleSelect.getSelectedIdList().get(0));
        Long brandId = Long.parseLong(multipleSelect.getSelectedIdList().get(1));
        Long supplierId = Long.parseLong(multipleSelect.getSelectedIdList().get(2));

        if (item.getProduct() == null) {
            item.setProduct(da.getProductById(productId));
        }
        if (item.getBrand() == null) {
            item.setBrand(da.getBrandById(brandId));
        }
        if (item.getSupplier() == null) {
            item.setSupplier(da.getSupplierById(supplierId));
        }

        item.setUpdatedBy(da.getUserByName(principal.getName()));

        if (item.getId() == null) {
            item.setCreatedBy(da.getUserByName(principal.getName()));
            da.addItem(item);
        } else {
            da.updateItem(item);
        }

        return new ModelAndView("redirect:/items");
    }

    /**
     * expire item string.
     *
     * @param id    the item identifier used to database which item to expire
     * @return      the string
     */
    @GetMapping("/admin/items/remove/{id}")
    public ModelAndView databaseExpireItem(@PathVariable Long id, Model model) {

        da.expireItem(Item.builder().Id(id).build());

        return new ModelAndView("redirect:/items");
    }

    /**
     * Go to products page string.
     *
     * @param model the model give thymeleaf meaningful attributes
     * @return      the string
     */
    @GetMapping("/products")
    public String goToPageProducts(Model model){

        List<Product> allProductList = da.getAllProducts();

        for (Product product : allProductList) {
            product.setCategoryList(da.getCategoriesByProductId(product.getId()));
        }

        model.addAttribute("productList", allProductList);

        return "view-products";
    }

    /**
     * Go to add product page.
     *
     * @param model the model give thymeleaf meaningful attributes
     * @return      the string
     */
    @GetMapping("/admin/products/add")
    public String goToPageProductAdd(Model model) {

        model
            .addAttribute("title", "Add Product")
            .addAttribute("product", new Product())
            .addAttribute("multipleSelectionObj", new MultipleSelect<Category>(da.getAllCategories(), new ArrayList<>()));

        return "admin/add-product";
    }

    /**
     * Go to edit product page.
     *
     * @param model the model give thymeleaf meaningful attributes
     * @return      the string
     */
    @GetMapping("/admin/products/edit/{id}")
    public String goToPageProductEdit(@PathVariable Long id, Model model) {

        Product product = da.getProductById(id);
        product.setCategoryList(da.getCategoriesByProductId(id));

        List<String> multipleSelectedIdList = new ArrayList<>();

        for (Category category : product.getCategoryList()) {
            multipleSelectedIdList.add(category.getId().toString());
        }

        MultipleSelect<Category> multipleSelect = new MultipleSelect<>(da.getAllCategories(), multipleSelectedIdList);

        model
            .addAttribute("title", "Edit Product")
            .addAttribute("product", product)
            .addAttribute("multipleSelectionObj", multipleSelect);

        return "admin/add-product";
    }

    /**
     * Add product to database string.
     *
     * @param       product object to refer to when passing to database
     * @return      the string
     */
    @PostMapping("/admin/products/submit")
    public ModelAndView databaseUpdateProduct(
            @ModelAttribute("product") Product product
            , @ModelAttribute MultipleSelect<Category> multipleSelect
            , BindingResult bindingResult) {

        if (product.getCategoryList() == null) {
            product.setCategoryList(new ArrayList<>());
        }
        for (String id : multipleSelect.getSelectedIdList()) {
            product.getCategoryList().add(da.getCategoryById(Long.parseLong(id)));
        }

        if (product.getId() == null) {
            da.addProduct(product);
        } else {
            da.updateProduct(product);
        }

        return new ModelAndView("redirect:/products");
    }

    /**
     * expire product string.
     *
     * @param        id referred to for expiring product in database
     * @return       the string
     */
    @GetMapping("/admin/products/remove/{id}")
    public ModelAndView databaseExpireProduct(@PathVariable Long id, Model model) {

        da.expireProduct(Product.builder().id(id).build());

        return new ModelAndView("redirect:/products");
    }

    /**
     * Go to the categories page string.
     *
     * @param model the model give thymeleaf meaningful attributes
     * @return      the string
     */
    @GetMapping("/categories")
    public String goToPageCategories(Model model){

        model.addAttribute("categoryList", da.getAllCategories());

        return "view-categories";
    }

    /**
     * Go to add category page.
     *
     * @param model the model give thymeleaf meaningful attributes
     * @return      the string
     */
    @GetMapping("/admin/categories/add")
    public String goToPageCategoryAdd(Model model) {

        model.addAttribute("category", new Category());

        return "admin/add-category";
    }

    /**
     * Go to edit category page.
     *
     * @param model the model give thymeleaf meaningful attributes
     * @return      the string
     */
    @GetMapping("/admin/categories/edit/{id}")
    public String goToPageCategoryEdit(@PathVariable Long id, Model model) {

        model.addAttribute("category", da.getCategoryById(id));

        return "admin/add-category";
    }

    /**
     * Add category to database string.
     *
     * @param        category referred to for inserting/updating into database
     * @return       the string
     */
    @PostMapping("/admin/categories/submit")
    public String databaseUpdateCategory(@ModelAttribute Category category, Model model) {

        if (category.getId() == null) {
            da.addCategory(category);
        } else {
            da.updateCategory(category);
        }

        model.addAttribute("categoryList", da.getAllCategories());

        return "view-categories";
    }

    /**
     * expire category string.
     *
     * @param        id referred to for expiring category in database
     * @return       the string
     */
    @GetMapping("/admin/categories/remove/{id}")
    public ModelAndView databaseExpireCategory(@PathVariable Long id, Model model) {

        da.expireCategory(Category.builder().id(id).build());

        return new ModelAndView("redirect:/categories");
    }

}
