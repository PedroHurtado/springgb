# Guía Completa de Anotaciones JPA

## Anotaciones de Entidad

### @Entity
**Utilidad:** Marca una clase como entidad JPA que será mapeada a una tabla de base de datos.
```java
@Entity
public class Usuario {
    // campos y métodos
}
```

### @Table
**Utilidad:** Especifica detalles de la tabla de base de datos asociada con la entidad.
```java
@Entity
@Table(name = "usuarios", schema = "public")
public class Usuario {
    // campos y métodos
}
```

### @Id
**Utilidad:** Marca un campo como clave primaria de la entidad.
```java
@Id
private Long id;
```

### @GeneratedValue
**Utilidad:** Especifica la estrategia de generación automática de valores para la clave primaria.
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

## Anotaciones de Mapeo de Columnas

### @Column
**Utilidad:** Mapea un campo de la entidad a una columna específica de la tabla.
```java
@Column(name = "nombre_usuario", length = 50, nullable = false, unique = true)
private String nombre;
```

### @Basic
**Utilidad:** Especifica el mapeo básico para un campo persistente (es opcional, se aplica por defecto).
```java
@Basic(fetch = FetchType.LAZY)
private String descripcion;
```

### @Transient
**Utilidad:** Excluye un campo del mapeo de persistencia.
```java
@Transient
private String campoTemporary;
```

### @Temporal
**Utilidad:** Especifica el tipo de fecha/hora para campos de tipo Date o Calendar.
```java
@Temporal(TemporalType.TIMESTAMP)
private Date fechaCreacion;
```

### @Lob
**Utilidad:** Indica que el campo debe ser persistido como un Large Object (BLOB o CLOB).
```java
@Lob
private byte[] imagen;

@Lob
private String textoLargo;
```

## Anotaciones de Relaciones

### @OneToOne
**Utilidad:** Define una relación uno a uno entre entidades.
```java
@OneToOne(cascade = CascadeType.ALL)
@JoinColumn(name = "perfil_id")
private Perfil perfil;
```

### @OneToMany
**Utilidad:** Define una relación uno a muchos.
```java
@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private Set<Pedido> pedidos = new HashSet<>();
```

