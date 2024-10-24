package io.github.onecx.ai.domain.models;

import static jakarta.persistence.FetchType.EAGER;
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
@Table(name = "AI_CONTEXT")
@NamedEntityGraph(name = AIContext.AI_CONTEXT_LOAD, includeAllAttributes = true)
public class AIContext extends TraceableEntity {

    public static final String AI_CONTEXT_LOAD = "AI_CONTEXT_LOAD";

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "LLM_SYSTEM_MESSAGE")
    private String llmSystemMessage;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "KB_ID")
    private AIKnowledgeBase knowledgebase;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "PROVIDER_ID")
    private AIProvider provider;

    @OneToOne(mappedBy = "aiContext", cascade = CascadeType.REMOVE)
    private AIKnowledgeVectorDb aiKnowledgeVectorDb;

    @OneToMany(cascade = { CascadeType.REMOVE }, fetch = LAZY, mappedBy = "aiContext", orphanRemoval = true)
    @OrderBy("creationDate ASC")
    private Set<AIKnowledgeUrl> aiKnowledgeUrls = new HashSet<>();

    @OneToMany(cascade = { CascadeType.REMOVE }, fetch = LAZY, mappedBy = "aiContext", orphanRemoval = true)
    @OrderBy("creationDate ASC")
    private Set<AIKnowledgeDatabase> aiKnowledgeDbs = new HashSet<>();

    @OneToMany(cascade = { CascadeType.REMOVE }, fetch = LAZY, mappedBy = "aiContext", orphanRemoval = true)
    @OrderBy("creationDate ASC")
    private Set<AIKnowledgeDocument> aiKnowledgeDocuments = new HashSet<>();

}
