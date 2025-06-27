# Estados de una Entidad JPA

En JPA (Java Persistence API), una entidad puede estar en uno de los siguientes **cuatro estados**:

---

## 🟡 1. Nuevo / Transient

- La entidad ha sido creada con `new`, pero **no está asociada** a ningún `EntityManager`.
- No existe en la base de datos.
- No será persistida a menos que se use `persist()`.

```java
User user = new User(); // Estado Transient
user.setName("Pedro");
```

---

## 🔵 2. Administrado / Managed / Persistente

- La entidad **está asociada al contexto de persistencia**.
- Cualquier cambio se sincronizará automáticamente con la base de datos al hacer `flush()` o `commit()`.

```java
em.persist(user); // Ahora está en estado Managed
```

---

## 🔴 3. Separado / Detached

- La entidad **ya no está en el contexto de persistencia**.
- Esto puede ocurrir si:
  - Se cierra el `EntityManager`.
  - Se llama a `em.detach()`.
  - Se usa `clear()` o `evict()`.
- Sus cambios **no se sincronizan automáticamente** con la base de datos.

```java
em.detach(user); // Estado Detached
user.setName("Pedro modificado"); // No se guarda automáticamente
```

---

## ⚫ 4. Eliminado / Removed

- La entidad está marcada para eliminación.
- Todavía es `Managed`, pero será eliminada al hacer `flush()` o `commit()`.

```java
em.remove(user); // Estado Removed
```

---

## ⏱ Transiciones comunes

```plaintext
NEW -> persist() -> MANAGED
MANAGED -> remove() -> REMOVED
MANAGED -> detach() -> DETACHED
DETACHED -> merge() -> MANAGED
```
