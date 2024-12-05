package edu.kdmk;

import edu.kdmk.models.Client;
import edu.kdmk.repositories.ClientRepository;
import edu.kdmk.config.MongoConfig;
import edu.kdmk.config.RedisConfig;
import edu.kdmk.repositories.cache.CachedClientRepository;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class CachedClientBenchmark {

    private static MongoConfig mongoConfig;
    private static RedisConfig redisConfig;
    private static ClientRepository clientRepository;
    private static CachedClientRepository cachedClientRepository;

    private UUID clientId;
    private Client client;

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(CachedClientBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(1)
                .measurementIterations(1) // Jedna iteracja testowa
                .build();

        new Runner(options).run();
    }

    @Setup
    public void init() {
        mongoConfig = new MongoConfig();
        redisConfig = new RedisConfig();

        // Initialize repositories
        clientRepository = new ClientRepository(mongoConfig.getDatabase());
        cachedClientRepository = new CachedClientRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient());

        client = new Client(UUID.randomUUID(), "Mark", "Performance Test", "456 Test St", 0);
        cachedClientRepository.insert(client);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        if (mongoConfig != null) mongoConfig.close();
    }

    @Benchmark
    public void readCacheBenchmark() {
        cachedClientRepository.findById(client.getId());
    }

    @Benchmark
    public void benchmarkCacheMiss() {
        // Invalidate cache and measure time for a cache miss (read from DB and update cache)
        cachedClientRepository.invalidateCache(client.getId());
        cachedClientRepository.findById(client.getId());
    }

    @Benchmark
    public void benchmarkDatabaseOnly() {
        // Measure time for reading directly from the database
        clientRepository.findById(client.getId());
    }
}

