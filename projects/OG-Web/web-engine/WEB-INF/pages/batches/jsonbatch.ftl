<#escape x as x?html>
{
    "template_data": {
      "name": "${batch.observationDate} - ${batch.observationTime}",
      "object_id":"${batchDoc.uniqueId.objectId}",
<#--  "version_id":"${batchDoc.uniqueId.version}", -->
      "date": "${batch.observationDate}",
      "time": "${batch.observationTime}",
      "status": "${batch.status}",
      "creation_instant": "${batch.creationInstant}",
      "start_instant": "${batch.startInstant}",
      "end_instant": "${batch.endInstant}",
      "main_server": "${batch.masterProcessHost}",
      "number_restarts": "${batch.numRestarts}",
      "total_results": "${resultPaging.totalItems}",
      "total_errors": "${errorPaging.totalItems}"
    },
    "data": {
        "batch_results": [
<#list batch.data as item>
            "${item.calculationConfiguration}|${item.computedValue.specification.targetSpecification.uniqueId}|${item.computedValue.specification.valueName}|${item.computedValue.specification.functionUniqueId}|${item.computedValue.value}"<#if item_has_next>,</#if>
</#list>
        ],
        "batch_errors": [<#list batch.errors as item>
            "${item.calculationConfiguration}|${item.hbComputationTargetSpecification}|${item.valueName}|${item.functionUniqueId}|${item.exceptionClass}|${item.exceptionMsg}|${item.stackTrace}"
        <#if item_has_next>,</#if>
</#list>
        ]
    }
}
</#escape>