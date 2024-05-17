package io.github.onecx.ai.rs.internal.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import java.util.ArrayList;
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

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;

import gen.io.github.onecx.ai.clients.model.ChatMessage;
import gen.io.github.onecx.ai.clients.model.ChatRequest;
import gen.io.github.onecx.ai.clients.model.Conversation;
import gen.io.github.onecx.ai.rs.internal.AiKnowledgeBaseInternalApi
import gen.io.github.onecx.ai.rs.internal.model.*;
import io.github.onecx.chat.domain.daos.ChatDAO;
import io.github.onecx.chat.domain.daos.MessageDAO;
import io.github.onecx.chat.domain.daos.ParticipantDAO;
import io.github.onecx.chat.domain.models.Chat.ChatType;
import io.github.onecx.chat.domain.models.Message;
import io.github.onecx.chat.domain.models.Participant;
import io.github.onecx.chat.rs.internal.mappers.ChatMapper;
import io.github.onecx.ai.domain.daos.AIKnowledgeBaseDAO;
import io.github.onecx.ai.rs.internal.mappers.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class KnowlegeBaseRestController implements AiKnowledgeBaseInternalApi {

    @Inject
    AIKnowledgeBaseDAO dao;

    @Inject
    ParticipantDAO participantDao;

    @Inject
    MessageDAO msgDao;

    @Inject
    ChatMapper mapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Context
    UriInfo uriInfo;

    @Override
    @Transactional
    public Response createChat(CreateChatDTO createChatDTO) {

        var chat = mapper.create(createChatDTO);
        chat = dao.create(chat);

        if (createChatDTO.getParticipants() != null && !createChatDTO.getParticipants().isEmpty()) {
            var participants = mapper.mapParticipantDTOs(createChatDTO.getParticipants());
            for (Participant participant : participants) {
                participant.setChat(chat);
                participant = participantDao.create(participant);
            }

        }

        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(chat.getId()).build())
                .entity(mapper.mapChat(chat))
                .build();
    }

    @Override
    @Transactional
    public Response deleteChat(String id) {
        dao.deleteQueryById(id);
        return Response.noContent().build();
    }

    @Override
    public Response getChatById(String id) {
        var chat = dao.findById(id);
        if (chat == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.mapChat(chat)).build();
    }

    @Override
    public Response getChats(Integer pageNumber, Integer pageSize) {
        var items = dao.findAll(pageNumber, pageSize);
        return Response.ok(mapper.mapPage(items)).build();
    }

    @Override
    public Response searchChats(ChatSearchCriteriaDTO chatSearchCriteriaDTO) {
        var criteria = mapper.map(chatSearchCriteriaDTO);
        var result = dao.findChatsByCriteria(criteria);
        return Response.ok(mapper.mapPage(result)).build();
    }

    @Override
    @Transactional
    public Response updateChat(String id, UpdateChatDTO updateChatDTO) {

        var chat = dao.findById(id);
        if (chat == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.update(updateChatDTO, chat);
        dao.update(chat);
        return Response.noContent().build();
    }

    @Override
    @Transactional
    public Response createChatMessage(String chatId, CreateMessageDTO createMessageDTO) {

        var chat = dao.findById(chatId);

        if (chat == null) {
            throw new ConstraintException("Chat does not exist", ChatErrorKeys.CHAT_DOES_NOT_EXIST, null);
        }

        var message = mapper.createMessage(createMessageDTO);
        message.setChat(chat);
        message = msgDao.create(message);

        if (ChatType.AI_CHAT.equals(chat.getType())) {

            Conversation conversation = mapper.mapChat2Conversation(chat);
            ChatMessage chatMessage = mapper.mapMessage(message);

            ChatRequest chatRequest = new ChatRequest();
            chatRequest.chatMessage(chatMessage);
            chatRequest.conversation(conversation);
            Response response = aiChatClient.chat(chatRequest);

            var chatResponse = response.readEntity(ChatMessage.class);

            var responseMessage = mapper.mapAiSvcMessage(chatResponse);
            responseMessage.setChat(chat);
            msgDao.create(responseMessage);

        }

        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(message.getId()).build())
                .build();

    }

    @Override
    public Response getChatMessages(String chatId) {
        var chat = dao.findById(chatId);

        if (chat == null || chat.getMessages() == null) {
            // Handle the case where chat or its messages are null
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var messages = chat.getMessages();
        List<Message> messageList = new ArrayList<>(messages);
        return Response.ok(mapper.mapMessageList(messageList)).build();

    }

    @Override
    public Response addParticipant(String chatId, @Valid @NotNull AddParticipantDTO addParticipantDTO) {

        var chat = dao.findById(chatId);

        if (chat == null) {
            throw new ConstraintException("Chat does not exist", ChatErrorKeys.CHAT_DOES_NOT_EXIST, null);
        }

        var participant = mapper.addParticipant(addParticipantDTO);
        participant.setChat(chat);
        participant = participantDao.create(participant);

        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(participant.getId()).build())
                .build();

    }

    @Override
    public Response getChatParticipants(String chatId) {

        var chat = dao.findById(chatId);

        if (chat == null || chat.getParticipants() == null) {
            // Handle the case where chat or its messages are null
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var participants = chat.getParticipants();
        List<Participant> participantList = new ArrayList<>(participants);
        return Response.ok(mapper.mapParticipantList(participantList)).build();

    }

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAIContext'");
    }

    @Override
    public Response createAIKnowledgeBase(@Valid @NotNull AIKnowledgeBaseDTO aiKnowledgeBaseDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAIKnowledgeBase'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createKnowledgeDocument'");
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
            @NotNull AIKnowledgeBaseSearchCriteriaDTO aiKnowledgebaseSearchCriteria) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAIKnowlegeBaseBySearchCriteria'");
    }

    @Override
    public Response findAIProviderBySearchCriteria(@NotNull AIProviderSearchCriteriaDTO aiProviderSearchCriteria) {
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
