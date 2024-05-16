package io.github.onecx.ai.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.TraceableEntity_;
import org.tkit.quarkus.jpa.utils.QueryCriteriaUtil;

import io.github.onecx.ai.domain.criteria.AIKnowledgeBaseSearchCriteria;
import io.github.onecx.ai.domain.models.AIKnowledgeBase;
import io.github.onecx.ai.domain.models.AIKnowledgeBase_;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class AIKnowledgeBaseDAO extends AbstractDAO<AIKnowledgeBase> {

    // https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830
    @Override
    public AIKnowledgeBase findById(Object id) throws DAOException {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(AIKnowledgeBase.class);
            var root = cq.from(AIKnowledgeBase.class);
            cq.where(cb.equal(root.get(TraceableEntity_.ID), id));

            EntityGraph graph = this.em.getEntityGraph(AIKnowledgeBase.AI_KNOWLEDGEBASE_LOAD);

            return this.getEntityManager().createQuery(cq).setHint(HINT_LOAD_GRAPH, graph).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            throw new DAOException(ErrorKeys.FIND_ENTITY_BY_ID_FAILED, e, entityName, id);
        }
    }

    public PageResult<AIKnowledgeBase> findAIKnowledgeBasesByCriteria(AIKnowledgeBaseSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(AIKnowledgeBase.class);
            var root = cq.from(AIKnowledgeBase.class);

            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                cq.where(cb.like(root.get(AIKnowledgeBase_.name), QueryCriteriaUtil.wildcard(criteria.getName())));
            }


            if (criteria.getDescription() != null && !criteria.getDescription().isBlank()) {
                cq.where(cb.like(root.get(AIKnowledgeBase_.description), QueryCriteriaUtil.wildcard(criteria.getDescription())));
            }

            if (criteria.getTenandId() != null) {
                cq.where(cb.equal(root.get(AIKnowledgeBase_.tenantId), criteria.getTenandId()));
            }

            if (criteria.getAppId() != null) {
                cq.where(cb.equal(root.get(AIKnowledgeBase_.appId), criteria.getAppId()));
            }

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_KBS_BY_CRITERIA, ex);
        }
    }

    public PageResult<AIKnowledgeBase> findAll(Integer pageNumber, Integer pageSize) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(AIKnowledgeBase.class);
            cq.from(AIKnowledgeBase.class);
            return createPageQuery(cq, Page.of(pageNumber, pageSize)).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_ALL_KB_PAGE, ex);
        }
    }

    public enum ErrorKeys {

        FIND_ENTITY_BY_ID_FAILED,
        ERROR_FIND_KBS_BY_CRITERIA,
        ERROR_FIND_ALL_KB_PAGE,
    }
}
