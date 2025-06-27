# Comparativa de Repositorios en Spring Data JPA

## Introducción

Spring Data JPA proporciona diferentes interfaces de repositorio para trabajar con datos de manera eficiente. Cada interfaz tiene un propósito específico y se construye sobre la anterior, añadiendo más funcionalidades.

---

## 1. CrudRepository

### Descripción
Es la interfaz base más fundamental que proporciona operaciones CRUD básicas.

### Métodos principales
- `save(T entity)` - Guardar/actualizar entidad
- `saveAll(Iterable<T> entities)` - Guardar múltiples entidades
- `findById(ID id)` - Buscar por ID (devuelve Optional)
- `existsById(ID id)` - Verificar si existe una entidad por ID
- `findAll()` - Obtener todas las entidades
- `findAllById(Iterable<ID> ids)` - Buscar múltiples por IDs
- `count()` - Contar registros totales
- `deleteById(ID id)` - Eliminar por ID
- `delete(T entity)` - Eliminar entidad específica
- `deleteAllById(Iterable<ID> ids)` - Eliminar múltiples por IDs
- `deleteAll()` - Eliminar todas las entidades

### Cuándo usarlo
Cuando solo necesitas operaciones básicas de CRUD sin paginación ni ordenamiento.

### Ejemplo de uso
```java
@Repository
public interface ProductCrudRepository extends CrudRepository<Product, Long> {
    // Heredas automáticamente todos los métodos CRUD básicos
}
```

---

## 2. PagingAndSortingRepository

### Descripción
Extiende `CrudRepository` y añade capacidades de paginación y ordenamiento.

### Métodos adicionales
- `findAll(Sort sort)` - Obtener todos los registros con ordenamiento
- `findAll(Pageable pageable)` - Obtener registros con paginación

### Cuándo usarlo
Cuando necesitas paginación y ordenamiento además de las operaciones CRUD básicas.

### Ejemplo de uso
```java
@Repository
public interface ProductPagingRepository extends PagingAndSortingRepository<Product, Long> {
    // CRUD básico + paginación y ordenamiento
}

// Uso en el servicio
@Service
public class ProductService {
    
    @Autowired
    private ProductPagingRepository repository;
    
    public Page<Product> getProducts(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return repository.findAll(pageable);
    }
}
```

---

## 3. JpaRepository

### Descripción
Es la interfaz más completa. Extiende `PagingAndSortingRepository` y añade funcionalidades específicas de JPA.

### Métodos adicionales
- `flush()` - Sincronizar cambios pendientes con la base de datos
- `saveAndFlush(T entity)` - Guardar entidad y sincronizar inmediatamente
- `saveAllAndFlush(Iterable<T> entities)` - Guardar múltiples y sincronizar
- `deleteInBatch(Iterable<T> entities)` - Eliminación en lote eficiente
- `deleteAllInBatch(Iterable<T> entities)` - Eliminar entidades específicas en lote
- `deleteAllByIdInBatch(Iterable<ID> ids)` - Eliminar por IDs en lote
- `deleteAllInBatch()` - Eliminar todas las entidades en lote
- `getOne(ID id)` - Obtener referencia lazy (**deprecated**)
- `getReferenceById(ID id)` - Obtener referencia lazy (reemplazo de getOne)

### Cuándo usarlo
Es la opción más común y recomendada para la mayoría de casos, ya que incluye todas las funcionalidades.

