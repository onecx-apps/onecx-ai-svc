package io.github.onecx.ai.domain.models;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.*;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "AI_KNOWLEDGE_DATABASE")
@NamedEntityGraph(name = AIKnowledgeDatabase.AI_KNOWLEDGE_DATABASE_LOAD, includeAllAttributes = true)
public class AIKnowledgeDatabase extends TraceableEntity {

    public static final String AI_KNOWLEDGE_DATABASE_LOAD = "AI_KNOWLEDGE_DATABASE_LOAD";

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DB")
    private String db;

    @Column(name = "USR")
    private String usr;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "CONTEXT_ID")
    private AIContext aiContext;

}
