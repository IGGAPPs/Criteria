# Criteria

Lightweight criteria library for filtering, sorting, and pagination in Spring Boot applications.

## Installation

```xml
<dependency>
    <groupId>es.iggapps</groupId>
    <artifactId>criteria</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## Quick Start

### 1. Create your CriteriaFactory

```java
@Component
public class PedidoCriteriaFactory extends CriteriaFactory {

    @Override
    protected Map<String, FilterConfig> configFilterWhiteList() {
        return Map.of(
            "nombre", new FilterConfig(Type.STRING, Set.of(Operator.EQ, Operator.CONTAINS_ALL)),
            "precio", new FilterConfig(Type.INTEGER, Set.of(Operator.GTE, Operator.LTE)),
            "fecha", new FilterConfig(Type.DATE, Set.of(Operator.GTE, Operator.LTE))
        );
    }

    @Override
    protected Set<String> configSortWhiteList() {
        return Set.of("nombre", "precio", "fecha");
    }

    @Override
    protected List<PlainSort> configDefaultSort() {
        return List.of(PlainSort.of("fecha", "DESC"));
    }
}
```

### 2. Use in your controller

```java
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoCriteriaFactory criteriaFactory;

    @GetMapping
    public ResponseEntity<?> find(
            @RequestParam Optional<String> filters,
            @RequestParam Optional<String> sorts,
            @RequestParam Optional<Integer> pageNumber,
            @RequestParam Optional<Integer> pageSize) {

        Criteria criteria = criteriaFactory.make(filters, sorts, pageNumber, pageSize);
        // Use criteria with your repository
    }
}
```

### 3. Make requests

```
GET /pedidos?filters=nombre:EQ:Laptop
GET /pedidos?filters=precio:GTE:100,precio:LTE:500
GET /pedidos?filters=fecha:GTE:2024-01-01,fecha:LTE:2024-12-31
GET /pedidos?sorts=precio:ASC,nombre:DESC
GET /pedidos?pageNumber=0&pageSize=10
```

## Filter Format

```
field:operator:value
```

Multiple filters are separated by commas:

```
field1:operator1:value1,field2:operator2:value2
```

## Operators

| Operator | Description | Types | Example |
|----------|-------------|-------|---------|
| `EQ` | Equals | All | `nombre:EQ:Laptop` |
| `NEQ` | Not equals | All | `nombre:NEQ:Laptop` |
| `GT` | Greater than | Numeric, Date | `precio:GT:100` |
| `GTE` | Greater than or equal | Numeric, Date | `precio:GTE:100` |
| `LT` | Less than | Numeric, Date | `precio:LT:500` |
| `LTE` | Less than or equal | Numeric, Date | `precio:LTE:500` |
| `CONTAINS` | String contains | String | `nombre:CONTAINS:laptop` |
| `STARTS_WITH` | String starts with | String | `nombre:STARTS_WITH:lap` |
| `ENDS_WITH` | String ends with | String | `nombre:ENDS_WITH:top` |
| `CONTAINS_ALL` | List contains all values | List | `tags:CONTAINS_ALL:[java,spring]` |
| `CONTAINS_ANY` | List contains any value | List | `tags:CONTAINS_ANY:[java,kotlin]` |

### Custom Operators

`Operator` is extensible. Add your own operators for infrastructure concerns (e.g., fuzzy search):

```java
Operator FZ = Operator.of("FZ");

FilterConfig config = new FilterConfig(
    Type.STRING, Set.of(Operator.EQ, FZ)
);
```

## Types

| Type | Java Type | Example Value |
|------|-----------|---------------|
| `STRING` | `String` | `Laptop` |
| `INTEGER` | `Integer` | `42` |
| `LONG` | `Long` | `9999999999` |
| `FLOAT` | `Float` | `3.14` |
| `DOUBLE` | `Double` | `3.14` |
| `BIG_DECIMAL` | `BigDecimal` | `123.456` |
| `BOOLEAN` | `Boolean` | `true` |
| `UUID` | `UUID` | `550e8400-e29b-41d4-a716-446655440000` |
| `DATE` | `LocalDate` | `2024-01-15` |
| `DATE_TIME` | `OffsetDateTime` | `2024-01-15T10:30:00+01:00` |

## List Filters

Use brackets for list values:

```
tags:CONTAINS_ALL:[java,spring,boot]
```

## Custom Types with ValueParser

Transform strings into your own value objects:

```java
public record Email(String value) {
    public Email {
        if (value == null || !value.contains("@")) {
            throw new IllegalArgumentException("Debe contener un '@'");
        }
    }
}

// In your CriteriaFactory
@Override
protected Map<String, FilterConfig> configFilterWhiteList() {
    return Map.of(
        "email", new FilterConfig(Type.STRING, Set.of(Operator.EQ),
            ValueParser.of(Email::new))
    );
}

// In your service
Filter<Email> filter = criteria.getFilterOrElseThrow("email");
Email email = filter.withOperatorOrElseThrow(Operator.EQ);
```

## Pattern Validation

Validate string format with regex:

```java
@Override
protected Map<String, FilterConfig> configFilterWhiteList() {
    return Map.of(
        "fecha", FilterConfig.withPattern(
            Type.STRING, Set.of(Operator.EQ), "\\d{2}-\\d{2}-\\d{4}")
    );
}
```

## Sort Format

```
field:order
```

Orders: `ASC` or `DESC`

Multiple sorts: `field1:ASC,field2:DESC`

## Criteria API

```java
Criteria criteria = criteriaFactory.make(filters, sorts, pageNumber, pageSize);

// Get a filter
Optional<Filter<String>> filter = criteria.findFilterBy("nombre");
Filter<String> filterOrThrow = criteria.getFilterOrElseThrow("nombre");

// Get value
String value = filterOrThrow.withOperatorOrElseThrow(Operator.EQ);

// Check existence
boolean exists = criteria.existsFilterBy("nombre");

// Pagination
int page = criteria.getPageNumber().getValue();
int size = criteria.getPageSize().getValue();
```

## Exceptions

| Exception | HTTP | Cause |
|-----------|------|-------|
| `CriteriaValidationException` | 400 | Invalid client input |
| `CriteriaException` | 500 | Internal programming error |

## License

MIT
