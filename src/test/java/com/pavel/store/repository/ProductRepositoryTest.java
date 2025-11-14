package com.pavel.store.repository;

import com.pavel.store.entity.Category;
import com.pavel.store.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true"
})
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category electronics;
    private Category books;
    private Product laptop;
    private Product smartphone;
    private Product novel;

    @BeforeEach
    void setUp() {
        // Создаем категории
        electronics = Category.builder()
                .name("Electronics")
                .build();
        entityManager.persist(electronics);

        books = Category.builder()
                .name("Books")
                .build();
        entityManager.persist(books);

        // Создаем продукты
        laptop = Product.builder()
                .name("Laptop")
                .article("LAP001")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .amount(10)
                .isAvailable(true)
                .category(electronics)
                .build();
        entityManager.persist(laptop);

        smartphone = Product.builder()
                .name("Smartphone")
                .article("PHONE001")
                .description("Latest smartphone")
                .price(new BigDecimal("699.99"))
                .amount(20)
                .isAvailable(true)
                .category(electronics)
                .build();
        entityManager.persist(smartphone);

        novel = Product.builder()
                .name("Novel")
                .article("BOOK001")
                .description("Bestseller novel")
                .price(new BigDecimal("19.99"))
                .amount(50)
                .isAvailable(true)
                .category(books)
                .build();
        entityManager.persist(novel);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findAll_WithPageable_ShouldReturnPagedProducts() {

        Pageable pageable = PageRequest.of(0, 10);


        Page<Product> result = productRepository.findAll(pageable);


        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.getContent().stream().anyMatch(p -> p.getName().equals("Laptop")));
    }

    @Test
    void findById_WithExistingId_ShouldReturnProduct() {

        Optional<Product> result = productRepository.findById(laptop.getId());


        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getName());
        assertEquals("Electronics", result.get().getCategory().getName());
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {

        Optional<Product> result = productRepository.findById(999L);


        assertFalse(result.isPresent());
    }

    @Test
    void findByName_WithExistingName_ShouldReturnProduct() {

        Optional<Product> result = productRepository.findByName("Laptop");


        assertTrue(result.isPresent());
        assertEquals(laptop.getId(), result.get().getId());
    }

    @Test
    void findByName_WithNonExistingName_ShouldReturnEmpty() {

        Optional<Product> result = productRepository.findByName("NonExistingProduct");


        assertFalse(result.isPresent());
    }
    
    @Test
    void updateAllPrices_ShouldUpdateAllProductPrices() {

        BigDecimal multiplier = new BigDecimal("1.1"); // 10% increase
        BigDecimal initialLaptopPrice = laptop.getPrice();
        BigDecimal initialSmartphonePrice = smartphone.getPrice();

        // Устанавливаем масштаб 2 знака после запятой и округление
        BigDecimal expectedLaptopPrice = initialLaptopPrice.multiply(multiplier)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal expectedSmartphonePrice = initialSmartphonePrice.multiply(multiplier)
                .setScale(2, BigDecimal.ROUND_HALF_UP);


        productRepository.updateAllPrices(multiplier);
        entityManager.flush();
        entityManager.clear();


        Product updatedLaptop = entityManager.find(Product.class, laptop.getId());
        Product updatedSmartphone = entityManager.find(Product.class, smartphone.getId());

        // Проверяем, что цены изменились согласно множителю
        assertEquals(0, expectedLaptopPrice.compareTo(updatedLaptop.getPrice()),
                String.format("Laptop price: expected %s, but was %s", expectedLaptopPrice, updatedLaptop.getPrice()));

        assertEquals(0, expectedSmartphonePrice.compareTo(updatedSmartphone.getPrice()),
                String.format("Smartphone price: expected %s, but was %s", expectedSmartphonePrice, updatedSmartphone.getPrice()));
    }

    @Test
    void findByCategoryName_ShouldReturnProductsByCategory() {

        Pageable pageable = PageRequest.of(0, 10);


        Page<Product> result = productRepository.findByCategoryName("Electronics", pageable);


        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .allMatch(p -> p.getCategory().getName().equals("Electronics")));
    }

    @Test
    void findByCategoryName_WithNonExistingCategory_ShouldReturnEmptyPage() {

        Pageable pageable = PageRequest.of(0, 10);


        Page<Product> result = productRepository.findByCategoryName("NonExisting", pageable);


        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }


    @Test
    void findAllForUpdate_ShouldReturnAllProducts() {

        List<Product> result = productRepository.findAllForUpdate();


        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void findByIdForUpdate_WithExistingId_ShouldReturnProduct() {

        Optional<Product> result = productRepository.findByIdForUpdate(laptop.getId());


        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getName());
    }

    @Test
    void findByIdForUpdate_WithNonExistingId_ShouldReturnEmpty() {

        Optional<Product> result = productRepository.findByIdForUpdate(999L);


        assertFalse(result.isPresent());
    }

    @Test
    void findAll_WithSpecification_ShouldReturnFilteredProducts() {

        Pageable pageable = PageRequest.of(0, 10);


        Specification<Product> priceSpecification = (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("price"), new BigDecimal("500"));

        Page<Product> result = productRepository.findAll(priceSpecification, pageable);


        assertNotNull(result);
        assertEquals(2, result.getTotalElements()); // Laptop и Smartphone
        assertTrue(result.getContent().stream()
                .allMatch(p -> p.getPrice().compareTo(new BigDecimal("500")) > 0));
    }

    @Test
    void findAll_WithCategorySpecification_ShouldReturnFilteredProducts() {

        Pageable pageable = PageRequest.of(0, 10);


        Specification<Product> categorySpecification = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category").get("name"), "Electronics");


        Page<Product> result = productRepository.findAll(categorySpecification, pageable);


        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .allMatch(p -> p.getCategory().getName().equals("Electronics")));
    }

    @Test
    void save_NewProduct_ShouldPersistProduct() {

        Product newProduct = Product.builder()
                .name("Tablet")
                .article("TAB001")
                .description("New tablet")
                .price(new BigDecimal("299.99"))
                .amount(15)
                .isAvailable(true)
                .category(electronics)
                .build();


        Product savedProduct = productRepository.save(newProduct);
        entityManager.flush();
        entityManager.clear();

        assertNotNull(savedProduct.getId());
        Product retrievedProduct = entityManager.find(Product.class, savedProduct.getId());
        assertEquals("Tablet", retrievedProduct.getName());
        assertEquals("Electronics", retrievedProduct.getCategory().getName());
    }

    @Test
    void delete_ExistingProduct_ShouldRemoveProduct() {

        productRepository.delete(laptop);
        entityManager.flush();
        entityManager.clear();


        Product deletedProduct = entityManager.find(Product.class, laptop.getId());
        assertNull(deletedProduct);
    }

    @Test
    void count_ShouldReturnTotalNumberOfProducts() {

        long count = productRepository.count();


        assertEquals(3, count);
    }
}