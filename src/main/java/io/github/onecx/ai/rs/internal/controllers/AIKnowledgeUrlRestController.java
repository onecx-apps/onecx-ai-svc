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

import gen.io.github.onecx.ai.rs.internal.AiKnowledgeUrlInternalApi;
import gen.io.github.onecx.ai.rs.internal.model.CreateAIKnowledgeUrlRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.ProblemDetailResponseDTO;
import gen.io.github.onecx.ai.rs.internal.model.UpdateAIKnowledgeUrlRequestDTO;
import io.github.onecx.ai.domain.daos.AIContextDAO;
import io.github.onecx.ai.domain.daos.AIKnowledgeUrlDAO;
import io.github.onecx.ai.domain.models.AIKnowledgeUrl;
import io.github.onecx.ai.rs.internal.mappers.AIKnowledgeUrlMapper;
import io.github.onecx.ai.rs.internal.mappers.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class AIKnowledgeUrlRestController implements AiKnowledgeUrlInternalApi {

    @Inject
    AIKnowledgeUrlDAO dao;

    @Inject
    AIContextDAO aiContextDAO;

    @Inject
    ExceptionMapper exceptionMapper;

    @Context
    UriInfo uriInfo;

    @Inject
    AIKnowledgeUrlMapper mapper;

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @Override
    public Response createKnowledgeUrl(String id, CreateAIKnowledgeUrlRequestDTO createAIKnowledgeUrlRequestDTO) {
        //check if ai-context exists
        var context = aiContextDAO.findById(id);
        if (context == null) {
            throw new ConstraintException("AIContext does not exist",
                    AIKonwledgeUrlErrorKeys.AI_CONTEXT_DOES_NOT_EXIST, null);
        }

        var aiKnowledgeUrl = mapper.createKnowledgeUrl(createAIKnowledgeUrlRequestDTO);
        aiKnowledgeUrl.setAiContext(context);
        aiKnowledgeUrl = dao.create(aiKnowledgeUrl);

        //update AIContext urls list
        context.getAiKnowledgeUrls().add(aiKnowledgeUrl);
        aiContextDAO.update(context);

        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(aiKnowledgeUrl.getId()).build())
                .entity(mapper.map(aiKnowledgeUrl))
                .build();
    }

    @Override
    public Response deleteKnowledgeUrl(String id) {
        dao.deleteQueryById(id);
        return Response.noContent().build();
    }

    @Override
    public Response getAIKnowledgeUrl(String id) {
        var item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(item)).build();
    }

    @Override
    public Response getAIKnowledgeUrlsByContextId(String id) {
        //check if ai-context exists
        var context = aiContextDAO.findById(id);
        if (context == null) {
            throw new ConstraintException("AIContext does not exist",
                    AIKonwledgeUrlErrorKeys.AI_CONTEXT_DOES_NOT_EXIST, null);
        }
        var aiKnowledgeUrls = context.getAiKnowledgeUrls();
        List<AIKnowledgeUrl> knowledgeUrlList = new ArrayList<>(aiKnowledgeUrls);
        return Response.ok(mapper.mapUrlList(knowledgeUrlList)).build();
    }

    @Override
    public Response updateKnowledgeUrl(String id, UpdateAIKnowledgeUrlRequestDTO updateAIKnowledgeUrlRequestDTO) {
        //check if knowledge-url exists
        var aiKnowledgeUrl = dao.findById(id);
        if (aiKnowledgeUrl == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.updateKnowledgeUrl(updateAIKnowledgeUrlRequestDTO, aiKnowledgeUrl);
        dao.update(aiKnowledgeUrl);
        return Response.noContent().build();
    }

    enum AIKonwledgeUrlErrorKeys {
        AI_CONTEXT_DOES_NOT_EXIST
    }
}
