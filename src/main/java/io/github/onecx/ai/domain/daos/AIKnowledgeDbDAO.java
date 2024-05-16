package io.github.onecx.ai.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;

import io.github.onecx.ai.domain.models.AIKnowledgeDb;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class AIKnowledgeDbDAO extends AbstractDAO<AIKnowledgeDb> {

    public enum ErrorKeys {

        ERROR_CREATE_KNOWLEDGE_DB,
    }
}
