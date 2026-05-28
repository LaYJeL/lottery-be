<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password'); section>
    <#if section = "header">
        <div class="logo-container">
                <div class="logo-gradient">
                     <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                </div>
            </div>
            <h1 class="auth-title">${msg("loginTitleNew")}</h1>
            <p class="auth-subtitle">${msg("loginSubtitleNew")}</p>
    <#elseif section = "form">
        <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
            <#assign hasLoginError = messagesPerField.existsError('username','password')>
            <div class="input-group">
                <label for="username">Email <span class="required">*</span></label>
                <div class="input-wrapper">
                    <svg xmlns="http://www.w3.org/2000/svg" class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
                     <input tabindex="1" id="username" class="${properties.kcInputClass!}" name="username" value="${(login.username!'')}"  type="email" autofocus autocomplete="off"
                            placeholder="${msg("placeholderEmail")}"
                            aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"
                     />
                </div>
                <#if hasLoginError>
                    <span id="input-error-username" class="error-message" aria-live="polite">
                        ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
                    </span>
                </#if>
            </div>

            <div class="input-group">
                <label for="password">Password <span class="required">*</span></label>
                <div class="input-wrapper">
                    <svg xmlns="http://www.w3.org/2000/svg" class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                    <input tabindex="2" id="password" class="${properties.kcInputClass!}" name="password" type="password" autocomplete="off"
                           placeholder="${msg("placeholderPassword")}"
                           aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"
                    />
                </div>
                <#if hasLoginError>
                    <span id="input-error-password" class="error-message" aria-live="polite">
                        ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
                    </span>
                </#if>
            </div>

            <div class="form-actions" style="margin-top: -10px; margin-bottom: 20px; text-align: right;">
                <#if realm.resetPasswordAllowed>
                    <a href="${url.loginResetCredentialsUrl}" class="forgot-link">${msg("doForgotPassword")}</a>
                </#if>
            </div>

            <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                <button tabindex="4" class="btn-primary" name="login" id="kc-login" type="submit">${msg("doLogIn")}</button>
            </div>
        </form>

        <div class="registration-container">
            <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
                <a href="${url.registrationUrl}" class="register-link">${msg("noAccount")} ${msg("signUp")}</a>
            </#if>
        </div>
        
        <div class="terms-container">
             <p>${msg("termsAndPrivacy")}</p>
        </div>
    </#if>
</@layout.registrationLayout>