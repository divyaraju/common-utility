package com.cerner.pophealth.appservices.common;

import static com.google.common.base.Preconditions.checkNotNull;

import com.cerner.dap.ontology.context.model.VersionedContext;
import com.cerner.harpe.client.PartitionClient;
import com.cerner.pophealth.appservices.candidates.models.pipeline.Code;
import com.cerner.pophealth.appservices.candidates.models.pipeline.Condition;

/**
 * Converter for converting a {@link com.cerner.pophealth.program.models.avro.poprecord.Condition}
 * model into our own {@link Condition} model for candidate supporting facts.
 */
public class ConditionExtractor {

  /**
   * Translates a population record condition into the model used by the care management data
   * store.
   *
   * @param condition the condition to extract
   * @param sensitiveVersionedContext the context used to filter sensitive data
   * @param partitionClient the client data partition
   */
  public static Condition getCondition(
      com.cerner.pophealth.program.models.avro.poprecord.Condition condition,
      VersionedContext sensitiveVersionedContext, PartitionClient partitionClient) {
    checkNotNull(condition, "condition: null");
    checkNotNull(partitionClient, "partitionClient: null");

    if (ExtractorUtils.isCodeSensitive(condition.getConditionCode(), sensitiveVersionedContext)) {
      return null;
    }

    Code code = ExtractorUtils.getCode(condition.getConditionCode());
    Condition.Builder conditionBuilder = Condition.newBuilder();

    if (code != null) {
      conditionBuilder.setCode(code.getCode());
      conditionBuilder.setCodingSystemId(code.getCodingSystemId());
      conditionBuilder.setName(code.getCodeSystemDisplay());
    }

    return conditionBuilder
        .setEffectiveDate(condition.getEffectiveDate())
        .setSource(ExtractorUtils.getSource(partitionClient, condition.getSource()))
        .build();
  }
}
