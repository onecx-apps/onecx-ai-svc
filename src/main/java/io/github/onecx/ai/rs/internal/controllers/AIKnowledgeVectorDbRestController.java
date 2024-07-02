package io.github.onecx.ai.rs.internal.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

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

import gen.io.github.onecx.ai.rs.internal.AiKnowledgeVectorDbInternalApi;
import gen.io.github.onecx.ai.rs.internal.model.CreateAIKnowledgeVectorDbRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.ProblemDetailResponseDTO;
import gen.io.github.onecx.ai.rs.internal.model.UpdateAIKnowledgeVectorDbRequestDTO;
import io.github.onecx.ai.domain.daos.AIContextDAO;
import io.github.onecx.ai.domain.daos.AIKnowledgeVectorDbDAO;
import io.github.onecx.ai.rs.internal.mappers.AIKnowledgeVectorDbMapper;
import io.github.onecx.ai.rs.internal.mappers.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class AIKnowledgeVectorDbRestController implements AiKnowledgeVectorDbInternalApi {

    @Inject
    AIKnowledgeVectorDbDAO dao;

    @Inject
    AIContextDAO aiContextDAO;

    @Inject
    ExceptionMapper exceptionMapper;

    @Context
    UriInfo uriInfo;

    @Inject
    AIKnowledgeVectorDbMapper mapper;

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @Override
    public Response createKnowledgeVectorDb(String id,
            CreateAIKnowledgeVectorDbRequestDTO createAIKnowledgeVectorDbRequestDTO) {

        var context = aiContextDAO.findById(id);
        if (context == null) {
            throw new ConstraintException("AIContext does not exist", AIKonwledgeVectorDbErrorKeys.AI_CONTEXT_DOES_NOT_EXIST,
                    null);
        }

        var aiKnowledgeVectorDb = mapper.createKnowledgeVectorDb(createAIKnowledgeVectorDbRequestDTO);
        aiKnowledgeVectorDb.setAiContext(context);
        context.setAiKnowledgeVectorDb(aiKnowledgeVectorDb);

        aiKnowledgeVectorDb = dao.create(aiKnowledgeVectorDb);
        aiContextDAO.update(context);

        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(aiKnowledgeVectorDb.getId()).build())
                .entity(mapper.map(aiKnowledgeVectorDb))
                .build();
    }

    @Override
    public Response deleteKnowledgeVectorDb(String id) {
        dao.deleteQueryById(id);
        return Response.noContent().build();
    }

    @Override
    public Response getAIKnowledgeVectorDb(String id) {
        var item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(item)).build();
    }

    @Override
    public Response getAIKnowledgeVectorDbByContextId(String id) {
        //check if ai-context exists
        var context = aiContextDAO.findById(id);
        if (context == null) {
            throw new ConstraintException("AIContext does not exist",
                    AIKonwledgeVectorDbErrorKeys.AI_CONTEXT_DOES_NOT_EXIST, null);
        }
        var aiKnowledgeVectorDb = context.getAiKnowledgeVectorDb();
        return Response.ok(mapper.map(aiKnowledgeVectorDb)).build();
    }

    @Override
    public Response updateKnowledgeVectorDb(String id,
            UpdateAIKnowledgeVectorDbRequestDTO updateAIKnowledgeVectorDbRequestDTO) {

        var aiKnowledgeVectorDb = dao.findById(id);
        if (aiKnowledgeVectorDb == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.updateKnowledgeVectorDb(updateAIKnowledgeVectorDbRequestDTO, aiKnowledgeVectorDb);
        dao.update(aiKnowledgeVectorDb);
        return Response.noContent().build();
    }

    enum AIKonwledgeVectorDbErrorKeys {
        AI_CONTEXT_DOES_NOT_EXIST
    }
}
