package io.github.onecx.ai.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.TraceableEntity_;

import io.github.onecx.ai.domain.models.AIKnowledgeUrl;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class AIKnowledgeUrlDAO extends AbstractDAO<AIKnowledgeUrl> {

    // https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830
    @Override
    public AIKnowledgeUrl findById(Object id) throws DAOException {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(AIKnowledgeUrl.class);
            var root = cq.from(AIKnowledgeUrl.class);
            cq.where(cb.equal(root.get(TraceableEntity_.ID), id));

            EntityGraph graph = this.em.getEntityGraph(AIKnowledgeUrl.AI_KNOWLEDGE_URL_LOAD);

            return this.getEntityManager().createQuery(cq).setHint(HINT_LOAD_GRAPH, graph).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            throw new DAOException(AIKnowledgeUrlDAO.ErrorKeys.FIND_ENTITY_BY_ID_FAILED, e, entityName, id);
        }
    }

    public enum ErrorKeys {
        FIND_ENTITY_BY_ID_FAILED
    }
}
