package edu.kdmk.managers;

import edu.kdmk.model.Client;
import edu.kdmk.repositories.EntityRepository;
import edu.kdmk.repositories.implemntations.ClientRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ClientManager {

    private final EntityManagerFactory emf;
    private EntityRepository<Client> clientRepository;

    public Client addClient(Client client) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            clientRepository.add(client, em);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        return client;
    }

    public boolean removeClient(Client client) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Client clientToRemove = clientRepository.getById(client.getId(), em);
            clientRepository.remove(clientToRemove, em);
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
