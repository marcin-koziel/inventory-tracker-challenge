package ca.shopify.inventorytrackerchallenge.database;

import ca.shopify.inventorytrackerchallenge.beans.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Database access object to access our in-memory H2-database.
 *
 * @author Marcin Koziel
 */
@Repository
public class DatabaseAccess {

    /**
     * *****************************************
     * Initialize class
     * *****************************************
     */

    @Autowired
    private final NamedParameterJdbcTemplate jdbc;

    /**
     * Instantiates a new Database access.
     *
     * @param jdbc the jdbc
     */
    public DatabaseAccess(NamedParameterJdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    /**
     * *****************************************
     * Section: Category
     * *****************************************
     */

    /**
     * Add Category.
     *
     * @param      category object that is provided to add
     * @return     the autogenerated Id
     */
    public int addCategory(Category category){

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "INSERT INTO categories (title) VALUES (:title)";
        namedParameters.addValue("title", category.getTitle());

        jdbc.update(query, namedParameters, keyHolder);

        int categoryId = keyHolder.getKey().intValue();

        return categoryId;
    }

    /**
     * Update Category.
     *
     * @param       category object that is provided to add
     * @return      the int returnValue of JdbcTemplate
     */
    public int updateCategory(Category category){

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "UPDATE categories SET title = :title WHERE id = :id";
        namedParameters
                .addValue("id", category.getId())
                .addValue("title", category.getTitle());

        int returnValue = jdbc.update(query, namedParameters);

        return returnValue;
    }

    /**
     * Expire Category.
     *
     * @param       category object that is provided to add
     * @return      the int returnValue of JdbcTemplate
     */
    public int expireCategory(Category category){

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "UPDATE categories SET expiredAt = NOW() WHERE id = :id";
        namedParameters.addValue("id", category.getId());

        int returnValue = jdbc.update(query, namedParameters);

        return returnValue;
    }

    /**
     * Get Category from database.
     *
     * @param       id the id of a category
     * @return      the category object based on id provided
     */
    public Category getCategoryById(Long id){

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "SELECT * FROM categories WHERE id = :id";
        namedParameters.addValue("id", id);

        BeanPropertyRowMapper<Category> categoryMapper = new BeanPropertyRowMapper<Category>(Category.class);

        List<Category> categoryList = jdbc.query(query, namedParameters, categoryMapper);

        if (!categoryList.isEmpty()) {
            return categoryList.get(0);
        } else {
            return null;
        }

    }

    /**
     * Get Category objects from database based on Product Id.
     *
     * @param       id the id of a product
     * @return      object(s) based on id provided
     */
    public List<Category> getCategoriesByProductId(Long id){

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "SELECT c.id, c.title, c.expiredAt FROM categories c INNER JOIN PRODUCT_CATEGORIES pc ON c.id = pc.categoryId WHERE pc.productId = :productId AND c.expiredAt IS NULL";

        namedParameters.addValue("productId", id);

        BeanPropertyRowMapper<Category> categoryMapper = new BeanPropertyRowMapper<Category>(Category.class);

        List<Category> categoryList = jdbc.query(query, namedParameters, categoryMapper);

        return categoryList;
    }

    /**
     * Gets all categories (not expired).
     *
     * @return the categories
     */
    public List<Category> getAllCategories() {

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "SELECT * FROM categories WHERE expiredAt IS NULL";

        BeanPropertyRowMapper<Category> agentsMapper =
                new BeanPropertyRowMapper<Category>(Category.class);

        List<Category> categoriesList = jdbc.query(query, namedParameters, agentsMapper);

        return categoriesList;
    }

    /**
     * *****************************************
     * Section: Product
     * *****************************************
     */

    /**
     * Add Product.
     *
     * @param       product object that is provided to add
     * @return      the autogenerated Id
     */
    public int addProduct(Product product){

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "INSERT INTO products (name, summary, description, createdAt, updatedAt) VALUES (:name, :summary, :description, NOW(), NOW())";
        namedParameters
                .addValue("name", product.getName())
                .addValue("summary", product.getSummary())
                .addValue("description", product.getDescription());

        int returnValue = jdbc.update(query, namedParameters, keyHolder);

        int productId = keyHolder.getKey().intValue();

        product.setId(Integer.toUnsignedLong(productId));

        for (Category category : product.getCategoryList()) {
            addProductCategory(product, category);
        }

        return returnValue;
    }

    /**
     * Update Product.
     *
     * @param       product object that is provided to add
     * @return      the int returnValue of JdbcTemplate
     */
    public int updateProduct(Product product){

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query =
                "UPDATE products SET name = :name, summary = :summary, description = :description, updatedAt = NOW() WHERE id = :id";

        namedParameters
                .addValue("id", product.getId())
                .addValue("name", product.getName())
                .addValue("summary", product.getSummary())
                .addValue("description", product.getDescription());

        int returnValue = jdbc.update(query, namedParameters);

        updateProductCategories(product);

        return returnValue;
    }

    /**
     * Add Product Categories Relationship record.
     *
     * @param       product object that is provided to reference
     * @return      the int returnValue of JdbcTemplate
     */
    public int updateProductCategories(Product product){

        MapSqlParameterSource namedParameters;
        int returnValue = 0;

        String query = "UPDATE PRODUCT_CATEGORIES SET expiredAt = NULL WHERE productId = :productId AND categoryId = :categoryId";

        expireProductCategories(product);

        for (Category category : product.getCategoryList()) {
            namedParameters = new MapSqlParameterSource();
            namedParameters
                    .addValue("productId", product.getId())
                    .addValue("categoryId", category.getId());

            returnValue = jdbc.update(query, namedParameters);

            if (returnValue == 0) {
                addProductCategory(product, category);
            }
        }

        return returnValue;
    }

    /**
     * Expire Product.
     *
     * @param       product object that is provided to reference
     * @return      the int returnValue of JdbcTemplate
     */
    public int expireProduct(Product product){

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "UPDATE products SET expiredAt = NOW() WHERE id = :id";
        namedParameters.addValue("id", product.getId());

        int returnValue = jdbc.update(query, namedParameters);

        return returnValue;
    }

    /**
     * Get Product object from database.
     *
     * @param        id the id of a Product
     * @return       object based on id provided
     */
    public Product getProductById(Long id){

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "SELECT * FROM products WHERE id = :id";
        namedParameters.addValue("id", id);

        BeanPropertyRowMapper<Product> categoryMapper = new BeanPropertyRowMapper<Product>(Product.class);

        List<Product> productList = jdbc.query(query, namedParameters, categoryMapper);

        if (!productList.isEmpty()) {
            return productList.get(0);
        } else {
            return null;
        }
    }

    /**
     * Gets all Products.
     *
     * @return the products
     */
    public List<Product> getAllProducts() {

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "SELECT * FROM products WHERE expiredAt IS NULL";

        BeanPropertyRowMapper<Product> agentsMapper =
                new BeanPropertyRowMapper<Product>(Product.class);

        List<Product> productList = jdbc.query(query, namedParameters, agentsMapper);

        return productList;
    }

    /**
     * Add Product Category Relationship record.
     *
     * @param       product object that is provided to reference
     * @param       category object that is provided to reference
     * @return     the int returnValue of JdbcTemplate
     */
    public int addProductCategory(Product product, Category category){

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "INSERT INTO product_categories (productId, categoryId, createdAt, updatedAt) VALUES (:productId, :categoryId, NOW(), NOW())";
        namedParameters
                .addValue("productId", product.getId())
                .addValue("categoryId", category.getId());

        int returnValue = jdbc.update(query, namedParameters);

        return returnValue;
    }

    /**
     * Expire Product Categories.
     *
     * @param       product object that is provided to reference
     * @return      the int returnValue of JdbcTemplate
     */
    public int expireProductCategories(Product product){

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "UPDATE product_categories SET expiredAt = NOW() WHERE id = :id";
        namedParameters.addValue("id", product.getId());

        int returnValue = jdbc.update(query, namedParameters);

        return returnValue;
    }

    /**
     * *****************************************
     * Section: Brand
     * *****************************************
     */

    /**
     * Gets all Brands.
     *
     * @return the brands
     */
    public List<Brand> getAllBrands() {

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "SELECT * FROM brands WHERE expiredAt IS NULL";

        BeanPropertyRowMapper<Brand> brandsMapper =
                new BeanPropertyRowMapper<Brand>(Brand.class);

        List<Brand> brandList = jdbc.query(query, namedParameters, brandsMapper);

        return brandList;
    }

    /**
     * Get a Brand object from database.
     *
     * @param       id of a Brand
     * @return      object based on id provided
     */
    public Brand getBrandById(Long id){

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "SELECT * FROM brands WHERE id = :id";
        namedParameters.addValue("id", id);

        BeanPropertyRowMapper<Brand> brandMapper = new BeanPropertyRowMapper<Brand>(Brand.class);

        List<Brand> brandList = jdbc.query(query, namedParameters, brandMapper);

        if (!brandList.isEmpty()) {
            return brandList.get(0);
        } else {
            return null;
        }
    }

    /**
     * *****************************************
     * Section: Item
     * *****************************************
     */

    /**
     * Add Item.
     *
     * @param       item that is provided to add to database
     * @return      the int returnValue of JdbcTemplate
     */
    public int addItem(Item item){

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        int ItemId = 0;

        String query = "INSERT INTO ITEMS (productId, brandId, supplierId, sku, discount, price, quantity, condition, sold, available, defective, createdBy, updatedBy, createdAt, updatedAt) " +
                "VALUES (:productId, :brandId, :supplierId, :sku, :discount, :price, :quantity, :condition, :sold, :available, :defective, :createdBy, :updatedBy, NOW(), NOW())";

        namedParameters
                .addValue("productId", item.getProduct().getId())
                .addValue("brandId", item.getBrand().getId())
                .addValue("supplierId", item.getSupplier().getId())
                .addValue("sku", item.getSku())
                .addValue("discount", item.getDiscount())
                .addValue("price", item.getPrice())
                .addValue("quantity", item.getQuantity())
                .addValue("condition", item.getCondition().name())
                .addValue("sold", item.getSold())
                .addValue("available", item.getAvailable())
                .addValue("defective", item.getDefective())
                .addValue("createdBy", item.getCreatedBy().getUsername())
                .addValue("updatedBy", item.getUpdatedBy().getUsername());

        int returnValue = jdbc.update(query, namedParameters, keyHolder);

        if (returnValue == 1) {
            ItemId = keyHolder.getKey().intValue();
        }

        return ItemId;
    }

    /**
     * Add Product.
     *
     * @param       item that is provided to reference to update
     * @return      the int returnValue of JdbcTemplate
     */
    public int updateItem(Item item){

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query =
                "UPDATE items " +
                        "SET productId = :productId" +
                        ", brandId = :brandId" +
                        ", supplierId = :supplierId" +
                        ", sku = :sku" +
                        ", discount = :discount" +
                        ", price = :price" +
                        ", quantity = :quantity" +
                        ", condition = :condition" +
                        ", sold = :sold" +
                        ", available = :available" +
                        ", defective = :defective" +
                        ", updatedBy = :updatedBy" +
                        ", updatedAt = NOW()" +
                        "WHERE id = :id";

        namedParameters
                .addValue("id", item.getId())
                .addValue("productId", item.getProduct().getId())
                .addValue("brandId", item.getBrand().getId())
                .addValue("supplierId", item.getSupplier().getId())
                .addValue("sku", item.getSku())
                .addValue("discount", item.getDiscount())
                .addValue("price", item.getPrice())
                .addValue("quantity", item.getQuantity())
                .addValue("condition", item.getCondition().name())
                .addValue("sold", item.getSold())
                .addValue("available", item.getAvailable())
                .addValue("defective", item.getDefective())
                .addValue("updatedBy", item.getUpdatedBy().getUsername());

        int returnValue = jdbc.update(query, namedParameters);

        return returnValue;
    }

    /**
     * Expire Product.
     *
     * @param       item object that is provided to expire
     * @return      the int returnValue of JdbcTemplate
     */
    public int expireItem(Item item){

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "UPDATE items SET expiredAt = NOW() WHERE id = :id";
        namedParameters.addValue("id", item.getId());

        int returnValue = jdbc.update(query, namedParameters);

        return returnValue;
    }

    /**
     * Gets all Items.
     *
     * @return the items list
     */
    public List<Item> getAllItems() {

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "SELECT * FROM items WHERE expiredAt IS NULL";

        List<Item> itemList = jdbc.query(query, namedParameters, new ResultSetExtractor<List<Item>>() {
            @Override
            public List<Item> extractData(ResultSet rs) throws SQLException, DataAccessException {

                List<Item> itemList = new ArrayList<>();

                while (rs.next()) {
                    Item item = Item.builder()
                            .Id(rs.getLong("id"))
                            .product(getProductById(rs.getLong("productId")))
                            .brand(getBrandById(rs.getLong("brandId")))
                            .supplier(getSupplierById(rs.getLong("supplierId")))
                            .sku(rs.getString("sku"))
                            .discount(rs.getFloat("discount"))
                            .price(rs.getFloat("price"))
                            .quantity(rs.getFloat("quantity"))
                            .condition(Item.Condition.valueOf(rs.getString("condition")))
                            .sold(rs.getInt("sold"))
                            .available(rs.getInt("available"))
                            .defective(rs.getInt("defective"))
                            .createdBy(getUserByName(rs.getString("createdBy")))
                            .updatedBy(getUserByName(rs.getString("updatedBy")))
                            .createdAt(rs.getObject("createdAt", LocalDateTime.class))
                            .updatedAt(rs.getObject("updatedAt", LocalDateTime.class))
                            .build();

                    item.getProduct().setCategoryList(getCategoriesByProductId(item.getProduct().getId()));

                    itemList.add(item);
                }
                return itemList;
            }
        });

        return itemList;
    }

    /**
     * Get Item from database.
     *
     * @param       id of a item
     * @return      object based on id provided
     */
    public Item getItemById(Long id){

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "SELECT * FROM items WHERE id = :id";
        namedParameters.addValue("id", id);

        Item item = jdbc.query(query, namedParameters, new ResultSetExtractor<Item>() {
            @Override
            public Item extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    Item item = Item.builder()
                            .Id(id)
                            .product(getProductById(rs.getLong("productId")))
                            .brand(getBrandById(rs.getLong("brandId")))
                            .supplier(getSupplierById(rs.getLong("supplierId")))
                            .sku(rs.getString("sku"))
                            .discount(rs.getFloat("discount"))
                            .price(rs.getFloat("price"))
                            .quantity(rs.getFloat("quantity"))
                            .condition(Item.Condition.valueOf(rs.getString("condition")))
                            .sold(rs.getInt("sold"))
                            .available(rs.getInt("available"))
                            .defective(rs.getInt("defective"))
                            .createdBy(getUserByName(rs.getString("createdBy")))
                            .updatedBy(getUserByName(rs.getString("updatedBy")))
                            .createdAt(rs.getObject("createdAt", LocalDateTime.class))
                            .updatedAt(rs.getObject("updatedAt", LocalDateTime.class))
                            .build();

                    return item;
                }
                return null;

            }
        });

