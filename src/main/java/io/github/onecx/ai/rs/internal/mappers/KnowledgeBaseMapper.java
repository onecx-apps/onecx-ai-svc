package io.github.onecx.ai.rs.internal.mappers;

import jakarta.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gen.io.github.onecx.ai.rs.internal.model.AIKnowledgeBaseDTO;
import gen.io.github.onecx.ai.rs.internal.model.CreateAIKnowledgeBaseRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.UpdateAIKnowledgeBaseRequestDTO;
import io.github.onecx.ai.domain.models.AIKnowledgeBase;

@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class KnowledgeBaseMapper {

    @Inject
    ObjectMapper mapper;

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
    public abstract AIKnowledgeBase createAIKnowledgeBase(CreateAIKnowledgeBaseRequestDTO dto);

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
    public abstract AIKnowledgeBase updateAIKnowledgeBase(UpdateAIKnowledgeBaseRequestDTO dto);

    @Mapping(target = "version", source = "modificationCount")
    @Mapping(target = "removeContextsItem", ignore = true)
    @Mapping(target = "contexts", ignore = true)
    public abstract AIKnowledgeBaseDTO map(AIKnowledgeBase knowledgeBase);

    @Named("properties")
    public String mapToString(Object properties) {

        if (properties == null) {
            return null;
        }

        try {
            return mapper.writeValueAsString(properties);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
