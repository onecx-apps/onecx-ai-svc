package io.github.onecx.ai.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.TraceableEntity_;
import org.tkit.quarkus.jpa.utils.QueryCriteriaUtil;

import io.github.onecx.ai.domain.criteria.AIContextSearchCriteria;
import io.github.onecx.ai.domain.models.AIContext;
import io.github.onecx.ai.domain.models.AIContext_;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class AIContextDAO extends AbstractDAO<AIContext> {

    // https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830
    @Override
    public AIContext findById(Object id) throws DAOException {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(AIContext.class);
            var root = cq.from(AIContext.class);
            cq.where(cb.equal(root.get(TraceableEntity_.ID), id));

            EntityGraph graph = this.em.getEntityGraph(AIContext.AI_CONTEXT_LOAD);

            return this.getEntityManager().createQuery(cq).setHint(HINT_LOAD_GRAPH, graph).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            throw new DAOException(ErrorKeys.FIND_ENTITY_BY_ID_FAILED, e, entityName, id);
        }
    }

    public PageResult<AIContext> findAIContextsByCriteria(AIContextSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(AIContext.class);
            var root = cq.from(AIContext.class);

            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                cq.where(cb.like(root.get(AIContext_.name), QueryCriteriaUtil.wildcard(criteria.getName())));
            }

            if (criteria.getDescription() != null && !criteria.getDescription().isBlank()) {
                cq.where(cb.like(root.get(AIContext_.description), QueryCriteriaUtil.wildcard(criteria.getDescription())));
            }

            if (criteria.getTenandId() != null) {
                cq.where(cb.equal(root.get(AIContext_.tenantId), criteria.getTenandId()));
            }

            if (criteria.getAppId() != null) {
                cq.where(cb.equal(root.get(AIContext_.appId), criteria.getAppId()));
            }

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_CONTEXTS_BY_CRITERIA, ex);
        }
    }

    public PageResult<AIContext> findAll(Integer pageNumber, Integer pageSize) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(AIContext.class);
            cq.from(AIContext.class);
            return createPageQuery(cq, Page.of(pageNumber, pageSize)).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_ALL_CONTEXTS_PAGE, ex);
        }
    }

    public enum ErrorKeys {

        FIND_ENTITY_BY_ID_FAILED,
        ERROR_FIND_CONTEXTS_BY_CRITERIA,
        ERROR_FIND_ALL_CONTEXTS_PAGE,
    }
}
