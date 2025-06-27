
# ⚖️ Comparación: JPA Estándar vs Spring Data JPA

## 🧩 ¿Qué es JPA?

**JPA (Java Persistence API)** es una especificación de Java para la gestión de datos entre objetos Java y bases de datos relacionales. Define interfaces y anotaciones estándar.

Ejemplos de implementaciones:
- Hibernate (más común)
- EclipseLink
- OpenJPA

**JPA no incluye la derivación de consultas por nombre de método.**

---

## 🚀 ¿Qué es Spring Data JPA?

**Spring Data JPA** es una extensión de Spring que facilita el uso de JPA. Añade funcionalidades avanzadas como:

- Repositorios automáticos (`JpaRepository`, `CrudRepository`)
- Derivación de consultas por nombre de método (`findByNombreAndEdad()`)
- Consultas con `@Query`
- Paginación y ordenación automáticas
- Proyecciones dinámicas
- Soporte opcional para QueryDSL, Specifications, etc.

---

## 🟡 Comparación rápida

| Característica                             | JPA estándar | Spring Data JPA |
|-------------------------------------------|--------------|------------------|
| Anotaciones `@Entity`, `@Id`, `@Column`   | ✅           | ✅               |
| CRUD manual con `EntityManager`           | ✅           | ✅               |
| Repositorios automáticos (`JpaRepository`) | ❌           | ✅               |
| Derivación por nombre (`findBy...`)       | ❌           | ✅               |
| Consultas `@Query` con JPQL                | ❌           | ✅               |
| Soporte para paginación (`Pageable`)       | ❌           | ✅               |
| Orden automático (`Sort`)                  | ❌           | ✅               |
| Proyecciones (`interface-based projections`)| ❌          | ✅               |
| Integración con Spring Boot                | ❌           | ✅               |

---

## ✅ Conclusión

- **JPA** es la especificación base y requiere más código manual.
- **Spring Data JPA** construye sobre JPA para acelerar el desarrollo, simplificando consultas y CRUD.
- Las consultas derivadas del nombre del método (`findByX`) **solo están disponibles en Spring Data JPA**.

---
