package io.github.onecx.ai.domain.criteria;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class AIProviderSearchCriteria {

    private String id;

    private String name;

    private String description;

    private String llmUrl;

    private String modelName;

    private String modelVersion;

    private String apiKey;

    private String tenandId;

    private String appId;

    private Integer pageNumber;

    private Integer pageSize;

}
