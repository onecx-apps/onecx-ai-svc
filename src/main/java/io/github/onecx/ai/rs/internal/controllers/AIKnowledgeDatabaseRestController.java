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

import gen.io.github.onecx.ai.rs.internal.AiKnowledgeDatabaseInternalApi;
import gen.io.github.onecx.ai.rs.internal.model.CreateAIKnowledgeDatabaseRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.ProblemDetailResponseDTO;
import gen.io.github.onecx.ai.rs.internal.model.UpdateAIKnowledgeDatabaseRequestDTO;
import io.github.onecx.ai.domain.daos.AIContextDAO;
import io.github.onecx.ai.domain.daos.AIKnowledgeDatabaseDAO;
import io.github.onecx.ai.domain.models.AIKnowledgeDatabase;
import io.github.onecx.ai.rs.internal.mappers.AIKnowledgeDatabaseMapper;
import io.github.onecx.ai.rs.internal.mappers.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class AIKnowledgeDatabaseRestController implements AiKnowledgeDatabaseInternalApi {

    @Inject
    AIKnowledgeDatabaseDAO dao;

    @Inject
    AIContextDAO aiContextDAO;

    @Inject
    ExceptionMapper exceptionMapper;

    @Context
    UriInfo uriInfo;

    @Inject
    AIKnowledgeDatabaseMapper mapper;

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @Override
    public Response createKnowledgeDb(String id, CreateAIKnowledgeDatabaseRequestDTO createAIKnowledgeDatabaseRequestDTO) {
        //check if ai-context exists
        var context = aiContextDAO.findById(id);
        if (context == null) {
            throw new ConstraintException("AIContext does not exist",
                    AIKonwledgeDatabaseErrorKeys.AI_CONTEXT_DOES_NOT_EXIST, null);
        }

        var aiKnowledgeDatabase = mapper.createKnowledgeDatabase(createAIKnowledgeDatabaseRequestDTO);
        aiKnowledgeDatabase.setAiContext(context);
        aiKnowledgeDatabase = dao.create(aiKnowledgeDatabase);

        //update AIContext urls list
        context.getAiKnowledgeDbs().add(aiKnowledgeDatabase);
        aiContextDAO.update(context);

        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(aiKnowledgeDatabase.getId()).build())
                .entity(mapper.map(aiKnowledgeDatabase))
                .build();
    }

    @Override
    public Response deleteKnowledgeDb(String id) {
        dao.deleteQueryById(id);
        return Response.noContent().build();
    }

    @Override
    public Response getAIKnowledgeDatabase(String id) {
        var item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(item)).build();
    }

    @Override
    public Response getAIKnowledgeDatabasesByContextId(String id) {
        //check if ai-context exists
        var context = aiContextDAO.findById(id);
        if (context == null) {
            throw new ConstraintException("AIContext does not exist",
                    AIKonwledgeDatabaseErrorKeys.AI_CONTEXT_DOES_NOT_EXIST, null);
        }
        var aiKnowledgeDatabases = context.getAiKnowledgeDbs();
        List<AIKnowledgeDatabase> knowledgeDatabaseList = new ArrayList<>(aiKnowledgeDatabases);
        return Response.ok(mapper.mapDatabaseList(knowledgeDatabaseList)).build();
    }

    @Override
    public Response updateKnowledgeDb(String id, UpdateAIKnowledgeDatabaseRequestDTO updateAIKnowledgeDatabaseRequestDTO) {
        //check if knowledge database exists
        var aiKnowledgeDatabase = dao.findById(id);
        if (aiKnowledgeDatabase == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.updateKnowledgeDatabase(updateAIKnowledgeDatabaseRequestDTO, aiKnowledgeDatabase);
        dao.update(aiKnowledgeDatabase);
        return Response.noContent().build();
    }

    enum AIKonwledgeDatabaseErrorKeys {
        AI_CONTEXT_DOES_NOT_EXIST
    }
}
