<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('password','password-confirm'); section>
    <#if section = "header">
        ${msg("updatePasswordTitle")}
    <#elseif section = "form">
        <form id="kc-passwd-update-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <input type="text" id="username" name="username" value="${username}" autocomplete="username" readonly="readonly" style="display:none;"/>
            <input type="password" id="password" name="password" autocomplete="current-password" style="display:none;"/>

            <div class="input-group">
                <label for="password-new">${msg("passwordNew")}</label>
                <div class="password-wrapper">
                    <svg xmlns="http://www.w3.org/2000/svg" class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                    <input type="password" id="password-new" name="password-new" class="${properties.kcInputClass!}" autofocus autocomplete="new-password"
                           aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>"
                    />
                     <button type="button" class="toggle-password-btn" onclick="togglePassword('password-new', this)">
                        <svg viewBox="0 0 24 24"><path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/></svg>
                    </button>
                </div>
                 <!-- Error span for New Password field -->
                <span id="client-password-error-top" class="${properties.kcInputErrorMessageClass!}" style="display:none;"></span>

                <#if messagesPerField.existsError('password')>
                    <span id="input-error-password" class="error-message" aria-live="polite">
                        ${kcSanitize(messagesPerField.get('password'))?no_esc}
                    </span>
                </#if>
            </div>

            <div class="input-group">
                <label for="password-confirm">${msg("passwordConfirm")}</label>
                <div class="password-wrapper">
                    <svg xmlns="http://www.w3.org/2000/svg" class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                    <input type="password" id="password-confirm" name="password-confirm" class="${properties.kcInputClass!}" autocomplete="new-password"
                           aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>"
                    />
                    <button type="button" class="toggle-password-btn" onclick="togglePassword('password-confirm', this)">
                        <svg viewBox="0 0 24 24"><path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/></svg>
                    </button>
                </div>
                <!-- Existing Error span for Confirm Password field -->
                <span id="client-password-error" class="${properties.kcInputErrorMessageClass!}" style="display:none;"></span>

                <!-- Password Requirements Hints -->
                <!-- Password Requirements Checklist -->
                <div class="password-rules-container">
                     <ul class="password-rules-list">
                         <li id="rule-length" class="rule-item invalid"><span class="icon">✗</span> ${msg("passwordRule.length")}</li>
                         <li id="rule-upper" class="rule-item invalid"><span class="icon">✗</span> ${msg("passwordRule.upper")}</li>
                         <li id="rule-lower" class="rule-item invalid"><span class="icon">✗</span> ${msg("passwordRule.lower")}</li>
                         <li id="rule-digit" class="rule-item invalid"><span class="icon">✗</span> ${msg("passwordRule.digit")}</li>
                         <li id="rule-special" class="rule-item invalid"><span class="icon">✗</span> ${msg("passwordRule.special")}</li>
                     </ul>
                </div>

                <style>
                    .password-rules-container {
                        margin-top: 8px;
                        font-size: 0.85rem;
                        color: #6c757d;
                    }
                    .password-rules-list {
                        list-style: none;
                        padding-left: 0;
                        margin-bottom: 0;
                    }
                    .rule-item {
                        display: flex;
                        align-items: center;
                        margin-bottom: 3px;
                    }
                    .rule-item .icon {
                        margin-right: 8px;
                        font-weight: bold;
                        width: 15px;
                        display: inline-block;
                        text-align: center;
                    }
                    .rule-item.valid {
                        color: #28a745;
                    }
                    .rule-item.valid .icon {
                        content: "✓";
                    }
                    .rule-item.invalid {
                        color: #dc3545; /* or a muted red */
                    }
                </style>

                <#if messagesPerField.existsError('password-confirm')>
                    <span id="input-error-password-confirm" class="error-message" aria-live="polite">
                        ${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}
                    </span>
                </#if>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                    <div class="${properties.kcFormOptionsWrapperClass!}">
                        <#if isAppInitiatedAction??>
                            <div class="checkbox">
                                <label><input type="checkbox" id="logout-sessions" name="logout-sessions" value="on" checked> ${msg("logoutOtherSessions")}</label>
                            </div>
                        </#if>
                    </div>
                </div>

                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                     <button class="btn-primary" type="submit">${msg("doSubmit")}</button>
                </div>
            </div>
        </form>

        <script>
            function togglePassword(inputId, btn) {
                const input = document.getElementById(inputId);
                const icon = btn.querySelector('svg path');

                if (input.type === "password") {
                    input.type = "text";
                     icon.setAttribute('d', 'M11.83 9L15 12.17V12a3 3 0 00-3-3h-.17zm-4.3.8l1.55 1.55c-.05.21-.08.43-.08.65a3 3 0 003 3c.22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53a5 5 0 01-5-5c0-.79.2-1.53.53-2.2zm-2.53-3.2l2.15 2.15C5.7 9.92 4.59 11.3 4 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l2.5 2.5 1.41-1.41L6.41 5.19 5 6.6zM12 17c-2.76 0-5-2.24-5-5 0-.5.1-1.2.4-1.8l6.4 6.4c-.6.2-1.2.4-1.8.4zm9-5c0-1.1-.3-2.14-.84-3.05L18.9 10.2c.07.26.1.54.1.8 0 2.76-2.24 5-5 5-.26 0-.54-.03-.8-.1l-1.25-1.25A6.9 6.9 0 0112 4.5c4.05 0 7.65 2.53 9 7.5z');
                } else {
                    input.type = "password";
                     icon.setAttribute('d', 'M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z');
                }
            }

            document.addEventListener('DOMContentLoaded', function() {
                const passwordInput = document.getElementById('password-new');
                const passwordConfirmInput = document.getElementById('password-confirm');
                const submitButton = document.querySelector('button[type="submit"]');

                const errorSpan = document.getElementById('client-password-error');
                 // Error span for New Password (below field)
                const errorSpanTop = document.getElementById('client-password-error-top');


                function validatePasswordMatch() {
                    const password = passwordInput.value;
                    const confirm = passwordConfirmInput.value;

                    if (confirm.length > 0 && password !== confirm) {
                        if(errorSpan) {
                            errorSpan.style.display = 'block';
                            errorSpan.innerText = "${msg('passwordMismatch')?no_esc}";
                        }
                        if(errorSpanTop) {
                            errorSpanTop.style.display = 'block';
                            errorSpanTop.innerText = "${msg('passwordMismatch')?no_esc}";
                        }
                        submitButton.disabled = true;
                        submitButton.classList.add('disabled');
                        passwordConfirmInput.classList.add('error');
                        passwordInput.classList.add('error');
                        passwordConfirmInput.setAttribute('aria-invalid', 'true');
                        passwordInput.setAttribute('aria-invalid', 'true');
                    } else {
                        if(errorSpan) errorSpan.style.display = 'none';
                         if(errorSpanTop) errorSpanTop.style.display = 'none';
                         
                        submitButton.disabled = false;
                        submitButton.classList.remove('disabled');
                        passwordConfirmInput.classList.remove('error');
                        passwordInput.classList.remove('error');
                        passwordConfirmInput.removeAttribute('aria-invalid');
                        passwordInput.removeAttribute('aria-invalid');
                    }
                }

                function validatePasswordRules() {
                    const password = passwordInput.value;
                    const rules = {
                        length: password.length >= 8,
                        upper: /[A-Z]/.test(password),
                        lower: /[a-z]/.test(password),
                        digit: /[0-9]/.test(password),
                        special: /[^a-zA-Z0-9]/.test(password)
                    };

                    for (const [key, isValid] of Object.entries(rules)) {
                        const ruleItem = document.getElementById('rule-' + key);
                        const iconSpan = ruleItem.querySelector('.icon');
                        if (isValid) {
                            ruleItem.classList.remove('invalid');
                            ruleItem.classList.add('valid');
                            iconSpan.innerText = '✓';
                        } else {
                            ruleItem.classList.remove('valid');
                            ruleItem.classList.add('invalid');
                            iconSpan.innerText = '✗';
                        }
                    }
                }

                passwordInput.addEventListener('input', validatePasswordRules);
                passwordInput.addEventListener('input', validatePasswordMatch);
                passwordConfirmInput.addEventListener('input', validatePasswordMatch);
            });
        </script>
    </#if>
</@layout.registrationLayout>
