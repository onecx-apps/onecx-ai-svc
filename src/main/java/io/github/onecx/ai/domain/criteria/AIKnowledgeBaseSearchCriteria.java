package io.github.onecx.ai.domain.criteria;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class AIKnowledgeBaseSearchCriteria {

    private String id;

    private String name;

    private String description;

    private String tenandId;

    private String appId;

    private Integer pageNumber;

    private Integer pageSize;

}
