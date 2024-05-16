package io.github.onecx.ai.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;

import io.github.onecx.ai.domain.models.AIKnowledgeDocument;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class AIKnowledgeDocumentDAO extends AbstractDAO<AIKnowledgeDocument> {

    public enum ErrorKeys {

        ERROR_CREATE_KNOWLEDGE_DOCUMENT,
    }
}
