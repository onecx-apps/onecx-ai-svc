package io.github.onecx.ai.domain.models;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "AI_KNOWLEDGE_DOCUMENT")
@NamedEntityGraph(name = AIKnowledgeDocument.AI_KNOWLEDGE_DOCUMENT_LOAD, includeAllAttributes = true)
public class AIKnowledgeDocument extends TraceableEntity {

    public static final String AI_KNOWLEDGE_DOCUMENT_LOAD = "AI_KNOWLEDGE_DOCUMENT_LOAD";

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DOC_REF_ID")
    private String documentRefId;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private DocumentStatusType status;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "CONTEXT_ID", foreignKey = @ForeignKey(name = "fkpf7peog2p8liv1w1elikej22m", foreignKeyDefinition = "FOREIGN KEY (context_id) REFERENCES ai_context(guid) ON DELETE CASCADE"))
    private AIContext aiContext;

    public enum DocumentStatusType {
        NEW,
        PROCESSING,
        EMBEDDED
    }

}
