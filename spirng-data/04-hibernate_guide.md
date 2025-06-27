# Guía de Hibernate: Objetos Principales y Session vs EntityManager

## Índice
1. [Introducción](#introducción)
2. [Principales Objetos de Hibernate](#principales-objetos-de-hibernate)
3. [Session vs EntityManager](#session-vs-entitymanager)
4. [Comparación Detallada](#comparación-detallada)
5. [Ejemplos Prácticos](#ejemplos-prácticos)
6. [Cuándo Usar Cada Uno](#cuándo-usar-cada-uno)

## Introducción

Hibernate es el framework ORM (Object-Relational Mapping) más popular en el ecosistema Java. Proporciona una capa de abstracción sobre JDBC y gestiona automáticamente la persistencia de objetos Java en bases de datos relacionales.

## Principales Objetos de Hibernate

### 1. Configuration
- **Propósito**: Configuración inicial de Hibernate
- **Función**: Lee archivos de configuración (hibernate.cfg.xml) y mapeos
- **Uso**: Se utiliza para crear SessionFactory

```java
Configuration configuration = new Configuration();
configuration.configure("hibernate.cfg.xml");
SessionFactory sessionFactory = configuration.buildSessionFactory();
```

### 2. SessionFactory
- **Propósito**: Fábrica de sesiones, patrón Singleton
- **Características**:
  - Thread-safe
  - Inmutable después de la construcción
  - Costoso de crear (se hace una vez por aplicación)
  - Mantiene metadatos de mapeo y configuración

```java
SessionFactory sessionFactory = new Configuration()
    .configure()
    .buildSessionFactory();
```

### 3. Session
- **Propósito**: Interfaz principal para interactuar con la base de datos
- **Características**:
  - NO es thread-safe
  - Representa una conexión lógica con la base de datos
  - Gestiona el ciclo de vida de las entidades
  - Mantiene caché de primer nivel

### 4. Transaction
- **Propósito**: Gestión de transacciones
- **Función**: Proporciona control transaccional sobre operaciones de base de datos

```java
Transaction transaction = session.beginTransaction();
// operaciones de base de datos
transaction.commit();
```

### 5. Query
- **Propósito**: Ejecución de consultas HQL, SQL nativo y Criteria
- **Tipos**:
  - HQL (Hibernate Query Language)
  - SQL nativo
  - Criteria API

### 6. Criteria
- **Propósito**: API programática para construir consultas
- **Ventaja**: Type-safe, menos propenso a errores

## Session vs EntityManager

### Session (Hibernate Nativo)
Session es la interfaz principal de Hibernate para interactuar con la base de datos. Es específica de Hibernate y ofrece todas las funcionalidades propias del framework.

**Características principales:**
- Interfaz nativa de Hibernate
- Acceso completo a todas las funcionalidades de Hibernate
- Gestión manual del ciclo de vida
- Mayor control sobre la configuración

### EntityManager (JPA)
EntityManager es la interfaz estándar de JPA (Java Persistence API) que Hibernate implementa. Proporciona un API estándar para la persistencia.

**Características principales:**
- Interfaz estándar de JPA
- Portabilidad entre diferentes proveedores JPA
- Gestión automática del ciclo de vida (con CDI/Spring)
- API más simplificada

## Comparación Detallada

| Aspecto | Session | EntityManager |
|---------|---------|---------------|
| **Estándar** | Específico de Hibernate | Estándar JPA |
| **Portabilidad** | Ligado a Hibernate | Portable entre proveedores JPA |
| **Funcionalidades** | Acceso completo a Hibernate | Funcionalidades estándar JPA |
| **Configuración** | hibernate.cfg.xml | persistence.xml |
| **Transacciones** | Manual con Transaction | Automática con @Transactional |
| **Consultas** | HQL, SQL, Criteria | JPQL, SQL, Criteria |
| **Caché** | Control directo | Configuración mediante anotaciones |
| **Lazy Loading** | Control granular | Configuración estándar |

### Métodos Principales

#### Session
```java
// Operaciones básicas
Object get(Class clazz, Serializable id);
Object load(Class clazz, Serializable id);
void save(Object object);
void update(Object object);
void saveOrUpdate(Object object);
void delete(Object object);

// Consultas
Query createQuery(String hql);
SQLQuery createSQLQuery(String sql);
Criteria createCriteria(Class persistentClass);

// Gestión de transacciones
Transaction beginTransaction();
void flush();
void clear();
void close();
```

#### EntityManager
```java
// Operaciones básicas
<T> T find(Class<T> entityClass, Object primaryKey);
<T> T getReference(Class<T> entityClass, Object primaryKey);
void persist(Object entity);
<T> T merge(T entity);
void remove(Object entity);

// Consultas
Query createQuery(String jpql);
Query createNativeQuery(String sql);
TypedQuery<T> createQuery(String jpql, Class<T> resultClass);

// Gestión de transacciones
EntityTransaction getTransaction();
void flush();
void clear();
void close();
```

## Ejemplos Prácticos

### Ejemplo con Session
```java
// Configuración
SessionFactory sessionFactory = new Configuration()
    .configure()
    .buildSessionFactory();

// Uso
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();

try {
    // Crear entidad
    Usuario usuario = new Usuario("Juan", "juan@email.com");
    session.save(usuario);
    
    // Consultar
    Usuario encontrado = (Usuario) session.get(Usuario.class, 1L);
    
    // Actualizar
    encontrado.setNombre("Juan Carlos");
    session.update(encontrado);
    
    tx.commit();
} catch (Exception e) {
    tx.rollback();
    throw e;
} finally {
    session.close();
}
```

### Ejemplo con EntityManager
```java
// Configuración (persistence.xml)
EntityManagerFactory emf = Persistence.createEntityManagerFactory("miPU");
EntityManager em = emf.createEntityManager();

try {
    em.getTransaction().begin();
    
    // Crear entidad
    Usuario usuario = new Usuario("Juan", "juan@email.com");
    em.persist(usuario);
    
    // Consultar
    Usuario encontrado = em.find(Usuario.class, 1L);
    
    // Actualizar
    encontrado.setNombre("Juan Carlos");
    em.merge(encontrado);
    
    em.getTransaction().commit();
} catch (Exception e) {
    em.getTransaction().rollback();
    throw e;
} finally {
    em.close();
}
```

### Ejemplo con Spring Boot (EntityManager)
```java
@Service
@Transactional
public class UsuarioService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public Usuario guardar(Usuario usuario) {
        return entityManager.merge(usuario);
    }
    
    public Usuario buscarPorId(Long id) {
        return entityManager.find(Usuario.class, id);
    }
    
    public List<Usuario> buscarTodos() {
        return entityManager.createQuery(
            "SELECT u FROM Usuario u", Usuario.class)
            .getResultList();
    }
}
```

## Cuándo Usar Cada Uno

### Usar Session cuando:
- Necesitas funcionalidades específicas de Hibernate
- Requieres control granular sobre el caché
- Trabajas con consultas HQL complejas
- El proyecto está completamente ligado a Hibernate
- Necesitas funcionalidades avanzadas como interceptores

### Usar EntityManager cuando:
- Buscas portabilidad entre proveedores JPA
- Trabajas con Spring Boot o CDI
- Prefieres un API estándar y simplificado
- El equipo está más familiarizado con JPA
- Planeas migrar a otro proveedor JPA en el futuro

## Configuración

### hibernate.cfg.xml (Session)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
    "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
    "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/mibd</property>
        <property name="hibernate.connection.username">usuario</property>
        <property name="hibernate.connection.password">password</property>
        <property name="hibernate.hbm2ddl.auto">update</property>
        <property name="hibernate.show_sql">true</property>
        
        <mapping class="com.ejemplo.modelo.Usuario"/>
    </session-factory>
</hibernate-configuration>
```

### persistence.xml (EntityManager)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence
             http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
             version="2.0">
    
    <persistence-unit name="miPU">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <class>com.ejemplo.modelo.Usuario</class>
        <properties>
            <property name="javax.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/mibd"/>
            <property name="javax.persistence.jdbc.user" value="usuario"/>
            <property name="javax.persistence.jdbc.password" value="password"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect"/>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <property name="hibernate.show_sql" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

## Conclusión

La elección entre Session y EntityManager depende principalmente de tus necesidades específicas:

- **Session** ofrece mayor control y acceso a funcionalidades específicas de Hibernate
- **EntityManager** proporciona portabilidad y simplicidad mediante el estándar JPA

En aplicaciones modernas con Spring Boot, EntityManager suele ser la opción preferida debido a su integración automática y gestión declarativa de transacciones. Sin embargo, Session sigue siendo válido cuando necesitas funcionalidades avanzadas específicas de Hibernate.

---

*Documento generado para referencia técnica de Hibernate*