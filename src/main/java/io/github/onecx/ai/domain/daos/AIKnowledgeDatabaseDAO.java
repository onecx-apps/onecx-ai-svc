package io.github.onecx.ai.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;

import io.github.onecx.ai.domain.models.AIKnowledgeDatabase;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class AIKnowledgeDatabaseDAO extends AbstractDAO<AIKnowledgeDatabase> {

    public enum ErrorKeys {

        ERROR_CREATE_KNOWLEDGE_DB,
    }
}
