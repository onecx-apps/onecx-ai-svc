package io.github.onecx.ai.domain.models;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "AI_KB")
@NamedEntityGraph(name = AIKnowledgeBase.AI_KNOWLEDGEBASE_LOAD, includeAllAttributes = true)
@SuppressWarnings("java:S2160")
public class AIKnowledgeBase extends TraceableEntity {

    public static final String AI_KNOWLEDGEBASE_LOAD = "AI_KNOWLEDGEBASE_LOAD";

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "APP_ID")
    private String appId;

    @OneToMany(cascade = ALL, fetch = LAZY, mappedBy = "knowledgebase", orphanRemoval = true)
    @OrderBy("creationDate ASC")
    private Set<AIContext> contexts = new HashSet<>();

}
