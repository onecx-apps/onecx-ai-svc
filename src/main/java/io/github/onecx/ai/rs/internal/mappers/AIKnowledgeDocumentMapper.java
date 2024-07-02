package io.github.onecx.ai.rs.internal.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.ai.rs.internal.model.AIKnowledgeDocumentDTO;
import gen.io.github.onecx.ai.rs.internal.model.CreateAIKnowledgeDocumentRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.UpdateAIKnowledgeDocumentRequestDTO;
import io.github.onecx.ai.domain.models.AIKnowledgeDocument;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface AIKnowledgeDocumentMapper {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "aiContext", ignore = true)
    public abstract AIKnowledgeDocument createKnowledgeDocument(CreateAIKnowledgeDocumentRequestDTO dto);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "aiContext", ignore = true)
    public abstract AIKnowledgeDocument updateKnowledgeDocument(UpdateAIKnowledgeDocumentRequestDTO dto,
            @MappingTarget AIKnowledgeDocument entity);

    @Mapping(target = "version", ignore = true)
    public abstract AIKnowledgeDocumentDTO map(AIKnowledgeDocument aiKnowledgeDocument);

    public abstract List<AIKnowledgeDocumentDTO> mapDocumentList(List<AIKnowledgeDocument> items);

}
