package io.github.onecx.ai.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;

import io.github.onecx.ai.domain.models.AIKnowledgeUrl;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class AIKnowledgeUrlDAO extends AbstractDAO<AIKnowledgeUrl> {

    public enum ErrorKeys {

        ERROR_CREATE_KNOWLEDGE_URL,
    }
}
