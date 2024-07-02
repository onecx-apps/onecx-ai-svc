package io.github.onecx.ai.rs.internal.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;

import gen.io.github.onecx.ai.rs.internal.AiContextInternalApi;
import gen.io.github.onecx.ai.rs.internal.model.AIContextSearchCriteriaDTO;
import gen.io.github.onecx.ai.rs.internal.model.CreateAIContextRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.ProblemDetailResponseDTO;
import gen.io.github.onecx.ai.rs.internal.model.UpdateAIContextRequestDTO;
import io.github.onecx.ai.domain.daos.AIContextDAO;
import io.github.onecx.ai.domain.daos.AIKnowledgeBaseDAO;
import io.github.onecx.ai.domain.models.AIContext;
import io.github.onecx.ai.rs.internal.mappers.AIContextMapper;
import io.github.onecx.ai.rs.internal.mappers.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class AIContextRestController implements AiContextInternalApi {

    @Inject
    AIKnowledgeBaseDAO aiKnowledgeBaseDAO;

    @Inject
    AIContextDAO dao;

    @Inject
    ExceptionMapper exceptionMapper;

    @Context
    UriInfo uriInfo;

    @Inject
    AIContextMapper mapper;

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @Override
    public Response createAIContext(String id, CreateAIContextRequestDTO createAIContextRequestDTO) {
        //check if aiKnowledgeBase exists
        var kb = aiKnowledgeBaseDAO.findById(id);
        if (kb == null) {
            throw new ConstraintException("AIKnowledgeBase does not exist",
                    AIContextRestController.KnowledgeBaseErrorKeys.KNOWLEDGE_BASE_DOES_NOT_EXIST, null);
        }
        //create AIContext
        var aiContext = mapper.createAIContext(createAIContextRequestDTO);
        aiContext.setKnowledgebase(kb);
        aiContext = dao.create(aiContext);

        //update context list in knowledgebase
        kb.getContexts().add(aiContext);
        aiKnowledgeBaseDAO.update(kb);

        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(aiContext.getId()).build())
                .entity(mapper.map(aiContext))
                .build();
    }

    @Override
    public Response deleteAIContext(String id) {
        dao.deleteQueryById(id);
        return Response.noContent().build();
    }

    @Override
    public Response findAIContextBySearchCriteria(AIContextSearchCriteriaDTO aiContextSearchCriteriaDTO) {
        var criteria = mapper.map(aiContextSearchCriteriaDTO);
        var result = dao.findAIContextsByCriteria(criteria);
        return Response.ok(mapper.mapPage(result)).build();
    }

    @Override
    public Response getAIContext(String id) {
        var item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(item)).build();
    }

    @Override
    public Response getAIContextsByKnowledgeBaseId(String id) {
        //check if ai-knowledgebase exists
        var kb = aiKnowledgeBaseDAO.findById(id);
        if (kb == null) {
            throw new ConstraintException("AIKnowledgeBase does not exist",
                    KnowledgeBaseErrorKeys.KNOWLEDGE_BASE_DOES_NOT_EXIST, null);
        }

        var aiContexts = kb.getContexts();
        List<AIContext> contextList = new ArrayList<>(aiContexts);
        return Response.ok(mapper.mapContextList(contextList)).build();
    }

    @Override
    public Response updateAIContext(String id, UpdateAIContextRequestDTO updateAIContextRequestDTO) {
        //check if context exists
        var aiContext = dao.findById(id);
        if (aiContext == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.updateAIContext(updateAIContextRequestDTO, aiContext);
        dao.update(aiContext);
        return Response.noContent().build();
    }

    enum KnowledgeBaseErrorKeys {
        KNOWLEDGE_BASE_DOES_NOT_EXIST
    }
}
