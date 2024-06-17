package io.github.onecx.ai.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.ai.rs.internal.model.AIProviderDTO;
import gen.io.github.onecx.ai.rs.internal.model.AIProviderPageResultDTO;
import gen.io.github.onecx.ai.rs.internal.model.AIProviderSearchCriteriaDTO;
import gen.io.github.onecx.ai.rs.internal.model.CreateAIProviderRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.UpdateAIProviderRequestDTO;
import io.github.onecx.ai.domain.criteria.AIProviderSearchCriteria;
import io.github.onecx.ai.domain.models.AIProvider;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface AIProviderMapper {

    AIProviderSearchCriteria map(AIProviderSearchCriteriaDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    AIProviderPageResultDTO mapPageResult(PageResult<AIProvider> page);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    AIProvider createAIProvider(CreateAIProviderRequestDTO dto);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    AIProvider updateAIProvide(UpdateAIProviderRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    void update(UpdateAIProviderRequestDTO chatDTO, @MappingTarget AIProvider entity);

    @Mapping(target = "version", source = "modificationCount")
    AIProviderDTO map(AIProvider provider);

}
