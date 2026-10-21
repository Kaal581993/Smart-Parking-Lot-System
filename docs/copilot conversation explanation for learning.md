Since no body answered, I collated with AI:

## Option B: Full Normalization - Detailed Analysis

### Benefits

**1. Data Integrity & Reusability**
- Aadhaar entity can be reused across multiple systems (banking, telecom, etc.)
- DrivingLicense entity can track license history (issued, renewed, revoked)
- Separation of concerns - each entity has single responsibility

**2. Flexibility for Complex Scenarios**
- Handle license transfers between states
- Track multiple license classes per person (LMV + MCWG)
- Support international licenses for foreign nationals
- Audit trail for license status changes

**3. Scalability**
- Easy to add new identity documents (Passport, Voter ID)
- Can build a master citizen registry
- Supports microservices architecture where identity service is separate

**4. Query Performance (in specific cases)**
- If you frequently query by license number, separate table with index is faster
- Can partition tables by document type

### Cons => Reprompted with AI agent to check further

**1. Complexity Overhead**
- More JOINs required for simple queries (3-4 tables instead of 1) => I want to implement and see this, as I think of preparing for the interview
- More repository classes to maintain => definitely a more space to learn and explore
- Increased DTO mapping complexity => again, Lombok and record based inplementation will definitely help
- Steeper learning curve for new developers => would love to dive deep into this

**2. Performance Penalty**
- Every vehicle owner lookup requires JOINs across 3+ tables => will force me explore some more effective solution to come up with
- N+1 query problems if not careful with lazy loading => time to explore it and a new learning curve, we could explore more better solution on how to solve this
- Increased memory usage due to object graph => not an issue for the moment
- Slower for simple CRUD operations => I am using H2 database and will not have lakhs of records, think of me looking to explore the solution to optimize the large scalable operations, if we don't come up with greedy ssolutions for complex use, what's the point of solving the assignment?

**3. Development Effort**
- More boilerplate code (repositories, services, DTOs) => lombok can solve this
- More test cases to write => would master to write the test cases
- Transaction management becomes complex => again, will have to prepare of greedy solution to optimize approach!
- Cascade operations need careful configuration => we will explore this, as it will help me to prepare for interviews

**4. Over-Engineering for Parking Lot**
- You're building a parking system, not a national identity database => Definitely! but as I said, we must ignore it!
- Aadhaar/License details rarely change during parking session => This is why I choose to normalize the entities or maybe we can declare the classes having static variables, correct me if I am wrong
- No need to track license history for parking operations => niether I am planning to implement this, but we can ignore for that
- Unnecessary abstraction for simple use case => elaorate with implementation of examples

**5. Data Consistency Challenges**
- Need to ensure Aadhaar and License are linked correctly => not a real time issue => we can ignore this for now
- Orphan records if cascade not configured properly => elborate and explain, we will come up with a greedy solution
- More points of failure in data flow => elborate and explain, we will come up with a greedy solution

### When Option B Makes Sense

**Use Option B if:**
- Building a comprehensive citizen management system
- Need to track license lifecycle (issue → renew → revoke)
- Multiple systems will share identity data
- Regulatory requirements demand audit trails
- Planning microservices with identity as separate service

**Avoid Option B if:**
- Building domain-specific application (parking, hotel, etc.)
- Identity data is just reference, not core business logic
- Team size is small
- Time-to-market is priority
- Simple CRUD operations dominate

### My Recommendation for Your Parking System

