package io.github.onecx.ai.rs.internal.mappers;

import jakarta.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.io.github.onecx.ai.rs.internal.model.AIKnowledgeBaseDTO;
import gen.io.github.onecx.ai.rs.internal.model.AIKnowledgeDocumentDTO;
import io.github.onecx.ai.domain.models.AIKnowledgeBase;
import io.github.onecx.ai.domain.models.AIKnowledgeDocument;

@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class KnowledgeBaseMapper {
    @Inject
    ObjectMapper mapper;

    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "aiContext", ignore = true)
    public abstract AIKnowledgeDocument createAIKnowledgeDocument(AIKnowledgeDocumentDTO aiKnowledgeDocumentDTO);

    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "contexts", ignore = true)
    public abstract AIKnowledgeBase createAIKnowledgeBase(AIKnowledgeBaseDTO aiKnowledgeBaseDTO);

}
