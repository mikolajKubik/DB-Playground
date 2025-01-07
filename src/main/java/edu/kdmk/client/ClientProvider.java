package edu.kdmk.client;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import edu.kdmk.client.Client;

import java.util.UUID;

@Deprecated
public class ClientProvider {
    private final CqlSession session;

    private EntityHelper<Client> clientEntityHelper;

    public ClientProvider(CqlSession session, EntityHelper<Client> clientEntityHelper) {
        this.session = session;
        this.clientEntityHelper = clientEntityHelper;
    }

    public Client findByUuid(UUID id) {
        return null;
    }

    public void create(Client client) {

    }
}
