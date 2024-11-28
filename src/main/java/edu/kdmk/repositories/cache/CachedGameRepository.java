package edu.kdmk.repositories.cache;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.game.Game;
import edu.kdmk.repositories.GameRepository;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lombok.extern.java.Log;
import redis.clients.jedis.JedisPooled;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Log
public class CachedGameRepository extends GameRepository {
    private final JedisPooled redisClient;
    private final Jsonb jsonb;

    public CachedGameRepository(MongoDatabase database, JedisPooled jedisPooled) {
        super(database);
        this.redisClient = jedisPooled;
        this.jsonb = JsonbBuilder.create();
    }

    private String getGameCacheKey(UUID gameId) {
        return "game:" + gameId.toString();
    }

    @Override
    public Optional<Game> findById(UUID id) {
        String cacheKey = getGameCacheKey(id);

        // Attempt to retrieve from Redis
        try {
            String gameJson = redisClient.get(cacheKey);
            if (gameJson != null) {
                Game cachedGame = jsonb.fromJson(gameJson, Game.class);
                return Optional.of(cachedGame);
            }
        } catch (Exception e) {
            log.severe("Redis connection failed during findById. Key: " + cacheKey + ". Error: " + e.getMessage());
        }

        // Fallback to MongoDB if Redis misses
        Optional<Game> gameFromDb = super.findById(id);

        // Attempt to update the cache
        gameFromDb.ifPresent(game -> {
            try {
                redisClient.set(cacheKey, jsonb.toJson(game));
                redisClient.expire(cacheKey, 3600);
            } catch (Exception e) {
                log.severe("Failed to update cache for gameId: " + game.getId() + ". Error: " + e.getMessage());
            }
        });

        return gameFromDb;
    }

    @Override
    public Optional<Game> findById(ClientSession session, UUID id) {
        String cacheKey = getGameCacheKey(id);

        try {
            String gameJson = redisClient.get(cacheKey);
            if (gameJson != null) {
                Game cachedGame = jsonb.fromJson(gameJson, Game.class);
                return Optional.of(cachedGame);
            }
        } catch (Exception e) {
            log.severe("Redis connection failed during findById (session). Key: " + cacheKey + ". Error: " + e.getMessage());
        }

        Optional<Game> gameFromDb = super.findById(session, id);

        gameFromDb.ifPresent(game -> {
            try {
                redisClient.set(cacheKey, jsonb.toJson(game));
                redisClient.expire(cacheKey, 3600);
            } catch (Exception e) {
                log.severe("Failed to update cache for gameId: " + game.getId() + ". Error: " + e.getMessage());
            }
        });

        return gameFromDb;
    }

    @Override
    public boolean insert(Game game) {
        boolean inserted = super.insert(game);
        if (inserted) {
            String cacheKey = getGameCacheKey(game.getId());
            try {
                redisClient.set(cacheKey, jsonb.toJson(game));
                redisClient.expire(cacheKey, 3600);
            } catch (Exception e) {
                log.severe("Failed to cache game after insert. gameId: " + game.getId() + ". Error: " + e.getMessage());
            }
        }
        return inserted;
    }

    @Override
    public boolean update(Game updatedGame) {
        boolean updated = super.update(updatedGame);
        if (updated) {
            String cacheKey = getGameCacheKey(updatedGame.getId());
            try {
                redisClient.set(cacheKey, jsonb.toJson(updatedGame));
                redisClient.expire(cacheKey, 3600);
            } catch (Exception e) {
                log.severe("Failed to update cache for gameId: " + updatedGame.getId() + ". Error: " + e.getMessage());
            }
        }
        return updated;
    }

    @Override
    public boolean deleteById(UUID id) {
        boolean deleted = super.deleteById(id);
        if (deleted) {
            String cacheKey = getGameCacheKey(id);
            try {
                redisClient.del(cacheKey);
            } catch (Exception e) {
                log.severe("Failed to remove game from cache during delete. gameId: " + id + ". Error: " + e.getMessage());
            }
        }
        return deleted;
    }

    public void invalidateCache(UUID gameId) {
        String cacheKey = getGameCacheKey(gameId);
        try {
            redisClient.del(cacheKey);
        } catch (Exception e) {
            log.severe("Failed to invalidate cache for gameId: " + gameId + ". Error: " + e.getMessage());
        }
    }

    @Override
    public boolean markAsRented(ClientSession session, UUID gameId) {
        boolean result = super.markAsRented(session, gameId);
        if (result) {
            String cacheKey = getGameCacheKey(gameId);
            try {
                Optional<Game> game = super.findById(session, gameId);
                game.ifPresent(value -> {
                    redisClient.set(cacheKey, jsonb.toJson(value));
                    redisClient.expire(cacheKey, 3600);
                });
            } catch (Exception e) {
                log.severe("Failed to update cache for rented gameId: " + gameId + ". Error: " + e.getMessage());
            }
        }
        return result;
    }

    @Override
    public boolean unmarkAsRented(ClientSession session, UUID gameId) {
        boolean result = super.unmarkAsRented(session, gameId);
        if (result) {
            String cacheKey = getGameCacheKey(gameId);
            try {
                Optional<Game> game = super.findById(session, gameId);
                game.ifPresent(value -> {
                    redisClient.set(cacheKey, jsonb.toJson(value));
                    redisClient.expire(cacheKey, 3600);
                });
            } catch (Exception e) {
                log.severe("Failed to update cache for unrented gameId: " + gameId + ". Error: " + e.getMessage());
            }
        }
        return result;
    }

    @Override
    public List<Game> findAll() {
        // No caching for findAll as the dataset might be too large to cache entirely.
        return super.findAll();
    }
}
