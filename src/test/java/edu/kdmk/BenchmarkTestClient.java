package edu.kdmk;


import edu.kdmk.models.Client;
import edu.kdmk.repositories.ClientRepository;
import edu.kdmk.config.MongoConfig;
import edu.kdmk.config.RedisConfig;
import edu.kdmk.repositories.cache.CachedClientRepository;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Deprecated
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class BenchmarkTestClient {

    //private static final int N = 10_000_000;

    //private static List<String> DATA_FOR_TESTING = createData();

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(BenchmarkTestClient.class.getSimpleName())
                .forks(1)
                .warmupIterations(1)
                .measurementIterations(1) // Jedna iteracja testowa
                .build();

        new Runner(opt).run();
    }

    private MongoConfig mongoConfig;
    private RedisConfig redisConfig;
    private ClientRepository clientRepository;
    private CachedClientRepository cachedClientRepository;

    private UUID clientId;
    private Client client;

    @Setup(Level.Trial)
    public void setup() {
        // MongoDB and Redis configurations
        String connectionString = ConnectionStringProvider.getConnectionString();
        String databaseName = "performance_test_db";

        mongoConfig = new MongoConfig();
        redisConfig = new RedisConfig();

        // Initialize repositories
        clientRepository = new ClientRepository(mongoConfig.getDatabase());
        cachedClientRepository = new CachedClientRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient());

        // Create a sample client
        client = new Client(UUID.randomUUID(), "Mark", "Performance Test", "456 Test St", 0);
        cachedClientRepository.insert(client); // Insert into the database via CachedClientRepository
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        if (mongoConfig != null) mongoConfig.close();
    }

    @Benchmark
    public void benchmarkCacheHit() {
        // Measure time for accessing a cached item (Cache hit)
        Optional<Client> clientFromCache = cachedClientRepository.findById(client.getId());
        if (!clientFromCache.isPresent()) {
            throw new IllegalStateException("Client should be retrieved from the cache");
        }
    }

    @Benchmark
    public void benchmarkCacheMiss() {
        // Invalidate cache and measure time for a cache miss (read from DB and update cache)
        cachedClientRepository.invalidateCache(client.getId());
        Optional<Client> clientFromCacheMiss = cachedClientRepository.findById(client.getId());
        if (!clientFromCacheMiss.isPresent()) {
            throw new IllegalStateException("Client should be retrieved from the database and updated in the cache");
        }
    }

    @Benchmark
    public void benchmarkDatabaseOnly() {
        // Measure time for reading directly from the database
        Optional<Client> clientFromDb = clientRepository.findById(client.getId());
        if (!clientFromDb.isPresent()) {
            throw new IllegalStateException("Client should be retrieved directly from the database");
        }
    }
}