        return item;
    }

    /**
     * *****************************************
     * Section: User
     * *****************************************
     */

    /**
     * Get username object by referencing name.
     *
     * @param           name the username provided to reference
     * @return          the Supplier(User) object
     */
    public Supplier getUserByName(String name) {

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "SELECT id, username, firstName, lastName FROM users WHERE username = :name";

        namedParameters.addValue("name", name);

        BeanPropertyRowMapper<Supplier> usersMapper =
                new BeanPropertyRowMapper<Supplier>(Supplier.class);

        List<Supplier> supplierList = jdbc.query(query, namedParameters, usersMapper);

        if (supplierList.isEmpty()) {
            return null;
        } else {
            return supplierList.get(0);
        }
    }

    /**
     * Get all Users by referencing name.
     *
     * @return         the Supplier(User) objects
     */
    public List<Supplier> getAllSuppliers() {

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query =
                "SELECT u.id, u.username, u.firstName, u.lastName FROM USERS u " +
                        "INNER JOIN AUTHORITIES a " +
                        "ON u.username = a.username " +
                        "WHERE a.authority = :role";

        namedParameters.addValue("role", "ROLE_SUPPLIER");

        BeanPropertyRowMapper<Supplier> usersMapper = new BeanPropertyRowMapper<Supplier>(Supplier.class);

        List<Supplier> supplierList = jdbc.query(query, namedParameters, usersMapper);

        return supplierList;
    }

    /**
     * Get Supplier(User) object from database.
     *
     * @param       id the id of a book
     * @return      object based on id provided
     */
    public Supplier getSupplierById(Long id){

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "SELECT * FROM users WHERE id = :id";
        namedParameters.addValue("id", id);

        BeanPropertyRowMapper<Supplier> supplierMapper = new BeanPropertyRowMapper<Supplier>(Supplier.class);

        List<Supplier> supplierList = jdbc.query(query, namedParameters, supplierMapper);

        if (!supplierList.isEmpty()) {
            return supplierList.get(0);
        } else {
            return null;
        }
    }

    /**
     * Is username taken boolean.
     *
     * @param           username the username provided to reference
     * @return          the boolean is username is taken
     */
    public boolean isUsernameTaken(String username) {

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        String query = "SELECT DISTINCT username FROM users";

        List<String> usernameList = jdbc.queryForList(query, namedParameters, String.class);

        return usernameList.contains(username);
    }

}
