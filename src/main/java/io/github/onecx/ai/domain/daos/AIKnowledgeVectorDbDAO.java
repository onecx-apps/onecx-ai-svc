package io.github.onecx.ai.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;

import io.github.onecx.ai.domain.models.AIKnowledgeVectorDb;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class AIKnowledgeVectorDbDAO extends AbstractDAO<AIKnowledgeVectorDb> {

    public enum ErrorKeys {

        ERROR_CREATE_KNOWLEDGE_VECTOR_DB,
    }
}
