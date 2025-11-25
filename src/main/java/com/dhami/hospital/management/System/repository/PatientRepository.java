package com.dhami.hospital.management.System.repository;

import com.dhami.hospital.management.System.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}

/*
---JPQL==>Java Persistence Query Language
an Object-oriented query language (similar to SQL but for entities)
Works with entities and their properties, not database tables
Database-agnostic - same JPQL works with MySQL, PostgreSQL, etc.

ex.
1. Basic SELECT Queries
 Method name query (Spring Data)
    List<User> findByName(String name);

    Equivalent JPQL
    @Query("SELECT u FROM User u WHERE u.name = ?1")
    List<User> findByNameJPQL(String name);
    // Select specific fields
    @Query("SELECT u.name, u.email FROM User u WHERE u.active = true")
    List<Object[]> findActiveUsersProjection();

      @Query("SELECT u FROM User u WHERE u.age > 18 AND u.active = true")
    List<User> findActiveAdultUsers();

    @Query("SELECT u FROM User u WHERE u.name LIKE '%admin%' OR u.email LIKE '%admin%'")
    List<User> findAdminUsers();

    @Query("SELECT u FROM User u WHERE u.age BETWEEN 18 AND 65")
    List<User> findWorkingAgeUsers();

    2. JOIN Operations
    @Query("SELECT o FROM Order o JOIN o.user u WHERE u.name = :userName")
    List<Order> findOrdersByUserName(@Param("userName") String userName);

    @Query("SELECT u FROM User u LEFT JOIN u.orders o WHERE o IS NULL")
    List<User> findUsersWithNoOrders();

    FETCH JOIN (Eager Loading)----
    public interface UserRepository extends JpaRepository<User, Long> {

    // Solves N+1 problem - loads orders in same query
    @Query("SELECT u FROM User u JOIN FETCH u.orders WHERE u.id = :userId")
    Optional<User> findUserWithOrders(@Param("userId") Long userId);

    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.orders o WHERE o.amount > 50")
    List<User> findUsersWithLargeOrders();
}

3. Aggregate Functions
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o.user.name, COUNT(o), SUM(o.amount), AVG(o.amount) " +
           "FROM Order o GROUP BY o.user.name")
    List<Object[]> getOrderStatisticsByUser();

    @Query("SELECT u.age, COUNT(u) FROM User u GROUP BY u.age HAVING COUNT(u) > 5")
    List<Object[]> findPopularAges();
}

4. UPDATE and DELETE Queries
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query("UPDATE User u SET u.active = false WHERE u.lastLogin < :date")
    int deactivateInactiveUsers(@Param("date") LocalDateTime date);

    @Modifying
    @Query("UPDATE User u SET u.email = CONCAT(u.name, '@company.com') WHERE u.email IS NULL")
    int setDefaultEmail();

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.age = u.age + 1 WHERE u.birthday < CURRENT_DATE")
    int incrementAgeForBirthday();

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.active = false AND u.createdDate < :date")
    int deleteInactiveUsers(@Param("date") LocalDateTime date);
}

5. Parameter Binding
@Query("SELECT u FROM User u WHERE u.name = ?1 AND u.age > ?2")
    List<User> findByNameAndMinAge(String name, Integer minAge);
but Named Parameters (Recommended)
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.name = :name AND u.age BETWEEN :minAge AND :maxAge")
    List<User> findByNameAndAgeRange(@Param("name") String name,
                                   @Param("minAge") Integer minAge,
                                   @Param("maxAge") Integer maxAge);

    @Query("SELECT u FROM User u WHERE u.email LIKE CONCAT('%', :domain)")
    List<User> findByEmailDomain(@Param("domain") String domain);
}

 */

/*
Native Query Basics

Raw SQL queries that execute directly on the database
@Query(value = "SELECT * FROM users WHERE name = :name AND age > :minAge",
           nativeQuery = true)
    List<User> findByNameAndMinAgeNative(@Param("name") String name,
                                       @Param("minAge") Integer minAge);
 */