### Ejemplo de uso
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Métodos de consulta personalizados usando naming conventions
    List<Product> findByName(String name);
    List<Product> findByPriceGreaterThan(BigDecimal price);
    Page<Product> findByActiveTrue(Pageable pageable);
    
    // Consultas personalizadas con @Query
    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);
    
    // Consulta nativa
    @Query(value = "SELECT * FROM products WHERE price > :minPrice", nativeQuery = true)
    List<Product> findExpensiveProducts(@Param("minPrice") BigDecimal minPrice);
}
```

---

## 4. Interfaces Adicionales

### ListCrudRepository y ListPagingAndSortingRepository

**Introducidas en Spring Data 3.0**

Similares a las interfaces tradicionales pero devuelven `List` en lugar de `Iterable`, lo que es más conveniente para trabajar en Java.

```java
public interface ProductListRepository extends ListCrudRepository<Product, Long> {
    // Los métodos findAll() devuelven List<Product> en lugar de Iterable<Product>
}
```

### ReactiveCrudRepository

Para programación reactiva con Spring WebFlux:

```java
public interface ProductReactiveRepository extends ReactiveCrudRepository<Product, Long> {
    Flux<Product> findByName(String name);
    Mono<Product> findByCode(String code);
}
```

---

## Comparativa Completa

| Característica | CrudRepository | PagingAndSortingRepository | JpaRepository |
|----------------|----------------|----------------------------|---------------|
| **Operaciones CRUD básicas** | ✅ | ✅ | ✅ |
| **Paginación** | ❌ | ✅ | ✅ |
| **Ordenamiento** | ❌ | ✅ | ✅ |
| **Operaciones en lote** | ❌ | ❌ | ✅ |
| **Flush manual** | ❌ | ❌ | ✅ |
| **Referencias lazy** | ❌ | ❌ | ✅ |
| **Optimizado para JPA** | ❌ | ❌ | ✅ |

---

## Ejemplo Práctico Completo

```java
// Entidad
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    // Constructores, getters y setters
}

// Repositorio usando JpaRepository (RECOMENDADO)
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Consultas derivadas del nombre del método
    List<Product> findByActiveTrue();
    List<Product> findByNameContainingIgnoreCase(String name);
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // Consulta personalizada
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.price > :minPrice")
    List<Product> findActiveProductsAbovePrice(@Param("minPrice") BigDecimal minPrice);
    
    // Actualización personalizada
    @Modifying
    @Query("UPDATE Product p SET p.active = false WHERE p.price < :threshold")
    int deactivateCheapProducts(@Param("threshold") BigDecimal threshold);
}

// Servicio
@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public Page<Product> getActiveProducts(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return productRepository.findByActiveTrue(pageable);
    }
    
    public List<Product> saveProducts(List<Product> products) {
        return productRepository.saveAllAndFlush(products);
    }
    
    public void deactivateExpensiveProducts(BigDecimal threshold) {
        productRepository.deactivateCheapProducts(threshold);
        productRepository.flush(); // Asegurar sincronización inmediata
    }
}
```

---

## Recomendaciones

### ✅ Usa JpaRepository cuando:
- Desarrolles aplicaciones Spring Boot estándar
- Necesites todas las funcionalidades disponibles
- Quieras operaciones optimizadas para JPA
- Trabajes con paginación y ordenamiento
- Requieras operaciones en lote

### ✅ Usa PagingAndSortingRepository cuando:
- Solo necesites paginación y ordenamiento
- Quieras limitar las operaciones disponibles
- Trabajes con repositorios no-JPA

### ✅ Usa CrudRepository cuando:
- Solo necesites operaciones CRUD básicas
- Trabajes con repositorios simples
- Quieras la interfaz más minimalista

---

## Mejores Prácticas

1. **Prefiere JpaRepository** para la mayoría de casos de uso
2. **Usa @Transactional** en servicios que modifiquen datos
3. **Implementa paginación** para consultas que puedan devolver muchos resultados
4. **Usa métodos derivados** para consultas simples
5. **Usa @Query** para consultas complejas
6. **Considera usar @Modifying** para operaciones de actualización/eliminación masiva
7. **Usa flush()** cuando necesites sincronización inmediata con la base de datos

---

## Conclusión

**JpaRepository es la opción recomendada** para la mayoría de aplicaciones Spring Boot ya que:
- Incluye todas las funcionalidades de las demás interfaces
- Proporciona métodos optimizados para JPA
- Es el estándar de facto en proyectos Spring
- No tiene overhead significativo aunque no uses todas sus funcionalidades

Solo considera las otras interfaces si tienes restricciones específicas o quieres limitar explícitamente las operaciones disponibles en tu repositorio.