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
    public Response internalAiAiContextsAiContextIdAiKnowledgeDatabasesPost(String aiContextId,
            @Valid @NotNull AIKnowledgeDatabaseDTO aiKnowledgeDatabaseDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiContextsAiContextIdAiKnowledgeDatabasesPost'");
    }

    @Override
    public Response internalAiAiContextsAiContextIdAiKnowledgeDocumentsPost(String aiContextId,
            @Valid @NotNull AIKnowledgeDocumentDTO aiKnowledgeDocumentDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiContextsAiContextIdAiKnowledgeDocumentsPost'");
    }

    @Override
    public Response internalAiAiContextsAiContextIdAiKnowledgeDocumentsPut(String aiContextId,
            @Valid @NotNull List<@Valid AIKnowledgeDocumentDTO> aiKnowledgeDocumentDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiContextsAiContextIdAiKnowledgeDocumentsPut'");
    }

    @Override
    public Response internalAiAiContextsAiContextIdAiKnowledgeUrlsPost(String aiContextId,
            @Valid @NotNull AIKnowledgeUrlDTO aiKnowledgeUrlDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiContextsAiContextIdAiKnowledgeUrlsPost'");
    }

    @Override
    public Response internalAiAiContextsAiContextIdAiKnowledgeVdbPost(String aiContextId,
            @Valid @NotNull AIKnowledgeVectorDbDTO aiKnowledgeVectorDbDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiContextsAiContextIdAiKnowledgeVdbPost'");
    }

    @Override
    public Response internalAiAiContextsAiContextIdAiProvidersAiProviderIdDelete(String aiContextId,
            String aiProviderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiContextsAiContextIdAiProvidersAiProviderIdDelete'");
    }

    @Override
    public Response internalAiAiContextsAiContextIdAiProvidersAiProviderIdPut(String aiContextId, String aiProviderId,
            @Valid AIProviderDTO aiProviderDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiContextsAiContextIdAiProvidersAiProviderIdPut'");
    }

    @Override
    public Response internalAiAiContextsAiContextIdDelete(String aiContextId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiContextsAiContextIdDelete'");
    }

    @Override
    public Response internalAiAiContextsAiContextIdDocumentsPost(String aiContextId,
            @Valid @NotNull InternalAiAiContextsAiContextIdDocumentsPostRequestDTO internalAiAiContextsAiContextIdDocumentsPostRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiContextsAiContextIdDocumentsPost'");
    }

    @Override
    public Response internalAiAiContextsAiContextIdPut(String aiContextId, @Valid @NotNull AIContextDTO aiContextDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiContextsAiContextIdPut'");
    }

    @Override
    public Response internalAiAiContextsSearchPost(
            @Valid @NotNull AIContextSearchCriteriaDTO aiContextSearchCriteriaDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiContextsSearchPost'");
    }

    @Override
    public Response internalAiAiKnowledgeDatabasesAiKnowledgeDatabaseIdDelete(String aiKnowledgeDatabaseId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiKnowledgeDatabasesAiKnowledgeDatabaseIdDelete'");
    }

    @Override
    public Response internalAiAiKnowledgeDatabasesAiKnowledgeDatabaseIdPut(String aiKnowledgeDatabaseId,
            @Valid @NotNull AIKnowledgeDatabaseDTO aiKnowledgeDatabaseDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiKnowledgeDatabasesAiKnowledgeDatabaseIdPut'");
    }

    @Override
    public Response internalAiAiKnowledgeDocumentsAiKnowledgeDocumentIdDelete(String aiKnowledgeDocumentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiKnowledgeDocumentsAiKnowledgeDocumentIdDelete'");
    }

    @Override
    public Response internalAiAiKnowledgeDocumentsAiKnowledgeDocumentIdPut(String aiKnowledgeDocumentId,
            @Valid @NotNull AIKnowledgeDocumentDTO aiKnowledgeDocumentDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiKnowledgeDocumentsAiKnowledgeDocumentIdPut'");
    }

    @Override
    public Response internalAiAiKnowledgeUrlsAiKnowledgeUrlIdDelete(String aiKnowledgeUrlId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiKnowledgeUrlsAiKnowledgeUrlIdDelete'");
    }

    @Override
    public Response internalAiAiKnowledgeUrlsAiKnowledgeUrlIdPut(String aiKnowledgeUrlId,
            @Valid @NotNull AIKnowledgeUrlDTO aiKnowledgeUrlDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiKnowledgeUrlsAiKnowledgeUrlIdPut'");
    }

    @Override
    public Response internalAiAiKnowledgeVdbsAiKnowledgeVdbIdDelete(String aiKnowledgeVdbId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiKnowledgeVdbsAiKnowledgeVdbIdDelete'");
    }

    @Override
    public Response internalAiAiKnowledgeVdbsAiKnowledgeVdbIdPut(String aiKnowledgeVdbId,
            @Valid @NotNull AIKnowledgeVectorDbDTO aiKnowledgeVectorDbDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiKnowledgeVdbsAiKnowledgeVdbIdPut'");
    }

    @Override
    public Response internalAiAiKnowledgebasesAiKnowledgebaseIdAiContextsPost(String aiKnowledgebaseId,
            @Valid @NotNull AIContextDTO aiContextDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiKnowledgebasesAiKnowledgebaseIdAiContextsPost'");
    }

    @Override
    public Response internalAiAiKnowledgebasesAiKnowledgebaseIdDelete(String aiKnowledgebaseId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiKnowledgebasesAiKnowledgebaseIdDelete'");
    }

    @Override
    public Response internalAiAiKnowledgebasesAiKnowledgebaseIdPut(String aiKnowledgebaseId,
            @Valid @NotNull AIKnowledgeBaseDTO aiKnowledgeBaseDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiKnowledgebasesAiKnowledgebaseIdPut'");
    }

    @Override
    public Response internalAiAiKnowledgebasesPost(@Valid @NotNull AIKnowledgeBaseDTO aiKnowledgeBaseDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiKnowledgebasesPost'");
    }

    @Override
    public Response internalAiAiKnowledgebasesSearchPost(
            @NotNull AIKnowledgeBaseSearchCriteriaDTO aiKnowledgebaseSearchCriteria) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiKnowledgebasesSearchPost'");
    }

    @Override
    public Response internalAiAiProvidersAiProviderIdDelete(String aiProviderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiProvidersAiProviderIdDelete'");
    }

    @Override
    public Response internalAiAiProvidersAiProviderIdPut(String aiProviderId,
            @Valid @NotNull AIProviderDTO aiProviderDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiProvidersAiProviderIdPut'");
    }

    @Override
    public Response internalAiAiProvidersPost(@Valid @NotNull AIProviderDTO aiProviderDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiProvidersPost'");
    }

    @Override
    public Response internalAiAiProvidersSearchPost(@NotNull AIProviderSearchCriteriaDTO aiProviderSearchCriteria) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalAiAiProvidersSearchPost'");
    }

}
