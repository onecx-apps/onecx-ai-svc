package io.github.onecx.ai.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.ai.rs.internal.model.AIKnowledgeVectorDbDTO;
import gen.io.github.onecx.ai.rs.internal.model.CreateAIKnowledgeVectorDbRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.UpdateAIKnowledgeVectorDbRequestDTO;
import io.github.onecx.ai.domain.models.AIKnowledgeVectorDb;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface AIKnowledgeVectorDbMapper {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", source = "user")
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "aiContext", ignore = true)
    @Mapping(target = "vdb", source = "vdbUrl")
    public abstract AIKnowledgeVectorDb createKnowledgeVectorDb(CreateAIKnowledgeVectorDbRequestDTO dto);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", source = "user")
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "vdb", source = "vdbUrl")
    @Mapping(target = "aiContext", ignore = true)
    public abstract AIKnowledgeVectorDb updateKnowledgeVectorDb(UpdateAIKnowledgeVectorDbRequestDTO dto,
            @MappingTarget AIKnowledgeVectorDb entity);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "vdbUrl", source = "vdb")
    @Mapping(target = "user", source = "creationUser")
    @Mapping(target = "pwd", ignore = true)
    public abstract AIKnowledgeVectorDbDTO map(AIKnowledgeVectorDb aiKnowledgeVectorDb);
}
