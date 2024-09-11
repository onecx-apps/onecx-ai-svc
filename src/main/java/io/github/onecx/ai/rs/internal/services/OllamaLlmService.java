package io.github.onecx.ai.rs.internal.services;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.language.StreamingLanguageModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingLanguageModel;
import gen.io.github.onecx.ai.rs.internal.model.AIContextDTO;
import gen.io.github.onecx.ai.rs.internal.model.AIProviderDTO;
import gen.io.github.onecx.ai.rs.internal.model.ChatRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.GenerateRequestDTO;
import io.github.onecx.ai.domain.daos.AIContextDAO;
import io.github.onecx.ai.domain.daos.AIProviderDAO;
import io.github.onecx.ai.rs.internal.mappers.AIProviderMapper;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class OllamaLlmService extends AbstractLlmService {

    @Inject
    AIProviderDAO daoProvider;

    @Inject
    AIContextDAO daoContext;

    @Inject
    AIProviderMapper mapperProvider;

    @Override
    public Response generate(GenerateRequestDTO generateRequestDTO) {

        AIProviderDTO providerDTO = setUpProvider(generateRequestDTO.getAiContext());

        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("Authorization", "Basic QVBJX0tFWTo1YTg1MzgwMC1mYjEzLTQ1NjQtYTA5OC1hMWZmZmI0NGEzMmU=");

        StreamingLanguageModel ollamaGenerate = OllamaStreamingLanguageModel.builder()
                .baseUrl("https://ollama.one-cx.org/")
                .modelName("mixtral")
                .customHeaders(customHeaders)
                .timeout(Duration.ofSeconds(60L))
                .build();

        final String[] message = { "" };

        CompletableFuture<String> future = new CompletableFuture<>();

        ollamaGenerate.generate(generateRequestDTO.getAiRequest().getMessage(), new StreamingResponseHandler<String>() {
            @Override
            public void onNext(String token) {
                message[0] += token;
                System.out.println(token);
            }

            @Override
            public void onComplete(dev.langchain4j.model.output.Response<String> response) {

                System.out.println(message[0]);
                future.complete(response.toString());
            }

            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
        future.join();
        return Response.ok(message[0].toString()).build();
    }

    @Override
    public Response chat(ChatRequestDTO chatRequestDTO) {
        final String[] message = { "" };
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("Authorization", "Basic QVBJX0tFWTo1YTg1MzgwMC1mYjEzLTQ1NjQtYTA5OC1hMWZmZmI0NGEzMmU=");

        StreamingChatLanguageModel ollama = OllamaStreamingChatModel.builder()
                .baseUrl("http://ollama.one-cx.org/")
                .modelName("mixtral")
                .customHeaders(customHeaders)
                .timeout(Duration.ofSeconds(30L))
                .build();

        CompletableFuture<dev.langchain4j.model.output.Response<AiMessage>> futureResponse = new CompletableFuture<>();

        ollama.generate("tell me a joke", new StreamingResponseHandler<AiMessage>() {

            @Override
            public void onNext(String token) {
                System.out.print(token);
                message[0] += token;
            }

            @Override
            public void onComplete(dev.langchain4j.model.output.Response<AiMessage> response) {
                futureResponse.complete(response);
            }

            @Override
            public void onError(Throwable error) {
                futureResponse.completeExceptionally(error);
            }
        });

        futureResponse.join();

        return Response.ok(message[0].toString()).build();
    }

    @Override
    public AIProviderDTO setUpProvider(AIContextDTO aiContextDTO) {

        if (aiContextDTO.getLlmProvider() != null) {
            return aiContextDTO.getLlmProvider();
        }

        return mapperProvider.map(daoContext.findById(aiContextDTO.getId()).getProvider());

    }
}
