package io.github.onecx.ai.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.ai.rs.internal.model.*;
import io.github.onecx.ai.domain.criteria.AIKnowledgeBaseSearchCriteria;
import io.github.onecx.ai.domain.models.AIKnowledgeBase;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface KnowledgeBaseMapper {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "contexts", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    AIKnowledgeBase createAIKnowledgeBase(CreateAIKnowledgeBaseRequestDTO dto);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "contexts", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    AIKnowledgeBase updateAIKnowledgeBase(UpdateAIKnowledgeBaseRequestDTO dto);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "contexts", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    public abstract AIKnowledgeBase updateAIKnowledgeBase(UpdateAIKnowledgeBaseRequestDTO dto,
            @MappingTarget AIKnowledgeBase entity);

    @Mapping(target = "version", source = "modificationCount")
    @Mapping(target = "removeContextsItem", ignore = true)
    @Mapping(target = "contexts", ignore = true)
    AIKnowledgeBaseDTO map(AIKnowledgeBase knowledgeBase);

    @Mapping(target = "description", ignore = true)
    public abstract AIKnowledgeBaseSearchCriteria map(AIKnowledgeBaseSearchCriteriaDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    public abstract AIKnowledgeBasePageResultDTO mapPage(PageResult<AIKnowledgeBase> page);

}