/*

What is Pagination?

Simple Analogy: Restaurant Menu
Full menu = All data from database (1000 items)
Pagination = Showing only one page at a time (10 items per page)
Page navigation = "Next page", "Previous page" buttons

Why Use Pagination?
Without Pagination:
// BAD: Loads ALL users at once
List<User> allUsers = userRepository.findAll(); // 10,000 users!
// 🚫 Slow performance
// 🚫 High memory usage
// 🚫 Poor user experience

With Pagination:
// GOOD: Loads only one page
Page<User> users = userRepository.findAll(PageRequest.of(0, 10));
// ✅ Fast performance
// ✅ Low memory usage
// ✅ Better user experience

Core Pagination Components
1. Pageable - "Page Request"

// Creates a request for page 0 (first page) with 10 items per page
Pageable pageable = PageRequest.of(0, 10);

// Page 2 with 20 items, sorted by name
Pageable pageable = PageRequest.of(2, 20, Sort.by("name"));

2. Page - "Page Response"
Page<User> page = userRepository.findAll(pageable);

page.getContent();        // List<User> - actual data (10 users)
page.getNumber();         // 0 - current page number
page.getSize();           // 10 - page size
page.getTotalElements();  // 150 - total users in database
page.getTotalPages();     // 15 - total pages (150/10)
page.hasNext();           // true - has next page
page.hasPrevious();       // false - no previous page (first page)

Implementation---->

1. Repository Level
// Built-in pagination - already available!
    Page<User> findAll(Pageable pageable);

     // Custom query with pagination
    Page<User> findByActiveTrue(Pageable pageable);

     // Native query with pagination
    @Query(value = "SELECT * FROM users WHERE age > 18",
           countQuery = "SELECT COUNT(*) FROM users WHERE age > 18",
           nativeQuery = true)
    Page<User> findAdults(Pageable pageable);

   2. Service Level
     public Page<User> getUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository.findAll(pageable);
    }
     public Page<User> getActiveUsers(int pageNumber, int pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return userRepository.findByActiveTrue(pageable);
    }

    3. Controller Level
    // Simple pagination
    @GetMapping("/users")
    public Page<User> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return userService.getUsers(page, size);
    }

    // Pagination with sorting
    @GetMapping("/users/sorted")
    public Page<User> getUsersSorted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {

        return userService.getActiveUsers(page, size, sortBy);
    }

    Custom Pagination Examples---->

    1. Search with Pagination
    public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

// Usage
Page<User> searchResults = userRepository
    .findByNameContainingIgnoreCase("john", PageRequest.of(0, 10));


2. Pagination with Filters

@Service
public class UserService {

    public Page<User> searchUsers(UserSearchFilter filter, Pageable pageable) {
        return userRepository.findByFilters(
            filter.getName(),
            filter.getMinAge(),
            filter.getMaxAge(),
            filter.isActive(),
            pageable
        );
    }
}

3. DTO Projection with Pagination
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT new com.example.UserDTO(u.id, u.name, u.email) FROM User u")
    Page<UserDTO> findAllUserDTOs(Pageable pageable);
}

Best Practices

1. Set Reasonable Page Sizes
// Good
PageRequest.of(0, 20);  // 20 items per page

// Bad
PageRequest.of(0, 1000); // Too many items!

2. Use Sorting Wisely

// Sort by indexed columns for better performance
PageRequest.of(0, 10, Sort.by("id"));  // Good
PageRequest.of(0, 10, Sort.by("createdDate"));  // Good if indexed

// Avoid sorting by unindexed columns in large datasets
PageRequest.of(0, 10, Sort.by("description"));  // Potentially slow

3. Choose Between Page and Slice

// Use Page when you need total count (for page numbers)
Page<User> page = repository.findAll(pageable); // Shows "Page 1 of 15"

// Use Slice when you only need "Load More" functionality
Slice<User> slice = repository.findAll(pageable); // Only shows "Load More" button

Key Benefits Summary:
Performance - Loads only needed data
Scalability - Works with large datasets
User Experience - Fast loading, easy navigation
Memory Efficient - Doesn't load everything at once
Standardized - Consistent API across application
Pagination is essential for any application dealing with large amounts of data - it makes your app faster and more user-friendly!
 */



