package edu.kdmk.managers;

import edu.kdmk.model.Client;
import edu.kdmk.repositories.EntityRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ClientManager {

    private final EntityManagerFactory emf;
    private EntityRepository<Client> clientRepository;

    public Client addClient(Client client) {
        var em = emf.createEntityManager();
        Client result;
        try {
            em.getTransaction().begin();
            result = clientRepository.add(client, em);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        return result;
    }

    public boolean removeClient(long id) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Client client = clientRepository.getById(id, em);
            if (client == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException();
            }
            clientRepository.remove(client, em);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        return true;
    }

    public Client updateClient(Client client) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (client == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException();
            }
            clientRepository.update(client, em);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
        return client;
    }

    public Client getClientById(Long id) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Client client =  clientRepository.getById(id, em);
            em.getTransaction().commit();
            return client;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Client> getAllClients() {
        var em = emf.createEntityManager();
        try {
            return clientRepository.getAll(em);
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
