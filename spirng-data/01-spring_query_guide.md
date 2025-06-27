# Guía completa de consultas en Spring Data JPA

Esta guía presenta las diferentes formas de realizar consultas personalizadas en Spring Data JPA, desde las más simples hasta las más avanzadas.

## Índice
1. [Consultas con @Query (JPQL)](#1-consultas-con-query-jpql)
2. [Consultas con @Query (SQL Nativo)](#2-consultas-con-query-sql-nativo)
3. [Consultas con Specification](#3-consultas-con-specification)
4. [Repositorio personalizado con Criteria API](#4-repositorio-personalizado-con-criteria-api)
5. [Comparación de enfoques](#5-comparación-de-enfoques)
6. [Casos de uso recomendados](#6-casos-de-uso-recomendados)

---

## 1. Consultas con @Query (JPQL)

### Configuración básica

```java
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String email;
    private Integer edad;
    private Boolean activo;
    
    // constructores, getters y setters
}
```

### Repository con @Query JPQL

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Consulta simple con parámetros posicionales
    @Query("SELECT u FROM Usuario u WHERE u.nombre = ?1")
    List<Usuario> findByNombre(String nombre);
    
    // Consulta con parámetros nombrados
    @Query("SELECT u FROM Usuario u WHERE u.nombre = :nombre AND u.edad > :edad")
    List<Usuario> findByNombreAndEdadMayorQue(@Param("nombre") String nombre, 
                                             @Param("edad") Integer edad);
    
    // Consulta con LIKE
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Usuario> findByNombreContaining(@Param("nombre") String nombre);
    
    // Consulta con múltiples condiciones
    @Query("SELECT u FROM Usuario u WHERE " +
           "(:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:email IS NULL OR u.email = :email) AND " +
           "(:edadMin IS NULL OR u.edad >= :edadMin) AND " +
           "(:edadMax IS NULL OR u.edad <= :edadMax)")
    List<Usuario> findByFiltros(@Param("nombre") String nombre,
                               @Param("email") String email,
                               @Param("edadMin") Integer edadMin,
                               @Param("edadMax") Integer edadMax);
    
    // Consulta con ordenación
    @Query("SELECT u FROM Usuario u WHERE u.activo = true ORDER BY u.nombre ASC")
    List<Usuario> findUsuariosActivosOrdenados();
    
    // Consulta con paginación (usar con Pageable)
    @Query("SELECT u FROM Usuario u WHERE u.edad BETWEEN :edadMin AND :edadMax")
    Page<Usuario> findByEdadBetween(@Param("edadMin") Integer edadMin,
                                   @Param("edadMax") Integer edadMax,
                                   Pageable pageable);
    
    // Consulta de agregación
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.activo = true")
    Long contarUsuariosActivos();
    
    // Consulta que retorna campos específicos
    @Query("SELECT new com.ejemplo.dto.UsuarioDTO(u.id, u.nombre, u.email) " +
           "FROM Usuario u WHERE u.activo = true")
    List<UsuarioDTO> findUsuariosActivosDTO();
    
    // Consulta de modificación
    @Modifying
    @Query("UPDATE Usuario u SET u.activo = false WHERE u.email = :email")
    int desactivarUsuarioPorEmail(@Param("email") String email);
    
    // Consulta de eliminación
    @Modifying
    @Query("DELETE FROM Usuario u WHERE u.activo = false")
    int eliminarUsuariosInactivos();
}
```

---

## 2. Consultas con @Query (SQL Nativo)

### Repository con SQL Nativo

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Consulta SQL nativa básica
    @Query(value = "SELECT * FROM usuario WHERE nombre = ?1", nativeQuery = true)
    List<Usuario> findByNombreNativo(String nombre);
    
    // Consulta SQL nativa con parámetros nombrados
    @Query(value = "SELECT * FROM usuario WHERE nombre = :nombre AND edad > :edad", 
           nativeQuery = true)
    List<Usuario> findByNombreAndEdadNativo(@Param("nombre") String nombre, 
                                           @Param("edad") Integer edad);
    
    // Consulta SQL nativa con LIKE
    @Query(value = "SELECT * FROM usuario WHERE LOWER(nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))", 
           nativeQuery = true)
    List<Usuario> findByNombreContainingNativo(@Param("nombre") String nombre);
    
    // Consulta SQL nativa compleja con múltiples JOINs
    @Query(value = """
        SELECT u.*, p.nombre as perfil_nombre 
        FROM usuario u 
        LEFT JOIN perfil p ON u.perfil_id = p.id 
        WHERE (:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) 
        AND (:activo IS NULL OR u.activo = :activo)
        ORDER BY u.nombre
        """, nativeQuery = true)
    List<Object[]> findUsuariosConPerfil(@Param("nombre") String nombre,
                                        @Param("activo") Boolean activo);
    
    // Consulta SQL nativa con paginación
    @Query(value = "SELECT * FROM usuario WHERE edad BETWEEN :edadMin AND :edadMax",
           countQuery = "SELECT COUNT(*) FROM usuario WHERE edad BETWEEN :edadMin AND :edadMax",
           nativeQuery = true)
    Page<Usuario> findByEdadBetweenNativo(@Param("edadMin") Integer edadMin,
                                         @Param("edadMax") Integer edadMax,
                                         Pageable pageable);
    
    // Consulta SQL nativa de agregación
    @Query(value = "SELECT COUNT(*) FROM usuario WHERE activo = true", nativeQuery = true)
    Long contarUsuariosActivosNativo();
    
    // Consulta SQL nativa que retorna valores específicos
    @Query(value = "SELECT id, nombre, email FROM usuario WHERE activo = true", 
           nativeQuery = true)
    List<Object[]> findUsuariosActivosNativo();
    
    // Consulta SQL nativa de modificación
    @Modifying
    @Query(value = "UPDATE usuario SET activo = false WHERE email = :email", 
           nativeQuery = true)
    int desactivarUsuarioPorEmailNativo(@Param("email") String email);
    
    // Consulta SQL nativa con funciones de base de datos
    @Query(value = """
        SELECT * FROM usuario 
        WHERE DATE_PART('year', fecha_creacion) = :anio 
        AND activo = true
        ORDER BY fecha_creacion DESC
        """, nativeQuery = true)
    List<Usuario> findUsuariosPorAnio(@Param("anio") Integer anio);
    
    // Consulta SQL nativa con subconsulta
    @Query(value = """
        SELECT * FROM usuario u 
        WHERE u.edad > (
            SELECT AVG(edad) FROM usuario WHERE activo = true
        )
        """, nativeQuery = true)
    List<Usuario> findUsuariosConEdadSuperiorAPromedio();
}
```

---

## 3. Consultas con Specification

### Configuración del Repository

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, 
                                           JpaSpecificationExecutor<Usuario> {
    // Métodos de Spring Data JPA disponibles automáticamente
}
```

### Clase Specifications

```java
public class UsuarioSpecifications {
    
    public static Specification<Usuario> tieneNombre(String nombre) {
        return (root, query, criteriaBuilder) -> {
            if (nombre == null || nombre.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("nombre")), 
                "%" + nombre.toLowerCase() + "%"
            );
        };
    }
    
    public static Specification<Usuario> tieneEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("email"), email);
        };
    }
    
    public static Specification<Usuario> edadMayorQue(Integer edad) {
        return (root, query, criteriaBuilder) -> {
            if (edad == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThan(root.get("edad"), edad);
        };
    }
    
    public static Specification<Usuario> edadEntre(Integer edadMin, Integer edadMax) {
        return (root, query, criteriaBuilder) -> {
            if (edadMin == null && edadMax == null) {
                return criteriaBuilder.conjunction();
            }
            if (edadMin == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("edad"), edadMax);
            }
            if (edadMax == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("edad"), edadMin);
            }
            return criteriaBuilder.between(root.get("edad"), edadMin, edadMax);
        };
    }
    
    public static Specification<Usuario> estaActivo(Boolean activo) {
        return (root, query, criteriaBuilder) -> {
            if (activo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("activo"), activo);
        };
    }
    
    public static Specification<Usuario> creadoEntre(LocalDateTime inicio, LocalDateTime fin) {
        return (root, query, criteriaBuilder) -> {
            if (inicio == null && fin == null) {
                return criteriaBuilder.conjunction();
            }
            if (inicio == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("fechaCreacion"), fin);
            }
            if (fin == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("fechaCreacion"), inicio);
            }
            return criteriaBuilder.between(root.get("fechaCreacion"), inicio, fin);
        };
    }
    
    // Specification con JOIN
    public static Specification<Usuario> tienePerfil(String nombrePerfil) {
        return (root, query, criteriaBuilder) -> {
            if (nombrePerfil == null || nombrePerfil.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Usuario, Perfil> perfilJoin = root.join("perfil");
            return criteriaBuilder.equal(perfilJoin.get("nombre"), nombrePerfil);
        };
    }
}
```

### Uso en el Servicio

```java
@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public List<Usuario> buscarUsuarios(String nombre, String email, Integer edadMinima, Boolean activo) {
        Specification<Usuario> spec = Specification.where(null);
        
        if (nombre != null) {
            spec = spec.and(UsuarioSpecifications.tieneNombre(nombre));
        }
        
        if (email != null) {
            spec = spec.and(UsuarioSpecifications.tieneEmail(email));
        }
        
        if (edadMinima != null) {
            spec = spec.and(UsuarioSpecifications.edadMayorQue(edadMinima));
        }
        
        if (activo != null) {
            spec = spec.and(UsuarioSpecifications.estaActivo(activo));
        }
        
        return usuarioRepository.findAll(spec);
    }
    
    public Page<Usuario> buscarUsuariosPaginado(UsuarioFiltroDTO filtro, Pageable pageable) {
        Specification<Usuario> spec = Specification
            .where(UsuarioSpecifications.tieneNombre(filtro.getNombre()))
            .and(UsuarioSpecifications.edadEntre(filtro.getEdadMin(), filtro.getEdadMax()))
            .and(UsuarioSpecifications.estaActivo(filtro.getActivo()));
        
        return usuarioRepository.findAll(spec, pageable);
    }
    
    public List<Usuario> buscarConFiltroComplejo(String nombre, String perfil, LocalDateTime fechaInicio) {
        return usuarioRepository.findAll(
            UsuarioSpecifications.tieneNombre(nombre)
                .and(UsuarioSpecifications.tienePerfil(perfil))
                .and(UsuarioSpecifications.creadoEntre(fechaInicio, LocalDateTime.now()))
                .and(UsuarioSpecifications.estaActivo(true))
        );
    }
}
```

---

## 4. Repositorio personalizado con Criteria API

### Interfaz personalizada

```java
public interface UsuarioRepositoryCustom {
    List<Usuario> findUsuariosConCriteria(UsuarioFiltroDTO filtro);
    Page<Usuario> findUsuariosConCriteriaPaginado(UsuarioFiltroDTO filtro, Pageable pageable);
    List<UsuarioDTO> findUsuariosConProyeccion(UsuarioFiltroDTO filtro);
    List<Object[]> findEstadisticasPorEdad();
}
```

### Implementación

```java
@Repository
public class UsuarioRepositoryImpl implements UsuarioRepositoryCustom {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<Usuario> findUsuariosConCriteria(UsuarioFiltroDTO filtro) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Usuario> query = cb.createQuery(Usuario.class);
        Root<Usuario> root = query.from(Usuario.class);
        
