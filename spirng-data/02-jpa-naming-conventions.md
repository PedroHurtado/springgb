
# 📘 Métodos de `JpaRepository` basados en Naming Conventions (Spring Data JPA)

Spring Data JPA permite crear consultas automáticamente basadas en el **nombre del método**, una funcionalidad conocida como **naming conventions** o **query derivation**.

---

## 🧱 Estructura general del método

```java
<Acción><By><Propiedad><Operador>...<Condiciones>
```

- `Acción`: `find`, `read`, `get`, `query`, `count`, `exists`, `delete`, etc.
- `By`: indica el inicio de las condiciones.
- `Propiedad`: nombre del campo en la entidad (debe coincidir con el nombre exacto o el nombre del getter).
- `Operador`: `And`, `Or`, `Between`, `GreaterThan`, `Like`, etc.

---

## 📌 Ejemplos comunes

Supón que tienes esta entidad:

```java
@Entity
public class Usuario {
    @Id
    private Long id;
    private String nombre;
    private String email;
    private int edad;
    private LocalDate fechaRegistro;
}
```

Y este repositorio:

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
```

### 🔎 Búsquedas simples

```java
List<Usuario> findByNombre(String nombre);
Usuario findByEmail(String email);
```

### 🔗 Combinaciones

```java
List<Usuario> findByNombreAndEdad(String nombre, int edad);
List<Usuario> findByNombreOrEmail(String nombre, String email);
```

### 🔢 Operadores de comparación

```java
List<Usuario> findByEdadGreaterThan(int edad);
List<Usuario> findByEdadLessThanEqual(int edad);
List<Usuario> findByFechaRegistroBetween(LocalDate inicio, LocalDate fin);
```

### 🔍 Texto y coincidencias

```java
List<Usuario> findByNombreLike(String patron);
List<Usuario> findByNombreContaining(String substring);
List<Usuario> findByNombreStartingWith(String prefijo);
List<Usuario> findByNombreEndingWith(String sufijo);
```

### ✅ Existencia y conteo

```java
boolean existsByEmail(String email);
long countByEdadGreaterThan(int edad);
```

### ❌ Eliminación

```java
void deleteByEmail(String email);
long deleteByEdadLessThan(int edad);
```

### 🔁 Ordenación

```java
List<Usuario> findByEdadGreaterThanOrderByNombreAsc(int edad);
```

También puedes usar `Sort` y `Pageable`:

```java
List<Usuario> findByEdadGreaterThan(int edad, Sort sort);
Page<Usuario> findByNombreContaining(String nombre, Pageable pageable);
```

---

## 📘 Operadores soportados

| Operador en el método                  | Traducción SQL |
|----------------------------------------|----------------|
| `And`, `Or`                            | `AND`, `OR`    |
| `Between`, `LessThan`, `GreaterThan`   | Comparaciones  |
| `IsNull`, `IsNotNull`                 | Nulos          |
| `Like`, `NotLike`, `StartingWith`      | `LIKE`         |
| `In`, `NotIn`                          | `IN`, `NOT IN` |
| `True`, `False`                        | Booleanos      |
| `IgnoreCase`                           | `LOWER()`      |

---

## ⚠️ Consideraciones

- Los nombres de los métodos deben **coincidir con los atributos del modelo**, respetando mayúsculas/minúsculas.
- Si hay ambigüedad o nombres complejos, es mejor usar `@Query` con JPQL o SQL nativo.
- Puedes extender con consultas personalizadas usando `@Query` si lo necesitas.

---
