package edu.kdmk.repositories.cache;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.Client;
import edu.kdmk.config.RedisConfig;
import edu.kdmk.repositories.ClientRepository;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lombok.extern.java.Log;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.json.JsonSetParams;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Log
public class CachedClientRepository extends ClientRepository {
    private final JedisPooled redisClient;
    private final Jsonb jsonb;

    public CachedClientRepository(MongoDatabase database, JedisPooled jedisPooled) {
        super(database);
        this.redisClient = jedisPooled;
        this.jsonb = JsonbBuilder.create();
    }

    private String getClientCacheKey(UUID clientId) {
        return "client:" + clientId.toString();
    }

    @Override
    public Optional<Client> findById(UUID id) {
        String cacheKey = getClientCacheKey(id);

        // Attempt to retrieve from Redis
        try {
            String clientJson = redisClient.get(cacheKey);
            if (clientJson != null) {
                Client cachedClient = jsonb.fromJson(clientJson, Client.class);
                return Optional.of(cachedClient);
            }
        } catch (Exception e) {
            log.severe("Redis connection failed during findById. Key: " + cacheKey + ". Error: " + e.getMessage());
        }

        // Fallback to MongoDB if Redis misses
        Optional<Client> clientFromDb = super.findById(id);

        // Attempt to update the cache
        clientFromDb.ifPresent(client -> {
            try {
                redisClient.set(cacheKey, jsonb.toJson(client));
                redisClient.expire(cacheKey, 3600);
            } catch (Exception e) {
                log.severe("Failed to update cache for clientId: " + client.getId() + ". Error: " + e.getMessage());
            }
        });

        return clientFromDb;
    }

    @Override
    public Optional<Client> findById(ClientSession session, UUID id) {
        String cacheKey = getClientCacheKey(id);

        try {
            String clientJson = redisClient.get(cacheKey);
            if (clientJson != null) {
                Client cachedClient = jsonb.fromJson(clientJson, Client.class);
                return Optional.of(cachedClient);
            }
        } catch (Exception e) {
            log.severe("Redis connection failed during findById (session). Key: " + cacheKey + ". Error: " + e.getMessage());
        }

        Optional<Client> clientFromDb = super.findById(session, id);

        clientFromDb.ifPresent(client -> {
            try {
                redisClient.set(cacheKey, jsonb.toJson(client));
                redisClient.expire(cacheKey, 3600);
            } catch (Exception e) {
                log.severe("Failed to update cache for clientId: " + client.getId() + ". Error: " + e.getMessage());
            }
        });

        return clientFromDb;
    }

    @Override
    public boolean insert(Client client) {
        boolean inserted = super.insert(client);
        if (inserted) {
            String cacheKey = getClientCacheKey(client.getId());
            try {
                redisClient.set(cacheKey, jsonb.toJson(client));
                redisClient.expire(cacheKey, 3600);
            } catch (Exception e) {
                log.severe("Failed to cache client after insert. clientId: " + client.getId() + ". Error: " + e.getMessage());
            }
        }
        return inserted;
    }

    @Override
    public boolean update(Client updatedClient) {
        boolean updated = super.update(updatedClient);
        if (updated) {
            String cacheKey = getClientCacheKey(updatedClient.getId());
            try {
                redisClient.set(cacheKey, jsonb.toJson(updatedClient));
                redisClient.expire(cacheKey, 3600);
            } catch (Exception e) {
                log.severe("Failed to update cache for clientId: " + updatedClient.getId() + ". Error: " + e.getMessage());
            }
        }
        return updated;
    }

    @Override
    public boolean deleteById(UUID id) {
        boolean deleted = super.deleteById(id);
        if (deleted) {
            String cacheKey = getClientCacheKey(id);
            try {
                redisClient.del(cacheKey);
            } catch (Exception e) {
                log.severe("Failed to remove client from cache during delete. clientId: " + id + ". Error: " + e.getMessage());
            }
        }
        return deleted;
    }

    public void invalidateCache(UUID clientId) {
        String cacheKey = getClientCacheKey(clientId);
        try {
            redisClient.del(cacheKey);
        } catch (Exception e) {
            log.severe("Failed to invalidate cache for clientId: " + clientId + ". Error: " + e.getMessage());
        }
    }

    @Override
    public boolean markAsRented(ClientSession session, UUID clientId) {
        boolean result = super.markAsRented(session, clientId);
        if (result) {
            String cacheKey = getClientCacheKey(clientId);
            try {
                redisClient.set(cacheKey, jsonb.toJson(clientId));
                redisClient.expire(cacheKey, 3600);
            } catch (Exception e) {
                log.severe("Failed to update cache for rented clientId: " + clientId + ". Error: " + e.getMessage());
            }
        }
        return result;
    }

