package edu.kdmk.repositories.cache;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.Client;
import edu.kdmk.config.RedisConfig;
import edu.kdmk.repositories.ClientRepository;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.json.JsonSetParams;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CachedClientRepository extends ClientRepository {
    private final JedisPooled redisClient;
    private final Jsonb jsonb;

    public CachedClientRepository(MongoDatabase database) {
        super(database);
        this.redisClient = new RedisConfig().getRedisJsonClient();
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
        clientFromDb.ifPresent(client -> redisClient.set(cacheKey, jsonb.toJson(client))); // Cache if found
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
        clientFromDb.ifPresent(client -> redisClient.set(cacheKey, jsonb.toJson(client))); // Cache if found
        return clientFromDb;
    }

    @Override
    public boolean insert(Client client) {
        boolean inserted = super.insert(client);
        if (inserted) {
            redisClient.set(getClientCacheKey(client.getId()), jsonb.toJson(client)); // Cache the inserted client
        }
        return inserted;
    }

    @Override
    public boolean insert(ClientSession session, Client client) {
        boolean inserted = super.insert(session, client);
        if (inserted) {
            redisClient.set(getClientCacheKey(client.getId()), jsonb.toJson(client)); // Cache the inserted client
        }
        return inserted;
    }

    @Override
    public boolean update(Client updatedClient) {
        boolean updated = super.update(updatedClient);
        if (updated) {
            redisClient.set(getClientCacheKey(updatedClient.getId()), jsonb.toJson(updatedClient)); // Update cache
        }
        return updated;
    }

    @Override
    public boolean update(ClientSession session, Client updatedClient) {
        boolean updated = super.update(session, updatedClient);
        if (updated) {
            redisClient.set(getClientCacheKey(updatedClient.getId()), jsonb.toJson(updatedClient)); // Update cache
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

    @Override
    public List<Client> findAll() {
        // Consider caching the entire list if this is a frequent operation,
        // though this is often less efficient due to potential data inconsistency.
        return super.findAll().stream()
                .peek(client -> redisClient.jsonSet(getClientCacheKey(client.getId()), client, JsonSetParams.jsonSetParams().nx())) // Cache each client if not already cached
                .collect(Collectors.toList());
    }

    public void invalidateCache(UUID clientId) {
        String cacheKey = getClientCacheKey(clientId);
        redisClient.del(cacheKey);
    }
}

//package edu.kdmk.repositories.cache;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//import com.mongodb.client.ClientSession;
//import com.mongodb.client.MongoDatabase;
//import edu.kdmk.models.Client;
//import edu.kdmk.config.RedisConfig;
//import edu.kdmk.repositories.ClientRepository;
//import redis.clients.jedis.UnifiedJedis;
//import redis.clients.jedis.search.Query;
//import redis.clients.jedis.search.SearchResult;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//public class CachedClientRepository extends ClientRepository {
//    private final UnifiedJedis redisClient;
//    private final Gson gson;
//
//    public CachedClientRepository(MongoDatabase database, RedisConfig redisConfig) {
//        super(database);
//        this.redisClient = redisConfig.getRedisJsonClient();
//        this.gson = new Gson();
//    }
//
//    @Override
//    public Optional<Client> findById(UUID id) {
//        String cacheKey = id.toString();
//        System.out.println(cacheKey);
//        //SearchResult search = redisClient.ftSearch(cacheKey, new Query());
//        try {
//            // Fetch JSON string from Redis
//            String cachedJson = redisClient.get(cacheKey);
//            if (cachedJson != null) {
//                Client cachedClient = gson.fromJson(cachedJson, Client.class);
//                return Optional.of(cachedClient);
//            }
//        } catch (JsonSyntaxException e) {
//            System.err.println("Error decoding JSON from Redis: " + e.getMessage());
//        }
//
//        // Fallback to database if not in cache
//        Optional<Client> clientFromDb = super.findById(id);
//        clientFromDb.ifPresent(client -> {
//            try {
//                redisClient.set(cacheKey, gson.toJson(client)); // Cache as JSON string
//            } catch (Exception e) {
//                System.err.println("Error caching client in Redis: " + e.getMessage());
//            }
//        });
//        return clientFromDb;
//    }
//
//    @Override
//    public Optional<Client> findById(ClientSession session, UUID id) {
//        String cacheKey = id.toString();
//
//        try {
//            // Fetch JSON string from Redis
//            String cachedJson = redisClient.get(cacheKey);
//            if (cachedJson != null) {
//                Client cachedClient = gson.fromJson(cachedJson, Client.class);
//                return Optional.of(cachedClient);
//            }
//        } catch (JsonSyntaxException e) {
//            System.err.println("Error decoding JSON from Redis: " + e.getMessage());
//        }
//
//        // Fallback to database if not in cache
//        Optional<Client> clientFromDb = super.findById(session, id);
//        clientFromDb.ifPresent(client -> {
//            try {
//                redisClient.set(cacheKey, gson.toJson(client)); // Cache as JSON string
//            } catch (Exception e) {
//                System.err.println("Error caching client in Redis: " + e.getMessage());
//            }
//        });
//        return clientFromDb;
//    }
//
//    @Override
//    public boolean insert(Client client) {
//        boolean inserted = super.insert(client);
//        if (inserted) {
//            try {
//                redisClient.set(client.getId().toString(), gson.toJson(client)); // Cache the client
//            } catch (Exception e) {
//                System.err.println("Error caching client in Redis: " + e.getMessage());
//            }
//        }
//        return inserted;
//    }
//
//    @Override
//    public boolean insert(ClientSession session, Client client) {
//        boolean inserted = super.insert(session, client);
//        if (inserted) {
//            try {
//                redisClient.set(client.getId().toString(), gson.toJson(client)); // Cache the client
//            } catch (Exception e) {
//                System.err.println("Error caching client in Redis: " + e.getMessage());
//            }
//        }
//        return inserted;
//    }
//
//    @Override
//    public boolean update(Client updatedClient) {
//        boolean updated = super.update(updatedClient);
//        if (updated) {
//            try {
//                redisClient.set(updatedClient.getId().toString(), gson.toJson(updatedClient)); // Update cache
//            } catch (Exception e) {
//                System.err.println("Error updating client in Redis: " + e.getMessage());
//            }
//        }
//        return updated;
//    }
//
//    @Override
//    public boolean update(ClientSession session, Client updatedClient) {
//        boolean updated = super.update(session, updatedClient);
//        if (updated) {
//            try {
//                redisClient.set(updatedClient.getId().toString(), gson.toJson(updatedClient)); // Update cache
//            } catch (Exception e) {
//                System.err.println("Error updating client in Redis: " + e.getMessage());
//            }
//        }
//        return updated;
//    }
//
//    @Override
//    public boolean deleteById(UUID id) {
//        boolean deleted = super.deleteById(id);
//        if (deleted) {
//            try {
//                redisClient.del(id.toString()); // Remove from cache
//            } catch (Exception e) {
//                System.err.println("Error removing client from Redis: " + e.getMessage());
//            }
//        }
//        return deleted;
//    }
//
//    @Override
//    public boolean deleteById(ClientSession session, UUID id) {
//        boolean deleted = super.deleteById(session, id);
//        if (deleted) {
//            try {
//                redisClient.del(id.toString()); // Remove from cache
//            } catch (Exception e) {
//                System.err.println("Error removing client from Redis: " + e.getMessage());
//            }
//        }
//        return deleted;
//    }
//
//    @Override
//    public List<Client> findAll() {
//        return super.findAll().stream()
//                .peek(client -> {
//                    try {
//                        redisClient.set(client.getId().toString(), gson.toJson(client)); // Cache each client as JSON string
//                    } catch (Exception e) {
//                        System.err.println("Error caching client in Redis: " + e.getMessage());
//                    }
//                })
//                .collect(Collectors.toList());
//    }
//}