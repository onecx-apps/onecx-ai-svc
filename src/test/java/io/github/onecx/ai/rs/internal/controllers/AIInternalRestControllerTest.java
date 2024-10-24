package io.github.onecx.ai.rs.internal.controllers;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.ai.rs.internal.model.*;
import io.github.onecx.ai.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(AIInternalRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class AIInternalRestControllerTest extends AbstractTest {

    @ConfigProperty(name = "quarkus.mockserver.endpoint")
    String mockServerEndpoint;

    /*
     * @Test
     * void sendRequestToModelLLMGenerateEndpoint() {
     *
     * System.out.println("Connecting to MockServer at: " + mockServerEndpoint);
     * GenerateRequestDTO generateRequest = getGenerateRequestDtoExample();
     *
     * var response = given()
     * .when()
     * .contentType(APPLICATION_JSON)
     * .body(generateRequest)
     * .post("/generate")
     * .then()
     * .statusCode(200)
     * .extract().body().as(AIResponseDTO.class);
     *
     * assertThat(response).isNotNull();
     * assertThat(response.getMessage()).isEqualTo("Mocking response from model");
     * }
     *
     * private GenerateRequestDTO getGenerateRequestDtoExample() {
     * GenerateRequestDTO generateRequestDTO = new GenerateRequestDTO();
     * AIRequestDTO aiRequestDto = new AIRequestDTO();
     * aiRequestDto.setMessage("message to the model");
     *
     * AIProviderDTO llmProvider = new AIProviderDTO();
     * llmProvider.setLlmUrl(mockServerEndpoint);
     * llmProvider.setModelName("llama2");
     * llmProvider.setModelVersion("model-version");
     * llmProvider.setName("provider-name");
     *
     * AIContextDTO aiContext = new AIContextDTO();
     * aiContext.setName("context1");
     * aiContext.setLlmProvider(llmProvider);
     *
     * generateRequestDTO.setAiRequest(aiRequestDto);
     * generateRequestDTO.setAiContext(aiContext);
     *
     * return generateRequestDTO;
     * }
     *
     */
}