    @Override
    public boolean unmarkAsRented(ClientSession session, UUID clientId) {
        boolean result = super.unmarkAsRented(session, clientId);
        if (result) {
            String cacheKey = getClientCacheKey(clientId);
            try {
                redisClient.set(cacheKey, jsonb.toJson(clientId));
                redisClient.expire(cacheKey, 3600);
            } catch (Exception e) {
                log.severe("Failed to update cache for unrented clientId: " + clientId + ". Error: " + e.getMessage());
            }
        }
        return result;
    }
}

/* public class CachedClientRepository extends ClientRepository {
    private final JedisPooled redisClient;
    private final Jsonb jsonb;

    public CachedClientRepository(MongoDatabase database, JedisPooled jedisPooled) {
        super(database);
        this.redisClient = jedisPooled;
        jsonb = JsonbBuilder.create();
    }

    private String getClientCacheKey(UUID clientId) {
        return "client:" + clientId.toString();
    }

    @Override
    public Optional<Client> findById(UUID id) {
        String cacheKey = getClientCacheKey(id);
        String clientJson = redisClient.get(cacheKey);
        if (clientJson != null) {
            Client cachedClient = jsonb.fromJson(clientJson, Client.class);
            return Optional.of(cachedClient);
        }

        Optional<Client> clientFromDb = super.findById(id);
        if (clientFromDb.isPresent()) {
            redisClient.set(cacheKey, jsonb.toJson(clientFromDb.get()));
            redisClient.expire(cacheKey, 3600);
        }
        return clientFromDb;
    }

    @Override
    public Optional<Client> findById(ClientSession session, UUID id) {
        String cacheKey = getClientCacheKey(id);
        String clientJson = redisClient.get(cacheKey);
        Client cachedClient = jsonb.fromJson(clientJson, Client.class);
        if (cachedClient != null) {
            return Optional.of(cachedClient);
        }

        // Fallback to database if not in cache
        Optional<Client> clientFromDb = super.findById(session, id);
        if (clientFromDb.isPresent()) {
            redisClient.set(cacheKey, jsonb.toJson(clientFromDb.get()));
            redisClient.expire(cacheKey, 3600);
        }
        return clientFromDb;
    }

    @Override
    public boolean insert(Client client) {
        boolean inserted = super.insert(client);
        if (inserted) {
            var cacheKey = getClientCacheKey(client.getId());
            redisClient.set(cacheKey, jsonb.toJson(client));
            redisClient.expire(cacheKey, 3600);// Cache the inserted client
        }
        return inserted;
    }

    @Override
    public boolean insert(ClientSession session, Client client) {
        boolean inserted = super.insert(session, client);
        if (inserted) {
            var cacheKey = getClientCacheKey(client.getId());
            redisClient.set(cacheKey, jsonb.toJson(client));
            redisClient.expire(cacheKey, 3600);// Cache the inserted client
        }
        return inserted;
    }

    @Override
    public boolean update(Client updatedClient) {
        boolean updated = super.update(updatedClient);
        if (updated) {
            var cacheKey = getClientCacheKey(updatedClient.getId());
            redisClient.set(cacheKey, jsonb.toJson(updatedClient));
            redisClient.expire(cacheKey, 3600); // Update cache
        }
        return updated;
    }

    @Override
    public boolean update(ClientSession session, Client updatedClient) {
        boolean updated = super.update(session, updatedClient);
        if (updated) {
            var cacheKey = getClientCacheKey(updatedClient.getId());
            redisClient.set(cacheKey, jsonb.toJson(updatedClient));
            redisClient.expire(cacheKey, 3600); // Update cache
        }
        return updated;
    }

    @Override
    public boolean deleteById(UUID id) {
        boolean deleted = super.deleteById(id);
        if (deleted) {
            redisClient.del(getClientCacheKey(id)); // Remove from cache
        }
        return deleted;
    }

    @Override
    public boolean deleteById(ClientSession session, UUID id) {
        boolean deleted = super.deleteById(session, id);
        if (deleted) {
            redisClient.del(getClientCacheKey(id)); // Remove from cache
        }
        return deleted;
    }

    public void invalidateCache(UUID clientId) {
        String cacheKey = getClientCacheKey(clientId);
        redisClient.del(cacheKey);
    }

    @Override
    public boolean markAsRented(ClientSession session, UUID clientId) {
        boolean result = super.markAsRented(session, clientId);
        if (result) {
            //redisClient.del(getClientCacheKey(clientId)); // Remove from cache
            redisClient.set(getClientCacheKey(clientId), jsonb.toJson(clientId)); // Update cache

        }
        return result;
    }

    @Override
    public boolean unmarkAsRented(ClientSession session, UUID clientId) {
        boolean result = super.unmarkAsRented(session, clientId);
        if (result) {
            //redisClient.del(getClientCacheKey(clientId)); // Remove from cache
            redisClient.set(getClientCacheKey(clientId), jsonb.toJson(clientId)); // Update cache
        }
        return result;
    }
} */