/*
hibernate-entity lifecycle-> "Transient → Persistent → Detached → Removed"
1. Transient State - "Newborn Entity"
What it is:
A brand new object created with new keyword
Not associated with any database row
Hibernate doesn't know about it
ex->
// Transient state - just a regular Java object
User user = new User();
user.setName("John");
user.setEmail("john@email.com");

// At this point:
// - No ID assigned (unless you set manually)
// - Not in database
// - Hibernate doesn't track changes

2. Persistent State - "Managed Entity"
What it is:
Entity is associated with database row
Managed by Hibernate's Persistence Context
Changes are automatically tracked and saved

What is Persistence Context?=>
Think of it as Hibernate's "memory" or "staging area"
A cache where Hibernate keeps track of all managed entities
Each EntityManager has its own Persistence Context

3. Detached State - "Graduated Entity"
Was previously persistent
No longer managed by Hibernate
Changes are NOT tracked automatically

How entities become Detached:=>entity manager methods calls like close(),clear(), evict()

Entity Manager's Role - "The Manager"==>
Think of EntityManager as:
A bridge between your Java objects and database
A manager that keeps track of entity states
Key Responsibilities:
1. CRUD Operations
EntityManager em = entityManagerFactory.createEntityManager();
// CREATE
em.persist(user); // Transient → Persistent

2. Query Execution
// JPQL Queries
List<User> users = em.createQuery("SELECT u FROM User u", User.class)
                    .getResultList();

// Native SQL
List<User> users = em.createNativeQuery("SELECT * FROM users", User.class)
                    .getResultList();

3. Transaction Management:
em.getTransaction().begin();

// All changes within transaction are atomic
User user1 = em.find(User.class, 1L);
User user2 = em.find(User.class, 2L);

user1.setName("John");
user2.setName("Jane");
em.getTransaction().commit(); // All changes saved together

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser() {
        User user = new User(); // Transient
        user.setName("John");

        // Becomes PERSISTENT
        User savedUser = userRepository.save(user);
        // Hibernate: INSERT INTO user (name) VALUES ('John')
        // ID is automatically generated

        return savedUser; // Still Persistent within @Transactional
    }
}

@Service
@Transactional
public class UserService {

    public void updateUser(Long userId) {
        // Becomes PERSISTENT
        User user = userRepository.findById(userId).orElseThrow();
        // Hibernate: SELECT * FROM user WHERE id = ?

        // Automatic change tracking - no explicit save needed!
        user.setName("John Doe"); // This change will be saved automatically
    } // Transaction commits - changes flushed to database
}

note -> Persistence Context in Spring Boot
Each @Transactional method has its own persistence context
Automatically created and closed by Spring
Spring Data JPA Repository = "Smart Manager"

public interface UserRepository extends JpaRepository<User, Long> {
    // Built-in methods that manage entity lifecycle
}

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void lifecycleDemo() {
        // 1. save() - Makes transient entities persistent
        User newUser = new User(); // Transient
        userRepository.save(newUser); // Persistent

        // 2. findById() - Returns persistent entities
        User user = userRepository.findById(1L).orElseThrow(); // Persistent

        // 3. delete() - Makes persistent entities removed
        userRepository.delete(user); // Removed

        // 4. findAll() - Returns persistent entities
        List<User> users = userRepository.findAll(); // All persistent
    }
}

all flow==>
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    // constructors, getters, setters
}

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void completeLifecycle() {
        // 1. TRANSIENT STATE
        User user = new User();
        user.setName("John");
        user.setEmail("john@email.com");

        // 2. PERSISTENT STATE (within transaction)
        User savedUser = userRepository.save(user);
        // Hibernate: INSERT INTO user (name, email) VALUES (?, ?)

        // 3. AUTOMATIC DIRTY CHECKING (Spring Boot Magic)
        savedUser.setName("John Doe"); // No explicit save call!
        savedUser.setEmail("johndoe@email.com");

        // 4. TRANSACTION COMMIT - changes automatically saved
    } // Hibernate: UPDATE user SET name=?, email=? WHERE id=?

    @Transactional(readOnly = true)
    public User getUserDetached(Long id) {
        User user = userRepository.findById(id).orElseThrow(); // Persistent
        return user; // Becomes DETACHED when method returns
    }

    public void updateDetachedEntity(Long id) {
        // Get DETACHED entity
        User detachedUser = getUserDetached(id);

        // Update detached entity
        detachedUser.setName("Updated Name");

        // Explicitly save to make it persistent again
        userRepository.save(detachedUser); // Detached → Persistent
        // Hibernate: UPDATE user SET name=? WHERE id=?
        Entities become detached when leaving transactional methods
        DTO pattern helps avoid detached entity issues
    }
}
so: @Transactional is key - defines persistence context boundaries









// When you call: userRepository.findByName("John")

1. Client: userRepository.findByName("John")
2. Proxy:  Intercepts method call
3. QueryLookupStrategy: Determines how to execute query
4. QueryCreator: Creates JPQL from method name
5. EntityManager: Creates Query object
6. Hibernate: Converts JPQL to SQL
7. JDBC: Executes SQL and returns ResultSet
8. Hibernate: Converts ResultSet to Entities
9. Spring Data: Returns result to client


Method Naming Conventions->
// Supported keywords:
find...By, read...By, query...By, count...By, get...By
delete...By, remove...By, exists...By

// Examples:
findByLastnameAndFirstname
findByEmailAddress
findByLastnameIgnoreCase
findByLastnameOrderByFirstnameAsc
 */
