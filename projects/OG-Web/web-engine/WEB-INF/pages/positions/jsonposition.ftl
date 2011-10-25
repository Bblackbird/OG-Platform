<#escape x as x?html>
{
    "template_data": {
        "name": "${position.name}",
        "object_id": "${position.uniqueId.objectId}",
        "version_id": "${position.uniqueId.version}",
        <#if deleted>
        "deleted": "${positionDoc.versionToInstant}",
        </#if>
        <#if security?has_content>
        "security_name": "${security.name}",
        "security_unique_id": "${security.uniqueId.objectId}",
        "security_type": "${security.securityType}",
        </#if>
        <#if position.securityLink.objectId?has_content>
        "security_object_id": "${position.securityLink.objectId}",
        </#if>
        "quantity": "${position.quantity}"
    },
    "securities": [
        <#list position.securityLink.externalId.externalIds as item>{
            "scheme": "${item.scheme.name}",
            "value": "${item.value}"
        }<#if item_has_next>,</#if></#list>
    ],
    "trades": [
        <#list position.trades as item>{
            "id": "${item.uniqueId.objectId}",
            "quantity": "${item.quantity}",
            "counterParty": "${item.counterpartyExternalId}",
            "date": "${item.tradeDate}",
            <#assign tradeAttr = item.attributes>
            "attributes":{<#list tradeAttr?keys as key>"${key}":"${tradeAttr[key]}"<#if key_has_next>,</#if></#list>}
        }<#if item_has_next>,</#if></#list>
    ]
}
</#escape>