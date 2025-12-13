<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password'); section>
    <#if section = "form">
        <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
            <div class="input-group">
                <label for="username">${msg("usernameOrEmail")}</label>
                <input id="username" name="username" value="${(login.username!'')}" type="text" autofocus autocomplete="off" />
            </div>

            <div class="input-group">
                <label for="password">${msg("password")}</label>
                <input id="password" name="password" type="password" autocomplete="off" />
            </div>

            <div class="form-actions">
                <#if realm.resetPasswordAllowed>
                    <a href="${url.loginResetCredentialsUrl}" class="forgot-link">${msg("doForgotPassword")}</a>
                </#if>
            </div>

            <button class="btn-primary" name="login" id="kc-login" type="submit">${msg("doLogIn")}</button>
        </form>

        <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
            <div class="registration-container">
                <a href="${url.registrationUrl}" class="btn-secondary">${msg("doRegister")}</a>
            </div>
        </#if>
    </#if>
</@layout.registrationLayout>