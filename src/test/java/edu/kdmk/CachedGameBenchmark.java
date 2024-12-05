package edu.kdmk;

import edu.kdmk.config.MongoConfig;
import edu.kdmk.config.RedisConfig;
import edu.kdmk.models.game.BoardGame;
import edu.kdmk.models.game.GameType;
import edu.kdmk.repositories.GameRepository;
import edu.kdmk.repositories.cache.CachedGameRepository;
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
public class CachedGameBenchmark {

    private static MongoConfig mongoConfig;
    private static RedisConfig redisConfig;
    private static GameRepository gameRepository;
    private static CachedGameRepository cachedGameRepository;
    private BoardGame boardGame;

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(CachedGameBenchmark.class.getSimpleName())
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

        gameRepository = new GameRepository(mongoConfig.getDatabase());
        cachedGameRepository = new CachedGameRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient());

        boardGame = new BoardGame(UUID.randomUUID(), "Uno2", GameType.BOARD_GAME,10, 0, 6, 8);
        cachedGameRepository.insert(boardGame);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        if (mongoConfig != null) mongoConfig.close();
    }

    @Benchmark
    public void readCacheBenchmark() {
        cachedGameRepository.findById(boardGame.getId());
    }

    @Benchmark
    public void benchmarkCacheMiss() {
        cachedGameRepository.invalidateCache(boardGame.getId());
        cachedGameRepository.findById(boardGame.getId());
    }

    @Benchmark
    public void benchmarkDatabaseOnly() {
        gameRepository.findById(boardGame.getId());
    }
}
