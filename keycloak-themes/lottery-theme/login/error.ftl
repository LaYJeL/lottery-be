<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "header">
        ${msg("errorTitle")}
    <#elseif section = "form">
        <div id="kc-error-message">
            <p class="instruction">${message.summary?no_esc}</p>
            <#if client?? && client.baseUrl?has_content>
                <div class="form-actions">
                    <a id="backToApplication" class="btn-primary" href="${client.baseUrl}" style="text-decoration: none; display: inline-block; text-align: center;">${msg("backToApplication")}</a>
                </div>
            <#else>
                <div class="form-actions">
                     <a id="backToLogin" class="btn-primary" href="http://localhost:5173" style="text-decoration: none; display: inline-block; text-align: center;">${msg("backToLogin")}</a>
                </div>
            </#if>
        </div>
    </#if>
</@layout.registrationLayout>
