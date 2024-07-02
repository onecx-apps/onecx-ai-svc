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

import gen.io.github.onecx.ai.rs.internal.AiKnowledgeDocumentInternalApi;
import gen.io.github.onecx.ai.rs.internal.model.*;
import io.github.onecx.ai.domain.daos.AIContextDAO;
import io.github.onecx.ai.domain.daos.AIKnowledgeDocumentDAO;
import io.github.onecx.ai.domain.models.AIKnowledgeDocument;
import io.github.onecx.ai.rs.internal.mappers.AIKnowledgeDocumentMapper;
import io.github.onecx.ai.rs.internal.mappers.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class AIKnowledgeDocumentRestController implements AiKnowledgeDocumentInternalApi {

    @Inject
    AIKnowledgeDocumentDAO dao;

    @Inject
    AIContextDAO aiContextDAO;

    @Inject
    ExceptionMapper exceptionMapper;

    @Context
    UriInfo uriInfo;

    @Inject
    AIKnowledgeDocumentMapper mapper;

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @Override
    public Response createKnowledgeDocument(String id,
            CreateAIKnowledgeDocumentRequestDTO createAIKnowledgeDocumentRequestDTO) {
        //check if ai-context exists
        var context = aiContextDAO.findById(id);
        if (context == null) {
            throw new ConstraintException("AIContext does not exist",
                    AIKonwledgeDocumentErrorKeys.AI_CONTEXT_DOES_NOT_EXIST, null);
        }

        var aiKnowledgeDocument = mapper.createKnowledgeDocument(createAIKnowledgeDocumentRequestDTO);
        aiKnowledgeDocument.setAiContext(context);
        aiKnowledgeDocument = dao.create(aiKnowledgeDocument);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(aiKnowledgeDocument.getId()).build())
                .entity(mapper.map(aiKnowledgeDocument))
                .build();
    }

    @Override
    public Response deleteKnowledgeDocument(String id) {
        dao.deleteQueryById(id);
        return Response.noContent().build();
    }

    @Override
    public Response embeddKnowledgeDocuments(String id, List<AIKnowledgeDocumentDTO> aiKnowledgeDocumentDTO) {
        throw new UnsupportedOperationException("Unimplemented method 'embeddKnowledgeDocuments'");
    }

    @Override
    public Response getAIKnowledgeDocument(String id) {
        var item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(item)).build();
    }

    @Override
    public Response getAIKnowledgeDocumentsByContextId(String id) {
        //check if ai-context exists
        var context = aiContextDAO.findById(id);
        if (context == null) {
            throw new ConstraintException("AIContext does not exist",
                    AIKnowledgeUrlRestController.AIKonwledgeUrlErrorKeys.AI_CONTEXT_DOES_NOT_EXIST, null);
        }

        var aiKnowledgeDocuments = context.getAiKnowledgeDocuments();
        List<AIKnowledgeDocument> knowledgeDocumentList = new ArrayList<>(aiKnowledgeDocuments);
        return Response.ok(mapper.mapDocumentList(knowledgeDocumentList)).build();
    }

    @Override
    public Response updateKnowledgeDocument(String id,
            UpdateAIKnowledgeDocumentRequestDTO updateAIKnowledgeDocumentRequestDTO) {
        //check if knowledge document exists
        var aiKnowledgeDocument = dao.findById(id);
        if (aiKnowledgeDocument == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.updateKnowledgeDocument(updateAIKnowledgeDocumentRequestDTO, aiKnowledgeDocument);
        dao.update(aiKnowledgeDocument);
        return Response.noContent().build();
    }

    @Override
    public Response uploadKnowledgeDocuments(String id, UploadKnowledgeDocumentsRequestDTO uploadKnowledgeDocumentsRequestDTO) {
        return null;
    }

    enum AIKonwledgeDocumentErrorKeys {
        AI_CONTEXT_DOES_NOT_EXIST
    }
}
