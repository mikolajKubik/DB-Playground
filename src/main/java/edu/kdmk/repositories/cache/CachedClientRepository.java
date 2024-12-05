package edu.kdmk.repositories.cache;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.Client;
import edu.kdmk.repositories.ClientRepository;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lombok.extern.java.Log;
import redis.clients.jedis.JedisPooled;

import java.util.Optional;
import java.util.UUID;

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

        try {
            String clientJson = redisClient.get(cacheKey);
            if (clientJson != null) {
                Client cachedClient = jsonb.fromJson(clientJson, Client.class);
                return Optional.of(cachedClient);
            }
        } catch (Exception e) {
            log.severe("Redis connection failed during findById. Key: " + cacheKey + ". Error: " + e.getMessage());
        }


        Optional<Client> clientFromDb = super.findById(id);

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
