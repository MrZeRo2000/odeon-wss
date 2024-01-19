package com.romanpulov.odeonwss.builder.entitybuilder;

import com.romanpulov.odeonwss.builder.AbstractClassBuilder;
import com.romanpulov.odeonwss.entity.DBProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;

import java.time.LocalDateTime;

public class EntityDBProcessInfoBuilder extends AbstractClassBuilder<DBProcessInfo> {
    public EntityDBProcessInfoBuilder() {
        super(DBProcessInfo.class);
    }

    public EntityDBProcessInfoBuilder withId(long id) {
        this.instance.setId(id);
        return this;
    }

    public EntityDBProcessInfoBuilder withProcessorType(ProcessorType processorType) {
        this.instance.setProcessorType(processorType);
        return this;
    }

    public EntityDBProcessInfoBuilder withProcessingStatus(ProcessingStatus processingStatus) {
        this.instance.setProcessingStatus(processingStatus);
        return this;
    }

    public EntityDBProcessInfoBuilder withUpdateDateTime(LocalDateTime updateDateTime) {
        this.instance.setUpdateDateTime(updateDateTime);
        return this;
    }
}
