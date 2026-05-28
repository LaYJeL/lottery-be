<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "header">
        <div class="auth-header">
            <div class="logo-container">
                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="20" height="16" x="2" y="4" rx="2"></rect><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"></path></svg>
            </div>
            <h1 class="auth-title">${msg("forgotPasswordTitle")}</h1>
            <p class="auth-subtitle">${msg("forgotPasswordSubtitle")}</p>
        </div>
    <#elseif section = "form">
        <form id="kc-reset-password-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <div class="input-group">
                <label for="username">${msg("emailLabel")} <span class="required">*</span></label>
                <div class="input-wrapper">
                    <svg xmlns="http://www.w3.org/2000/svg" class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
                    <input type="text" id="username" name="username" class="${properties.kcInputClass!}" autofocus value="${(auth.attemptedUsername!'')}" 
                           placeholder="${msg("placeholderEmail")}"
                           aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"/>
                </div>
                <#if messagesPerField.existsError('username')>
                    <span id="input-error-username" class="error-message" aria-live="polite">
                        ${kcSanitize(messagesPerField.get('username'))?no_esc}
                    </span>
                </#if>
            </div>

            <button class="btn-primary" type="submit">${msg("doSubmit")}</button>

            <div class="registration-container" style="border-top: none; margin-top: 20px; padding-top: 0;">
                <a href="${url.loginUrl}" class="back-to-login-link" style="color: #94a3b8; text-decoration: none; display: inline-flex; align-items: center; justify-content: center; gap: 8px; transition: color 0.2s;">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m12 19-7-7 7-7"></path><path d="M19 12H5"></path></svg>
                    ${msg("backToLogin")}
                </a>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
