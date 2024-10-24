package io.github.onecx.ai.rs.internal.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gen.io.github.onecx.ai.rs.internal.AiInternalApi;
import gen.io.github.onecx.ai.rs.internal.model.ChatRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.GenerateRequestDTO;
import io.github.onecx.ai.domain.daos.AIContextDAO;
import io.github.onecx.ai.domain.daos.AIProviderDAO;
import io.github.onecx.ai.rs.internal.services.OllamaLlmService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class AIInternalRestController implements AiInternalApi {

    @Inject
    AIProviderDAO daoProvider;

    @Inject
    AIContextDAO daoContext;

    @Inject
    OllamaLlmService ollamaLlmService;

    @Override
    public Response chat(ChatRequestDTO chatRequestDTO) {
        return ollamaLlmService.chat(chatRequestDTO);
    }

    @Override
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public Response generate(GenerateRequestDTO generateRequestDTO) {
        Logger LOGGER = LoggerFactory.getLogger(AIInternalRestController.class);
        LOGGER.info("AIInternalRestController generate");

        return ollamaLlmService.generate(generateRequestDTO);
    }
}