        List<Predicate> predicates = construirPredicados(cb, root, filtro);
        
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.asc(root.get("nombre")));
        
        return entityManager.createQuery(query).getResultList();
    }
    
    @Override
    public Page<Usuario> findUsuariosConCriteriaPaginado(UsuarioFiltroDTO filtro, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        // Query para datos
        CriteriaQuery<Usuario> query = cb.createQuery(Usuario.class);
        Root<Usuario> root = query.from(Usuario.class);
        
        List<Predicate> predicates = construirPredicados(cb, root, filtro);
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        
        // Aplicar ordenación
        aplicarOrdenacion(cb, query, root, pageable);
        
        TypedQuery<Usuario> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        
        List<Usuario> usuarios = typedQuery.getResultList();
        
        // Query para contar
        Long total = contarUsuarios(filtro);
        
        return new PageImpl<>(usuarios, pageable, total);
    }
    
    @Override
    public List<UsuarioDTO> findUsuariosConProyeccion(UsuarioFiltroDTO filtro) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UsuarioDTO> query = cb.createQuery(UsuarioDTO.class);
        Root<Usuario> root = query.from(Usuario.class);
        
        // Proyección con constructor
        query.select(cb.construct(UsuarioDTO.class,
            root.get("id"),
            root.get("nombre"),
            root.get("email"),
            root.get("edad")
        ));
        
        List<Predicate> predicates = construirPredicados(cb, root, filtro);
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        
        return entityManager.createQuery(query).getResultList();
    }
    
    @Override
    public List<Object[]> findEstadisticasPorEdad() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Usuario> root = query.from(Usuario.class);
        
        // Agrupación y funciones de agregación
        query.multiselect(
            cb.function("FLOOR", Integer.class, cb.quot(root.get("edad"), 10)),
            cb.count(root),
            cb.avg(root.get("edad")),
            cb.min(root.get("edad")),
            cb.max(root.get("edad"))
        );
        
        query.groupBy(cb.function("FLOOR", Integer.class, cb.quot(root.get("edad"), 10)));
        query.orderBy(cb.asc(cb.function("FLOOR", Integer.class, cb.quot(root.get("edad"), 10))));
        
        return entityManager.createQuery(query).getResultList();
    }
    
    private List<Predicate> construirPredicados(CriteriaBuilder cb, Root<Usuario> root, UsuarioFiltroDTO filtro) {
        List<Predicate> predicates = new ArrayList<>();
        
        // Filtro por nombre
        if (filtro.getNombre() != null && !filtro.getNombre().isEmpty()) {
            predicates.add(cb.like(
                cb.lower(root.get("nombre")), 
                "%" + filtro.getNombre().toLowerCase() + "%"
            ));
        }
        
        // Filtro por email
        if (filtro.getEmail() != null && !filtro.getEmail().isEmpty()) {
            predicates.add(cb.equal(root.get("email"), filtro.getEmail()));
        }
        
        // Filtro por rango de edad
        if (filtro.getEdadMin() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("edad"), filtro.getEdadMin()));
        }
        
        if (filtro.getEdadMax() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("edad"), filtro.getEdadMax()));
        }
        
        // Filtro por activo
        if (filtro.getActivo() != null) {
            predicates.add(cb.equal(root.get("activo"), filtro.getActivo()));
        }
        
        // Filtro por fechas
        if (filtro.getFechaInicio() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("fechaCreacion"), filtro.getFechaInicio()));
        }
        
        if (filtro.getFechaFin() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("fechaCreacion"), filtro.getFechaFin()));
        }
        
        // Filtro con JOIN
        if (filtro.getNombrePerfil() != null && !filtro.getNombrePerfil().isEmpty()) {
            Join<Usuario, Perfil> perfilJoin = root.join("perfil", JoinType.LEFT);
            predicates.add(cb.equal(perfilJoin.get("nombre"), filtro.getNombrePerfil()));
        }
        
        return predicates;
    }
    
    private void aplicarOrdenacion(CriteriaBuilder cb, CriteriaQuery<Usuario> query, 
                                  Root<Usuario> root, Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            pageable.getSort().forEach(order -> {
                if (order.isAscending()) {
                    orders.add(cb.asc(root.get(order.getProperty())));
                } else {
                    orders.add(cb.desc(root.get(order.getProperty())));
                }
            });
            query.orderBy(orders);
        }
    }
    
    private Long contarUsuarios(UsuarioFiltroDTO filtro) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Usuario> countRoot = countQuery.from(Usuario.class);
        
        countQuery.select(cb.count(countRoot));
        
        List<Predicate> predicates = construirPredicados(cb, countRoot, filtro);
        countQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
