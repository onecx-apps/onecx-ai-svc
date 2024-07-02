package io.github.onecx.ai.rs.internal.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;

import gen.io.github.onecx.ai.rs.internal.AiKnowledgeBaseInternalApi;
import gen.io.github.onecx.ai.rs.internal.model.AIKnowledgeBaseSearchCriteriaDTO;
import gen.io.github.onecx.ai.rs.internal.model.AIProviderDTO;
import gen.io.github.onecx.ai.rs.internal.model.CreateAIKnowledgeBaseRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.ProblemDetailResponseDTO;
import gen.io.github.onecx.ai.rs.internal.model.UpdateAIKnowledgeBaseRequestDTO;
import io.github.onecx.ai.domain.daos.AIKnowledgeBaseDAO;
import io.github.onecx.ai.rs.internal.mappers.ExceptionMapper;
import io.github.onecx.ai.rs.internal.mappers.KnowledgeBaseMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class KnowlegeBaseRestController implements AiKnowledgeBaseInternalApi {

    @Inject
    AIKnowledgeBaseDAO dao;

    @Inject
    ExceptionMapper exceptionMapper;

    @Context
    UriInfo uriInfo;

    @Inject
    KnowledgeBaseMapper mapper;

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    enum KnowledgeBaseErrorKeys {
        KB_DOES_NOT_EXIST
    }

    @Override
    public Response addAIProvider(String aiContextId, String aiProviderId, @Valid AIProviderDTO aiProviderDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addAIProvider'");
    }

    @Override
    public Response createAIKnowledgeBase(CreateAIKnowledgeBaseRequestDTO aiKnowledgeBaseDTO) {

        var kb = mapper.createAIKnowledgeBase(aiKnowledgeBaseDTO);
        kb = dao.create(kb);

        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(kb.getId()).build())
                .entity(mapper.map(kb))
                .build();
    }

    @Override
    public Response getAIKnowledgeBase(String id) {
        var item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(item)).build();
    }

    @Override
    public Response deleteAIKnowledgeBase(String aiKnowledgebaseId) {
        dao.deleteQueryById(aiKnowledgebaseId);
        return Response.noContent().build();
    }

    @Override
    public Response findAIKnowlegeBaseBySearchCriteria(
            @Valid @NotNull AIKnowledgeBaseSearchCriteriaDTO aiKnowledgeBaseSearchCriteriaDTO) {
        var criteria = mapper.map(aiKnowledgeBaseSearchCriteriaDTO);
        var result = dao.findAIKnowledgeBasesByCriteria(criteria);
        return Response.ok(mapper.mapPage(result)).build();
    }

    @Override
    public Response removeAIProvider(String aiContextId, String aiProviderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeAIProvider'");
    }

    @Override
    public Response updateAIKnowledgeBase(String aiKnowledgebaseId,
            UpdateAIKnowledgeBaseRequestDTO aiKnowledgeBaseDTO) {
        //check if exists
        var kb = dao.findById(aiKnowledgebaseId);
        if (kb == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.updateAIKnowledgeBase(aiKnowledgeBaseDTO, kb);
        dao.update(kb);
        return Response.noContent().build();
    }

    @Override
    public Response getAIProviderOfAIContext(String id, String aiProviderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAIProviderOfAIContext'");
    }

}
