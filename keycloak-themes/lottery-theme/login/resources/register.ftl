<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('email','username','password','password-confirm'); section>
    <#if section = "form">
        <form id="kc-register-form" action="${url.registrationAction}" method="post">
            <div class="input-group">
                <label for="email">${msg("email")} <span class="required">*</span></label>
                <input type="text" id="email" name="email" value="${(register.formData.email!'')}" autocomplete="email" />
            </div>

            <div class="input-group">
                <label for="username">${msg("username")} <span class="required">*</span></label>
                <input type="text" id="username" name="username" value="${(register.formData.username!'')}" autocomplete="username" />
            </div>

            <div class="input-group">
                <label for="password">${msg("password")} <span class="required">*</span></label>
                <input type="password" id="password" name="password" autocomplete="new-password" />
            </div>

            <div class="input-group">
                <label for="password-confirm">${msg("passwordConfirm")} <span class="required">*</span></label>
                <input type="password" id="password-confirm" name="password-confirm" />
            </div>

            <button class="btn-primary" type="submit">${msg("doRegister")}</button>

            <div class="registration-container">
                <a href="${url.loginUrl}" class="btn-secondary">${kcSanitize(msg("backToLogin"))?no_esc}</a>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>