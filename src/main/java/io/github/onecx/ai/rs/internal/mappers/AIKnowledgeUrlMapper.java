package io.github.onecx.ai.rs.internal.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.ai.rs.internal.model.AIKnowledgeUrlDTO;
import gen.io.github.onecx.ai.rs.internal.model.CreateAIKnowledgeUrlRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.UpdateAIKnowledgeUrlRequestDTO;
import io.github.onecx.ai.domain.models.AIKnowledgeUrl;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface AIKnowledgeUrlMapper {

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
    public abstract AIKnowledgeUrl createKnowledgeUrl(CreateAIKnowledgeUrlRequestDTO dto);

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
    public abstract AIKnowledgeUrl updateKnowledgeUrl(UpdateAIKnowledgeUrlRequestDTO dto,
            @MappingTarget AIKnowledgeUrl entity);

    @Mapping(target = "version", source = "modificationCount")
    public abstract AIKnowledgeUrlDTO map(AIKnowledgeUrl aiKnowledgeUrl);

    public abstract List<AIKnowledgeUrlDTO> mapUrlList(List<AIKnowledgeUrl> items);
}