### @ManyToOne
**Utilidad:** Define una relación muchos a uno.
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "categoria_id")
private Categoria categoria;
```

### @ManyToMany
**Utilidad:** Define una relación muchos a muchos.
```java
@ManyToMany
@JoinTable(
    name = "usuario_rol",
    joinColumns = @JoinColumn(name = "usuario_id"),
    inverseJoinColumns = @JoinColumn(name = "rol_id")
)
private Set<Rol> roles = new HashSet<>();
```

### @JoinColumn
**Utilidad:** Especifica la columna de clave foránea para las relaciones.
```java
@ManyToOne
@JoinColumn(name = "categoria_id", nullable = false)
private Categoria categoria;
```

### @JoinTable
**Utilidad:** Define la tabla intermedia para relaciones ManyToMany.
```java
@ManyToMany
@JoinTable(
    name = "producto_categoria",
    joinColumns = @JoinColumn(name = "producto_id"),
    inverseJoinColumns = @JoinColumn(name = "categoria_id")
)
private Set<Categoria> categorias;
```

## Anotaciones de Consultas

### @NamedQuery
**Utilidad:** Define consultas JPQL con nombre que pueden ser reutilizadas.
```java
@Entity
@NamedQuery(
    name = "Usuario.findByEmail",
    query = "SELECT u FROM Usuario u WHERE u.email = :email"
)
public class Usuario {
    // campos y métodos
}
```

### @NamedQueries
**Utilidad:** Agrupa múltiples consultas con nombre.
```java
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findByActivo", query = "SELECT u FROM Usuario u WHERE u.activo = true")
})
```

### @NamedNativeQuery
**Utilidad:** Define consultas SQL nativas con nombre.
```java
@NamedNativeQuery(
    name = "Usuario.findByEmailNative",
    query = "SELECT * FROM usuarios WHERE email = ?",
    resultClass = Usuario.class
)
```

## Anotaciones de Ciclo de Vida

### @PrePersist
**Utilidad:** Método ejecutado antes de persistir la entidad.
```java
@PrePersist
public void prePersist() {
    this.fechaCreacion = new Date();
}
```

### @PostPersist
**Utilidad:** Método ejecutado después de persistir la entidad.
```java
@PostPersist
public void postPersist() {
    System.out.println("Entidad persistida con ID: " + this.id);
}
```

### @PreUpdate
**Utilidad:** Método ejecutado antes de actualizar la entidad.
```java
@PreUpdate
public void preUpdate() {
    this.fechaActualizacion = new Date();
}
```

### @PostUpdate
**Utilidad:** Método ejecutado después de actualizar la entidad.
```java
@PostUpdate
public void postUpdate() {
    System.out.println("Entidad actualizada");
}
```

### @PreRemove
**Utilidad:** Método ejecutado antes de eliminar la entidad.
```java
@PreRemove
public void preRemove() {
    System.out.println("Preparando eliminación de entidad");
}
```

### @PostRemove
**Utilidad:** Método ejecutado después de eliminar la entidad.
```java
@PostRemove
public void postRemove() {
    System.out.println("Entidad eliminada");
}
```

### @PostLoad
**Utilidad:** Método ejecutado después de cargar la entidad desde la base de datos.
```java
@PostLoad
public void postLoad() {
    System.out.println("Entidad cargada desde BD");
}
```

## Anotaciones de Herencia

### @Inheritance
**Utilidad:** Especifica la estrategia de herencia para una jerarquía de entidades.
```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Persona {
    // campos comunes
}
```

### @DiscriminatorColumn
**Utilidad:** Define la columna discriminadora para estrategias de herencia SINGLE_TABLE.
```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_persona", discriminatorType = DiscriminatorType.STRING)
public abstract class Persona {
    // campos comunes
}
```

### @DiscriminatorValue
**Utilidad:** Especifica el valor discriminador para una entidad específica.
```java
@Entity
@DiscriminatorValue("EMPLEADO")
public class Empleado extends Persona {
    // campos específicos de empleado
}
```

## Anotaciones de Mapeo Avanzado

### @Embedded
**Utilidad:** Indica que un objeto embebido debe ser incluido en la entidad.
```java
@Embedded
private Direccion direccion;
```

### @Embeddable
**Utilidad:** Marca una clase como embebible en otras entidades.
```java
@Embeddable
public class Direccion {
    private String calle;
    private String ciudad;
    private String codigoPostal;
}
```

### @AttributeOverride
**Utilidad:** Sobrescribe el mapeo de un atributo embebido.
```java
@Embedded
@AttributeOverride(name = "calle", column = @Column(name = "direccion_calle"))
private Direccion direccion;
```

### @AssociationOverride
**Utilidad:** Sobrescribe el mapeo de una asociación en un objeto embebido.
```java
@Embedded
@AssociationOverride(name = "pais", joinColumns = @JoinColumn(name = "pais_id"))
private Direccion direccion;
```

## Anotaciones de Validación y Constraints

### @Version
**Utilidad:** Implementa control de concurrencia optimista mediante versionado.
```java
@Version
private Long version;
```

### @Enumerated
**Utilidad:** Especifica cómo persistir un campo enum.
```java
@Enumerated(EnumType.STRING)
private EstadoUsuario estado;
```

## Anotaciones de Cache

### @Cacheable
**Utilidad:** Indica si la entidad puede ser almacenada en caché de segundo nivel.
```java
@Entity
@Cacheable(true)
public class Producto {
    // campos y métodos
}
```

## Anotaciones de Mapeo Secundario

### @SecondaryTable
**Utilidad:** Mapea una entidad a múltiples tablas.
```java
@Entity
@SecondaryTable(name = "detalles_usuario", pkJoinColumns = @PrimaryKeyJoinColumn(name = "usuario_id"))
public class Usuario {
    @Column(table = "detalles_usuario")
    private String biografia;
}
```

## Estrategias de Generación de ID

### GenerationType.AUTO
Deja que el proveedor de persistencia elija la estrategia más apropiada.

### GenerationType.IDENTITY
Utiliza columnas auto-incrementales de la base de datos.

### GenerationType.SEQUENCE
Utiliza secuencias de base de datos.

### GenerationType.TABLE
Utiliza una tabla específica para generar valores únicos.

## Tipos de Fetch

### FetchType.EAGER
Carga los datos asociados inmediatamente junto con la entidad principal.

### FetchType.LAZY
Carga los datos asociados solo cuando se accede a ellos por primera vez.

## Tipos de Cascade

### CascadeType.PERSIST
Propaga la operación de persistencia.

### CascadeType.MERGE
Propaga la operación de merge.

### CascadeType.REMOVE
Propaga la operación de eliminación.

### CascadeType.REFRESH
Propaga la operación de refresh.

### CascadeType.DETACH
Propaga la operación de detach.

### CascadeType.ALL
Propaga todas las operaciones anteriores.

---

## Ejemplo Completo de Entidad

```java
@Entity
@Table(name = "productos")
@NamedQuery(name = "Producto.findByCategoria", 
           query = "SELECT p FROM Producto p WHERE p.categoria.nombre = :categoria")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;
    
    @Column(name = "precio", precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Lob
    private String descripcion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ItemPedido> items = new HashSet<>();
    
    @Embedded
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "fecha_creacion"))
    private Auditoria auditoria;
    
    @Version
    private Long version;
    
    @PrePersist
    public void prePersist() {
        if (auditoria == null) {
            auditoria = new Auditoria();
        }
        auditoria.setFechaCreacion(new Date());
    }
    
    @PreUpdate
    public void preUpdate() {
        if (auditoria != null) {
            auditoria.setFechaActualizacion(new Date());
        }
    }
    
    // Getters y setters
}
```

Esta guía cubre las anotaciones JPA más importantes y su uso práctico en el desarrollo de aplicaciones Java con persistencia de datos.