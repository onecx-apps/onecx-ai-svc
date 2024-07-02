package io.github.onecx.ai.rs.internal.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.ai.rs.internal.model.*;
import io.github.onecx.ai.domain.models.AIKnowledgeDatabase;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface AIKnowledgeDatabaseMapper {

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
    @Mapping(target = "usr", source = "user")
    public abstract AIKnowledgeDatabase createKnowledgeDatabase(CreateAIKnowledgeDatabaseRequestDTO dto);

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
    @Mapping(target = "usr", ignore = true)
    public abstract AIKnowledgeDatabase updateKnowledgeDatabase(UpdateAIKnowledgeDatabaseRequestDTO dto,
            @MappingTarget AIKnowledgeDatabase entity);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "user", source = "usr")
    @Mapping(target = "pwd", ignore = true)
    @Mapping(target = "tables", ignore = true)
    @Mapping(target = "removeTablesItem", ignore = true)
    public abstract AIKnowledgeDatabaseDTO map(AIKnowledgeDatabase aiKnowledgeDatabase);

    public abstract List<AIKnowledgeDatabaseDTO> mapDatabaseList(List<AIKnowledgeDatabase> items);
}
