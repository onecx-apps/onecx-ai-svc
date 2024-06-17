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

import gen.io.github.onecx.ai.rs.internal.AiProviderInternalApi;
import gen.io.github.onecx.ai.rs.internal.model.AIProviderSearchCriteriaDTO;
import gen.io.github.onecx.ai.rs.internal.model.CreateAIProviderRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.ProblemDetailResponseDTO;
import gen.io.github.onecx.ai.rs.internal.model.UpdateAIProviderRequestDTO;
import io.github.onecx.ai.domain.daos.AIProviderDAO;
import io.github.onecx.ai.rs.internal.mappers.AIProviderMapper;
import io.github.onecx.ai.rs.internal.mappers.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class AIProviderRestController implements AiProviderInternalApi {

    @Inject
    AIProviderDAO dao;

    @Inject
    ExceptionMapper exceptionMapper;

    @Context
    UriInfo uriInfo;

    @Inject
    AIProviderMapper mapper;

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @Override
    public Response createAIProvider(CreateAIProviderRequestDTO aiProviderDTO) {

        var provider = mapper.createAIProvider(aiProviderDTO);
        provider = dao.create(provider);

        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(provider.getId()).build())
                .entity(mapper.map(provider))
                .build();

    }

    @Override
    public Response deleteAIProvider(String aiProviderId) {
        dao.deleteQueryById(aiProviderId);
        return Response.noContent().build();
    }

    @Override
    public Response findAIProviderBySearchCriteria(
            @Valid @NotNull AIProviderSearchCriteriaDTO aiProviderSearchCriteriaDTO) {
        var criteria = mapper.map(aiProviderSearchCriteriaDTO);
        var result = dao.findAIProvidersByCriteria(criteria);
        return Response.ok(mapper.mapPageResult(result)).build();
    }

    @Override
    public Response updateAIProvider(String aiProviderId, UpdateAIProviderRequestDTO aiProviderDTO) {
        var provider = dao.findById(aiProviderId);
        if (provider == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.update(aiProviderDTO, provider);
        dao.update(provider);
        return Response.noContent().build();
    }

    @Override
    public Response getAIProvider(String id) {
        var provider = dao.findById(id);
        if (provider == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(provider)).build();
    }

}
