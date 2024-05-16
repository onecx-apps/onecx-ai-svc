package io.github.onecx.ai.domain.models;


import jakarta.persistence.*;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "AI_PROVIDER")
@NamedEntityGraph(name = AIProvider.AI_PROVIDER_LOAD, includeAllAttributes = true)
public class AIProvider extends TraceableEntity {

    public static final String AI_PROVIDER_LOAD = "AI_PROVIDER_LOAD";

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "LLM_URL")
    private String llmUrl;

    @Column(name = "MODEL_NAME")
    private String modelName;

    @Column(name = "MODEL_VERSION")
    private String modelVersion;

    @Column(name = "API_KEY")
    private String apiKey;

    @Column(name = "APP_ID")
    private String appId;



}
