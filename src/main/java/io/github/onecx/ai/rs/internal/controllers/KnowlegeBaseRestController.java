package io.github.onecx.ai.rs.internal.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import java.util.List;

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
import gen.io.github.onecx.ai.rs.internal.model.*;
import io.github.onecx.ai.domain.daos.AIKnowledgeBaseDAO;
import io.github.onecx.ai.domain.daos.AIKnowledgeDocumentDAO;
import io.github.onecx.ai.domain.models.AIKnowledgeBase;
import io.github.onecx.ai.domain.models.AIKnowledgeDocument;
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
    AIKnowledgeDocumentDAO aiKnowledgeDocumentDAO;

    @Inject
    AIKnowledgeBaseDAO aiKnowledgeBaseDAO;

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    KnowledgeBaseMapper knowledgeBaseMapper;

    @Context
    UriInfo uriInfo;

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    enum ChatErrorKeys {
        CHAT_DOES_NOT_EXIST
    }

    @Override
    public Response addAIProvider(String aiContextId, String aiProviderId, @Valid AIProviderDTO aiProviderDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addAIProvider'");
    }

    @Override
    public Response createAIContext(String aiKnowledgebaseId, @Valid @NotNull AIContextDTO aiContextDTO) {

        return Response.ok().build();
    }

    @Override
    public Response createAIKnowledgeBase(@Valid @NotNull AIKnowledgeBaseDTO aiKnowledgeBaseDTO) {
        AIKnowledgeBase aiKnowledgeBase = knowledgeBaseMapper.createAIKnowledgeBase(aiKnowledgeBaseDTO);

        aiKnowledgeBase.setTenantId("123");
        aiKnowledgeBaseDAO.create(aiKnowledgeBase);
        return Response.ok().build();
    }

    @Override
    public Response createAIProvider(@Valid @NotNull AIProviderDTO aiProviderDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAIProvider'");
    }

    @Override
    public Response createKnowledgeDb(String aiContextId,
            @Valid @NotNull AIKnowledgeDatabaseDTO aiKnowledgeDatabaseDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createKnowledgeDb'");
    }

    @Override
    public Response createKnowledgeDocument(String aiContextId,
            @Valid @NotNull AIKnowledgeDocumentDTO aiKnowledgeDocumentDTO) {
        AIKnowledgeDocument aiKnowledgeDocument = knowledgeBaseMapper.createAIKnowledgeDocument(aiKnowledgeDocumentDTO);
        aiKnowledgeDocument.setTenantId("testTenant");
        //aiKnowledgeDocumentDAO.create(aiKnowledgeDocument);
        return Response.ok(aiKnowledgeDocument).build();
    }

    @Override
    public Response createKnowledgeUrl(String aiContextId, @Valid @NotNull AIKnowledgeUrlDTO aiKnowledgeUrlDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createKnowledgeUrl'");
    }

    @Override
    public Response createKnowledgeVectorDb(String aiContextId,
            @Valid @NotNull AIKnowledgeVectorDbDTO aiKnowledgeVectorDbDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createKnowledgeVectorDb'");
    }

    @Override
    public Response deleteAIContext(String aiContextId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAIContext'");
    }

    @Override
    public Response deleteAIKnowledgeBase(String aiKnowledgebaseId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAIKnowledgeBase'");
    }

    @Override
    public Response deleteAIProvider(String aiProviderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAIProvider'");
    }

    @Override
    public Response deleteKnowledgeDb(String aiKnowledgeDatabaseId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteKnowledgeDb'");
    }

    @Override
    public Response deleteKnowledgeDocument(String aiKnowledgeDocumentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteKnowledgeDocument'");
    }

    @Override
    public Response deleteKnowledgeUrl(String aiKnowledgeUrlId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteKnowledgeUrl'");
    }

    @Override
    public Response deleteKnowledgeVectorDb(String aiKnowledgeVdbId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteKnowledgeVectorDb'");
    }

    @Override
    public Response embeddKnowledgeDocuments(String aiContextId,
            @Valid @NotNull List<@Valid AIKnowledgeDocumentDTO> aiKnowledgeDocumentDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'embeddKnowledgeDocuments'");
    }

    @Override
    public Response findAIContextBySearchCriteria(
            @Valid @NotNull AIContextSearchCriteriaDTO aiContextSearchCriteriaDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAIContextBySearchCriteria'");
    }

    @Override
    public Response findAIKnowlegeBaseBySearchCriteria(
            @Valid @NotNull AIKnowledgeBaseSearchCriteriaDTO aiKnowledgeBaseSearchCriteriaDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAIKnowlegeBaseBySearchCriteria'");
    }

    @Override
    public Response findAIProviderBySearchCriteria(
            @Valid @NotNull AIProviderSearchCriteriaDTO aiProviderSearchCriteriaDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAIProviderBySearchCriteria'");
    }

    @Override
    public Response removeAIProvider(String aiContextId, String aiProviderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeAIProvider'");
    }

    @Override
    public Response updateAIContext(String aiContextId, @Valid @NotNull AIContextDTO aiContextDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAIContext'");
    }

    @Override
    public Response updateAIKnowledgeBase(String aiKnowledgebaseId,
            @Valid @NotNull AIKnowledgeBaseDTO aiKnowledgeBaseDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAIKnowledgeBase'");
    }

    @Override
    public Response updateAIProvider(String aiProviderId, @Valid @NotNull AIProviderDTO aiProviderDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAIProvider'");
    }

    @Override
    public Response updateKnowledgeDb(String aiKnowledgeDatabaseId,
            @Valid @NotNull AIKnowledgeDatabaseDTO aiKnowledgeDatabaseDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateKnowledgeDb'");
    }

    @Override
    public Response updateKnowledgeDocument(String aiKnowledgeDocumentId,
            @Valid @NotNull AIKnowledgeDocumentDTO aiKnowledgeDocumentDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateKnowledgeDocument'");
    }

    @Override
    public Response updateKnowledgeUrl(String aiKnowledgeUrlId, @Valid @NotNull AIKnowledgeUrlDTO aiKnowledgeUrlDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateKnowledgeUrl'");
    }

    @Override
    public Response updateKnowledgeVectorDb(String aiKnowledgeVdbId,
            @Valid @NotNull AIKnowledgeVectorDbDTO aiKnowledgeVectorDbDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateKnowledgeVectorDb'");
    }

    @Override
    public Response uploadKnowledgeDocuments(String aiContextId,
            @Valid @NotNull UploadKnowledgeDocumentsRequestDTO uploadKnowledgeDocumentsRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'uploadKnowledgeDocuments'");
    }

}
