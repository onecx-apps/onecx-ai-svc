package io.github.onecx.ai.rs.internal.services;

import jakarta.ws.rs.core.Response;

import gen.io.github.onecx.ai.rs.internal.model.AIContextDTO;
import gen.io.github.onecx.ai.rs.internal.model.AIProviderDTO;
import gen.io.github.onecx.ai.rs.internal.model.ChatRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.GenerateRequestDTO;

abstract class AbstractLlmService {
    public abstract Response generate(GenerateRequestDTO generateRequestDTO);

    public abstract Response chat(ChatRequestDTO chatRequestDTO);

    public abstract AIProviderDTO setUpProvider(AIContextDTO aiContextDTO);
}
