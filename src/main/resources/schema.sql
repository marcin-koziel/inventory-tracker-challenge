/**
 * Creating tables and appending data to h2 database
 *
 * @author Marcin Koziel
 */

CREATE TABLE IF NOT EXISTS AUTHORITIES (
    id              LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username        VARCHAR_IGNORECASE NOT NULL,
    authority       VARCHAR_IGNORECASE NOT NULL
);

CREATE TABLE IF NOT EXISTS USERS (
    id              LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username        VARCHAR_IGNORECASE NOT NULL,
    password        VARCHAR_IGNORECASE NOT NULL,
    firstName       VARCHAR_IGNORECASE NULL,
    lastName        VARCHAR_IGNORECASE NULL,
    enabled         BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS PRODUCTS (
    id              LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(128) NOT NULL,
    summary         VARCHAR(255) NOT NULL,
    description     VARCHAR(400) NOT NULL,
    createdAt       DATETIME NOT NULL,
    updatedAt       DATETIME NOT NULL,
    expiredAt       DATETIME NULL
);

CREATE TABLE IF NOT EXISTS REVIEWS (
    id              LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,
    productId       LONG NOT NULL,
    text            VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS METADATA (
    id              LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS CATEGORIES (
    id              LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(128) NOT NULL,
    expiredAt       DATETIME NULL
);

CREATE TABLE IF NOT EXISTS CATEGORIES_SUBCATEGORIES (
    Id              LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,
    categoryId      LONG NOT NULL,
    subcategoryId   LONG NOT NULL,
    expiredAt       DATETIME NULL
);

CREATE TABLE IF NOT EXISTS PRODUCT_CATEGORIES (
    Id                  LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,
    productId           LONG NOT NULL,
    categoryId          LONG NOT NULL,
    createdAt           DATETIME NOT NULL,
    updatedAt           DATETIME NOT NULL,
    expiredAt           DATETIME NULL
);

CREATE TABLE IF NOT EXISTS BRANDS (
    id              LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(128) NOT NULL,
    summary         VARCHAR(255) NOT NULL,
    createdAt       DATETIME NOT NULL,
    updatedAt       DATETIME NOT NULL,
    expiredAt       DATETIME NULL
);

CREATE TABLE IF NOT EXISTS ORDERS (
    id              LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,
    type            SMALLINT NOT NULL,
    status          SMALLINT NOT NULL,
    subTotal        FLOAT NOT NULL,
    itemDiscount    FLOAT NOT NULL,
    tax             FLOAT NOT NULL,
    shipping        FLOAT NOT NULL,
    total           FLOAT NOT NULL,
    promo           VARCHAR(50) NOT NULL,
    discount        FLOAT NOT NULL,
    grandTotal      FLOAT NOT NULL,
    createdAt       DATETIME NOT NULL,
    updatedAt       DATETIME NOT NULL,
    expiredAt       DATETIME NULL
);

CREATE TABLE IF NOT EXISTS ITEMS (
    id              LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,
    productId       LONG NOT NULL,
    brandId         LONG NOT NULL,
    supplierId      LONG NOT NULL,
    sku             VARCHAR(128) NOT NULL,
    discount        FLOAT NOT NULL,
    price           FLOAT NOT NULL,
    quantity        FLOAT NOT NULL,
    condition       VARCHAR_IGNORECASE NOT NULL,
    sold            SMALLINT NOT NULL,
    available       SMALLINT NOT NULL,
    defective       SMALLINT NOT NULL,
    createdBy       VARCHAR_IGNORECASE NOT NULL,
    updatedBy       VARCHAR_IGNORECASE NOT NULL,
    createdAt       DATETIME NOT NULL,
    updatedAt       DATETIME NOT NULL,
    expiredAt       DATETIME NULL
);

CREATE TABLE ITEM_ORDERS (
    id              LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,
    itemId          LONG NOT NULL,
    orderId         LONG NOT NULL
);

ALTER TABLE IF EXISTS REVIEWS
ADD CONSTRAINT FK_REVIEWS_PRODUCTS FOREIGN KEY (productId)
REFERENCES PRODUCTS (id);

ALTER TABLE IF EXISTS PRODUCT_CATEGORIES
ADD CONSTRAINT FK_PRODUCT_CATEGORIES_PRODUCTS FOREIGN KEY (productId)
REFERENCES PRODUCTS (id);

ALTER TABLE IF EXISTS PRODUCT_CATEGORIES
ADD CONSTRAINT FK_PRODUCT_CATEGORIES_CATEGORIES FOREIGN KEY (categoryId)
REFERENCES CATEGORIES (id);

ALTER TABLE IF EXISTS CATEGORIES_SUBCATEGORIES
ADD CONSTRAINT FK_CATEGORIES_SUBCATEGORIES_CATEGORIES FOREIGN KEY (categoryId)
REFERENCES CATEGORIES (id);

ALTER TABLE IF EXISTS CATEGORIES_SUBCATEGORIES
ADD CONSTRAINT FK_CATEGORIES_SUBCATEGORIES_CATEGORIES_2 FOREIGN KEY (subcategoryId)
REFERENCES CATEGORIES (id);

ALTER TABLE IF EXISTS ITEMS
ADD CONSTRAINT FK_ITEMS_PRODUCTS FOREIGN KEY (productId)
REFERENCES PRODUCTS (id);

ALTER TABLE IF EXISTS ITEMS
ADD CONSTRAINT FK_ITEMS_BRANDS FOREIGN KEY (brandId)
REFERENCES BRANDS (id);

alter table if exists ITEMS
ADD CONSTRAINT FK_ITEMS_USERS FOREIGN KEY (supplierId)
REFERENCES USERS (id);

ALTER TABLE IF EXISTS ITEM_ORDERS
ADD CONSTRAINT FK_ITEM_ORDERS_PRODUCTS FOREIGN KEY (itemId)
REFERENCES ITEMS (id);

ALTER TABLE IF EXISTS ITEM_ORDERS
ADD CONSTRAINT FK_ITEM_ORDERS_ORDERS FOREIGN KEY (orderId)
REFERENCES ORDERS (id);

INSERT INTO AUTHORITIES (username, authority)
VALUES
       ( 'TheToyInsider', 'ROLE_SUPPLIER'),
       ( 'Articture', 'ROLE_SUPPLIER'),
       ( 'Gcouletpens', 'ROLE_SUPPLIER'),
       ( 'Kith', 'ROLE_SUPPLIER'),
       ( 'OctobersVeryOwn', 'ROLE_SUPPLIER');

INSERT INTO USERS (username, password, firstName, lastName, enabled)
VALUES
        ( 'TheToyInsider', '$2a$10$BJEVl9QB.UCwXFDXS1WAjegOkRzu5mFIa.vtdK.K0enq4J0yDe69S', 'Laurie', 'Schacht', true)
        ,( 'Articture', '$2a$10$BJEVl9QB.UCwXFDXS1WAjegOkRzu5mFIa.vtdK.K0enq4J0yDe69S', 'Robert', 'Walker', true)
        ,( 'Gcouletpens', '$2a$10$BJEVl9QB.UCwXFDXS1WAjegOkRzu5mFIa.vtdK.K0enq4J0yDe69S', 'Justin', 'Smith', true)
        ,( 'Kith', '$2a$10$BJEVl9QB.UCwXFDXS1WAjegOkRzu5mFIa.vtdK.K0enq4J0yDe69S', 'Angel', 'Turner', true)
        ,( 'OctobersVeryOwn', '$2a$10$BJEVl9QB.UCwXFDXS1WAjegOkRzu5mFIa.vtdK.K0enq4J0yDe69S', 'Louis', 'McCracken', true);

INSERT INTO BRANDS (title, summary, createdAt, updatedAt)
VALUES
        (
        'ColourPop Cosmetics'
        , 'ColourPop Cosmetics, also known as ColourPop, is an American cosmetics brand based in Los Angeles, California.'
        , NOW(), NOW()
        )
        ,(
        'Gymshark | Official Store'
        , 'Gymshark is a British fitness apparel and accessories brand, manufacturer and retailer headquartered in Solihull, England.'
        , NOW(), NOW()
        )
        ,( 'The Goulet Pen Company'
        , 'Gymshark is a British fitness apparel and accessories brand, manufacturer and retailer headquartered in Solihull, England.'
        , NOW(), NOW()
        )
        ,( 'Lovevery', 'Lovevery is an American company producing play-kit subscription boxes for children.', NOW(), NOW())
        ,( 'OctobersVeryOwn', 'LThe OVO® Heritage Collection is designed to celebrate Canadian Icons.', NOW(), NOW())
        ,( 'Articture', 'LThe OVO® Heritage Collection is designed to celebrate Canadian Icons.', NOW(), NOW())
        ,( 'Kith', 'What does Kith brand stand for? Friends and family.', NOW(), NOW())
        ,( 'Gouletpens', 'Goulet Pens provides fountain pen enthusiasts the most personal online shopping experience through comprehensive education, exemplary service, and products.', NOW(), NOW());

INSERT INTO CATEGORIES (title)
VALUES ( 'Clothing' ), ('Jewelry & Watches'), ('Home & Garden')
       , ('Fashion Accessories'), ('Health & Beauty'), ('Accessories')
       , ('Toys & Hobbies'), ('Shoes'), ('Books'), ('Food & Beverages');

INSERT INTO PRODUCTS (name, summary, description, createdAt, updatedAt)
VALUES (
        'Play Kit (0-12 Months)'
        , 'This Play Kit helps them build new brain connections with high-contrast images and black and white sensory mittens.'
        , 'Your baby can see about 8-14 inches from their face, and they view the world in shades of black, white, and gray. That’s why this first Play Kit features high contrast, black and white objects. Images your baby can distinguish from the blurriness of the world around them are helpful as their eyes begin to work together, focus, adjust to light, and process color.'
        , NOW()
        , NOW()
       )
       ,(
           'Kith & Nike Air Force 1'
       , 'Air Force 1 silhouette. Premium pebbled leather upper. Perforated toe box. Co-branded lace dubrae.'
       , 'Gradient co-branded Kith x Nike woven label on tongue. Asymmetrical gradient blue and orange TPU Swoosh logo on side panel. The NYC Swoosh logo on the lateral heel panels. Kith A I R embroidery at heel. Flat cotton laces. Nike Air sole unit. Icey blue translucent outsole.'
       , NOW()
       , NOW()
       )
       ,(
        'Pelikan M605 Fountain Pen'
        , 'This distinctive new fine writing instruments series will convince you. These colors will brighten your day!'
        , 'The fountain pen has a completely rhodium-plated 14kt gold nib. The smooth operating piston mechanism allows you to fill this pen with bottled ink. These special edition Souverän® pens are presented in a beautiful Pelikan gift box.'
        , NOW()
        , NOW()
       )
       ,(
           'Tranquility Duvet Cover Set'
       , 'Give your bedroom an instant makeover with the 4-piece Tranquility set.'
       , 'An Articture Bedding Collection Exclusive, the Tranquility gives classic toile design a cozy, lived-in look that combines sophisticated luxury with the relaxing, breathable comfort of 100% Egyptian cotton.'
       , NOW()
       , NOW()
       );


INSERT INTO PRODUCT_CATEGORIES (productId, categoryId, createdAt, updatedAt)
VALUES
       ( 1, 7, NOW(), NOW() )
       , ( 2, 1, NOW(), NOW() ), ( 2, 8, NOW(), NOW() )
       , ( 3, 6, NOW(), NOW() )
       , ( 4, 3, NOW(), NOW() );

INSERT INTO ITEMS (productId, brandId, supplierId, sku, discount, price, quantity, condition, sold, available, defective, createdBy, updatedBy, createdAt, updatedAt)
VALUES
        ( 1, 4, 1, '4TDOA4Y7', 10.50, 79.50, 50, 'BRAND_NEW', 16, 33, 1, 'BestRecruiter', 'BestRecruiter', '2022-01-19 20:44:25.668411', '2022-01-19 20:44:25.668411')
        ,( 2, 7, 3, '0I0V5861', 28.95, 499.98, 20, 'BRAND_NEW', 6, 14, 0, 'BestRecruiter', 'BestRecruiter', '2022-01-19 20:44:25.668411', '2022-01-19 20:44:25.668411')
        ,( 3, 8, 4, '96534CZE', 20.00, 449.50, 60, 'BRAND_NEW', 28, 40, 2, 'BestRecruiter', 'BestRecruiter', '2022-01-19 20:44:25.668411', '2022-01-19 20:44:25.668411')
        ,( 4, 6, 5, 'UJB9FJ4T', 15.20, 146.50, 30.80, 'BRAND_NEW', 12, 18, 0, 'BestRecruiter', 'BestRecruiter', '2022-01-19 20:44:25.668411', '2022-01-19 20:44:25.668411');