**Stick with Option A** because:
- Parking lot doesn't need license history tracking
- Aadhaar + License are just for identification during parking
- Simpler model = faster development, fewer bugs
- Can always refactor to Option B later if needed
- YAGNI principle (You Aren't Gonna Need It)

**Hybrid approach if uncertain:**
```java
VehicleOwner (Entity)
- @Id vehicleOwnerAadharNo (PK)
- @Column(unique = true) vehicleOwnerPanNo
- @OneToOne LicenseNo (FK)
- @Embedded Address (not separate entity)
```

This gives you some normalization without the full complexity.

---

## Learning Assignment: Composite Primary Keys in Spring Boot

### Objective
Implement Option B (Full Normalization) with composite primary keys to master advanced JPA concepts for interview preparation.

### Phase 1: Entity Structure Design

**Step 1.1: Create @Embeddable Composite Key Class**
```java
@Embeddable
public class OwnerIdentityId implements Serializable {
    private String aadharNo;
    private String licenseNo;

    // Implement equals() and hashCode()
    // Critical for composite keys to work correctly
}
```

**Step 1.2: Create Separate Identity Entities**
- `Aadhaar` entity with @Id aadharNo
- `DrivingLicense` entity with @Id licenseNo
- `Address` entity with @Id (composite or generated)

**Step 1.3: Create VehicleOwner with @EmbeddedId**
```java
@Entity
public class VehicleOwner {
    @EmbeddedId
    private OwnerIdentityId id;

    @OneToOne
    @JoinColumn(name = "aadhar_no")
    private Aadhaar aadhaar;

    @OneToOne
    @JoinColumn(name = "license_no")
    private DrivingLicense drivingLicense;
}
```

### Phase 2: Repository Layer

**Step 2.1: Create Repository Interfaces**
- `AadhaarRepository extends JpaRepository<Aadhaar, String>`
- `DrivingLicenseRepository extends JpaRepository<DrivingLicense, String>`
- `VehicleOwnerRepository extends JpaRepository<VehicleOwner, OwnerIdentityId>`

**Step 2.2: Custom Query Methods**
```java
// Learn to query by composite key parts
@Query("SELECT vo FROM VehicleOwner vo WHERE vo.id.aadharNo = :aadharNo")
Optional<VehicleOwner> findByAadharNo(@Param("aadharNo") String aadharNo);

// Learn JOIN queries
@Query("SELECT vo FROM VehicleOwner vo JOIN vo.aadhaar a WHERE a.state = :state")
List<VehicleOwner> findByAadhaarState(@Param("state") String state);
```

### Phase 3: DTO Mapping

**Step 3.1: Create DTOs using Java Records**
```java
public record VehicleOwnerDTO(
    String aadharNo,
    String licenseNo,
    String name,
    String phone
) {}
```

**Step 3.2: MapStruct or Manual Mapping**
- Learn MapStruct for automatic mapping
- Or implement manual mapping in service layer
- Handle nested object mapping (Aadhaar → DTO)

### Phase 4: Performance Optimization

**Step 4.1: Identify N+1 Query Problem**
```java
// BAD: Triggers N+1 queries
List<VehicleOwner> owners = repository.findAll();
for (VehicleOwner owner : owners) {
    owner.getAadhaar().getName(); // Lazy loading triggers query
}
```

**Step 4.2: Solutions to Implement**
1. **@EntityGraph** - Define fetch plan
```java
@EntityGraph(attributePaths = {"aadhaar", "drivingLicense"})
List<VehicleOwner> findAll();
```

2. **JOIN FETCH in JPQL**
```java
@Query("SELECT vo FROM VehicleOwner vo JOIN FETCH vo.aadhaar JOIN FETCH vo.drivingLicense")
List<VehicleOwner> findAllWithDetails();
```

3. **@BatchSize** - Batch fetching
```java
@BatchSize(size = 50)
@OneToOne
private Aadhaar aadhaar;
```

4. **Eager Loading** - Use cautiously
```java
@OneToOne(fetch = FetchType.EAGER)
private Aadhaar aadhaar;
```

### Phase 5: Transaction Management

**Step 5.1: Understand @Transactional**
```java
@Service
@Transactional
public class VehicleOwnerService {

    @Transactional(propagation = Propagation.REQUIRED)
    public VehicleOwner createOwner(VehicleOwnerDTO dto) {
        // All operations in single transaction
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAudit(VehicleOwner owner) {
        // Separate transaction
    }
}
```

**Step 5.2: Transaction Isolation Levels**
- Learn READ_COMMITTED, READ_UNCOMMITTED, REPEATABLE_READ, SERIALIZABLE
- Implement and test each level

**Step 5.3: Rollback Scenarios**
```java
@Transactional(rollbackFor = {CustomException.class})
public void complexOperation() {
    // Custom rollback behavior
}
```

### Phase 6: Cascade Operations

**Step 6.1: Configure Cascade Types**
```java
@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
@JoinColumn(name = "aadhar_no")
private Aadhaar aadhaar;
```

**Step 6.2: Understand Each Cascade Type**
- `PERSIST` - Save child when parent is saved
- `MERGE` - Update child when parent is updated
- `REMOVE` - Delete child when parent is deleted
- `REFRESH` - Refresh child from DB
- `DETACH` - Detach child from persistence context
- `ALL` - All of the above

**Step 6.3: Orphan Removal**
```java
orphanRemoval = true
// Automatically deletes child when removed from collection
```

### Phase 7: Data Consistency Challenges

**Step 7.1: Orphan Records Problem**
- **Scenario**: Delete VehicleOwner but Aadhaar remains
- **Solution**: Configure `orphanRemoval = true`
- **Test**: Verify cascade delete works correctly

**Step 7.2: Data Flow Failure Points**
1. **Partial Save Failure** - Aadhaar saved, License fails
   - Solution: Use @Transactional with rollback
2. **Concurrent Updates** - Two transactions updating same record
   - Solution: Implement @Version for optimistic locking
3. **Foreign Key Violations** - Delete referenced record
   - Solution: Configure cascade or handle exceptions

**Step 7.3: Implementing @Version for Optimistic Locking**
```java
@Version
private Long version;
```

### Phase 8: Testing Strategy

**Step 8.1: Unit Tests**
- Test composite key equals/hashCode
- Test repository methods
- Test DTO mapping

**Step 8.2: Integration Tests**
```java
@DataJpaTest
class VehicleOwnerRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveOwnerWithCompositeKey() {
        // Test composite key persistence
    }

    @Test
    void shouldHandleCascadeOperations() {
        // Test cascade save/delete
    }
}
```

**Step 8.3: Performance Tests**
- Measure query execution time
- Test with different fetch strategies
- Compare JOIN vs N+1 performance

### Phase 9: Advanced Topics

**Step 9.1: Static Variables vs Entities**
- **Your Question**: "Can we declare classes having static variables?"
- **Answer**: No, static variables are not persisted in database
- **Correct Approach**: Use @Enumerated for enums, or reference tables
- **Example**: StateCode as enum with @Enumerated(EnumType.STRING)

**Step 9.2: Caching Strategies**
- Learn First-Level Cache (EntityManager)
- Learn Second-Level Cache (Ehcache, Hazelcast)
- Implement query caching

**Step 9.3: Database Indexing**
```java
@Table(indexes = {
    @Index(name = "idx_aadhar", columnList = "aadhar_no"),
    @Index(name = "idx_license", columnList = "license_no")
})
```

### Phase 10: Interview Preparation Questions

**Technical Questions to Master:**
1. When to use @EmbeddedId vs @IdClass?
2. Difference between FetchType.LAZY and EAGER?
3. How to solve N+1 query problem?
4. What is orphanRemoval and when to use it?
5. Transaction propagation types?
6. Optimistic vs Pessimistic locking?
7. Cascade types and their use cases?
8. When to use @Version?
9. Difference between @JoinColumn and @JoinTable?
10. How does equals/hashCode affect composite keys?

### Implementation Order

1. **Week 1**: Phase 1-2 (Entity design + Repositories)
2. **Week 2**: Phase 3-4 (DTOs + Performance optimization)
3. **Week 3**: Phase 5-6 (Transactions + Cascade)
4. **Week 4**: Phase 7-8 (Data consistency + Testing)
5. **Week 5**: Phase 9-10 (Advanced topics + Interview prep)

### Key Learning Outcomes

By completing this assignment, you will master:
- Composite primary keys with @Embeddable
- Complex entity relationships
- Performance tuning techniques
- Transaction management
- Cascade operations
- Testing strategies for JPA
- Interview-ready knowledge

### Notes on "Greedy Solutions"

As you mentioned, we'll explore optimized solutions:
- **Query Optimization**: Indexing, query plans, EXPLAIN ANALYZE
- **Caching**: Reduce database hits
- **Batch Operations**: Bulk insert/update
- **Connection Pooling**: HikariCP configuration
- **Read Replicas**: For read-heavy operations
- **Partitioning**: For large datasets

This approach ensures you learn both the problems AND their enterprise-grade solutions.