```

### Repository principal

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, 
                                           UsuarioRepositoryCustom {
    // Métodos de Spring Data JPA + métodos personalizados
}
```

### DTOs de apoyo

```java
public class UsuarioFiltroDTO {
    private String nombre;
    private String email;
    private Integer edadMin;
    private Integer edadMax;
    private Boolean activo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String nombrePerfil;
    
    // constructores, getters y setters
}

public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private Integer edad;
    
    public UsuarioDTO(Long id, String nombre, String email, Integer edad) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.edad = edad;
    }
    
    // getters y setters
}
```

---

## 5. Comparación de enfoques

| Característica | @Query JPQL | @Query SQL Nativo | Specification | Criteria API |
|----------------|-------------|-------------------|---------------|--------------|
| **Facilidad de uso** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ |
| **Type Safety** | ⭐⭐ | ⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Flexibilidad** | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Consultas dinámicas** | ⭐ | ⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Portabilidad DB** | ⭐⭐⭐⭐⭐ | ⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Mantenibilidad** | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **Rendimiento** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |

---

## 6. Casos de uso recomendados

### Usar @Query JPQL cuando:
- Consultas simples a medianas
- Quieres mantener portabilidad entre bases de datos
- El equipo está familiarizado con JPA/Hibernate
- No necesitas consultas dinámicas complejas

### Usar @Query SQL Nativo cuando:
- Necesitas funciones específicas de la base de datos
- Consultas muy complejas con múltiples JOINs
- Optimizaciones específicas de rendimiento
- Consultas con subconsultas complejas

### Usar Specification cuando:
- Necesitas consultas dinámicas
- Quieres reutilizar lógica de filtrado
- Buscas type safety
- Trabajas con formularios 