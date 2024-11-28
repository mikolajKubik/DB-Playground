/*
package edu.kdmk;

import edu.kdmk.models.Client;
import edu.kdmk.repositories.ClientRepository;
import edu.kdmk.config.MongoConfig;
import edu.kdmk.config.RedisConfig;
import edu.kdmk.repositories.cache.CachedClientRepository;
import org.junit.jupiter.api.*;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import javax.naming.directory.SearchResult;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@State(Scope.Benchmark)
public class CachedClientTest {

    private static MongoConfig mongoConfig;
    private static RedisConfig redisConfig;
    private static ClientRepository clientRepository;
    private static CachedClientRepository cachedClientRepository;

    private UUID clientId;
    private Client client;

    */
/*public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }*//*


    @Benchmark
    public void init() {
        String connectionString = ConnectionStringProvider.getConnectionString();
        String databaseName = "performance_test_db";

//        mongoConfig = new MongoConfig(connectionString, databaseName);
        mongoConfig = new MongoConfig();
        redisConfig = new RedisConfig();

        // Initialize repositories
        clientRepository = new ClientRepository(mongoConfig.getDatabase());
        cachedClientRepository = new CachedClientRepository(mongoConfig.getDatabase());
    }


    @AfterAll
    static void tearDown() throws Exception {
        if (mongoConfig != null) mongoConfig.close();
    }

    @BeforeEach
    void setupClient() {
        client = new Client(UUID.randomUUID(), "Mark", "Performance Test", "456 Test St", 0);
    }

    @Test
    void testPerformanceBetweenRegularAndCachedRepository() {
        // Insert the client into MongoDB via CachedClientRepository
        boolean inserted = clientRepository.insert(client);
        assertTrue(inserted, "Client should be inserted successfully into MongoDB");

        long startTime;
        long endTime;

         System.out.println(client.getId());

        // Measure time for first access using CachedClientRepository (MongoDB + Cache)
        startTime = System.nanoTime();
        Optional<Client> clientFromCachedRepoFirst = cachedClientRepository.findById(client.getId());
        endTime = System.nanoTime();

        assertTrue(clientFromCachedRepoFirst.isPresent(), "Client should be retrieved from MongoDB using CachedClientRepository");
        System.out.println("Time to access using CachedClientRepository (first access, MongoDB): " + (endTime - startTime) / 1_000_000_000.0 + " seconds");

        // Measure time for second access using CachedClientRepository (Cache only)
        startTime = System.nanoTime();
        Optional<Client> clientFromCachedRepoSecond = cachedClientRepository.findById(client.getId());
        endTime = System.nanoTime();

        assertTrue(clientFromCachedRepoSecond.isPresent(), "Client should be retrieved from cache using CachedClientRepository");
        System.out.println("Time to access using CachedClientRepository (second access, Cache): " + (endTime - startTime) / 1_000_000_000.0 + " seconds");

        startTime = System.nanoTime();
        Optional<Client> clientFromCachedRepoThird = cachedClientRepository.findById(client.getId());
        endTime = System.nanoTime();

        assertTrue(clientFromCachedRepoThird.isPresent(), "Client should be retrieved from cache using CachedClientRepository");
        System.out.println("Time to access using CachedClientRepository (third access, Cache): " + (endTime - startTime) / 1_000_000_000.0 + " seconds");

        startTime = System.nanoTime();
        Optional<Client> clientFromCachedRepoFourth = cachedClientRepository.findById(client.getId());
        endTime = System.nanoTime();

        assertTrue(clientFromCachedRepoFourth.isPresent(), "Client should be retrieved from cache using CachedClientRepository");
        System.out.println("Time to access using CachedClientRepository (third IV, Cache): " + (endTime - startTime) / 1_000_000_000.0 + " seconds");


    }

    @Benchmark
    public void insertClient() {

    }
}

*/
/*
* import org.openjdk.jmh.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class CacheVsDatabaseBenchmark {

    private Map<Integer, String> cache;
    private Map<Integer, String> database;

    @Setup(Level.Iteration)
    public void setup() {
        // Symulacja bazy danych
        database = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            database.put(i, "Value " + i);
        }

        // Symulacja cache
        cache = new HashMap<>();
    }

    @Benchmark
    public String readFromCacheHit() {
        int key = 1; // Klucz istniejący w cache
        cache.put(key, "Cached Value");
        return cache.get(key); // Trafienie w cache
    }

    @Benchmark
    public String readFromCacheMiss() {
        int key = 1001; // Klucz, którego nie ma w cache
        String value = cache.get(key);
        if (value == null) {
            value = database.get(key); // Odczyt z bazy
            if (value != null) {
                cache.put(key, value); // Aktualizacja cache
            }
        }
        return value;
    }

    @Benchmark
    public String readFromDatabase() {
        int key = 1002; // Odczyt bezpośrednio z bazy danych
        return database.get(key);
    }

    @Benchmark
    public void invalidateCache() {
        cache.clear(); // Unieważnienie cache
    }
}
*
* */
