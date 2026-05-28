<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('email','password','password-confirm'); section>
    <#if section = "header">
        <div class="auth-header">
            <div class="logo-container">
                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
            </div>
            <h1 class="auth-title">${msg("registerTitle")}</h1>
            <p class="auth-subtitle">${msg("registerSubtitle")}</p>
        </div>
    <#elseif section = "form">
        <form id="kc-register-form" action="${url.registrationAction}" method="post">

            <div class="input-group">
                <label for="email">${msg("emailLabel")} <span class="required">*</span></label>
                <div class="input-wrapper">
                    <svg xmlns="http://www.w3.org/2000/svg" class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
                    <input type="email" id="email" name="email" value="${(register.formData.email!'')}" autocomplete="email" placeholder="${msg("placeholderEmail")}" />
                </div>
                <span id="client-email-error" class="error-message" style="display:none;"></span>
                <#if messagesPerField.existsError('email')>
                    <span id="input-error-email" class="error-message" aria-live="polite">
                        ${kcSanitize(messagesPerField.get('email'))?no_esc}
                    </span>
                </#if>
            </div>

            <!-- Username field removed (Email as Username) -->

            <div class="input-group">
                <label for="password">${msg("passwordLabel")} <span class="required">*</span></label>
                <div class="password-wrapper">
                    <svg xmlns="http://www.w3.org/2000/svg" class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                    <input type="password" id="password" name="password" autocomplete="new-password"
                           placeholder="${msg("placeholderPassword")}"
                           aria-invalid="${messagesPerField.existsError('password','password-confirm')?c}"
                           class="${messagesPerField.existsError('password','password-confirm')?then('error', '')}"
                    />
                    <button type="button" class="toggle-password-btn" onclick="togglePassword('password', this)">
                        <svg viewBox="0 0 24 24"><path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/></svg>
                    </button>
                </div>
            <!-- Error span for Password field -->
                <span id="client-password-error-top" class="error-message" style="display:none;"></span>
                <#if messagesPerField.existsError('password')>
                    <span id="input-error-password" class="error-message" aria-live="polite">
                        ${kcSanitize(messagesPerField.get('password'))?no_esc}
                    </span>
                </#if>
                
                
                <!-- Password Requirements Checklist -->
                <div class="password-rules-container" id="password-rules">
                     <div class="rules-header">${msg("passwordRule.header")}</div>
                     <ul class="password-rules-list">
                         <li id="rule-length" class="rule-item invalid">
                             <span class="icon-circle"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle></svg></span>
                             <span class="icon-check" style="display:none;"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg></span>
                             ${msg("passwordRule.length")}
                         </li>
                         <li id="rule-upper" class="rule-item invalid">
                             <span class="icon-circle"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle></svg></span>
                             <span class="icon-check" style="display:none;"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg></span>
                             ${msg("passwordRule.upper")}
                         </li>
                         <li id="rule-lower" class="rule-item invalid">
                             <span class="icon-circle"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle></svg></span>
                             <span class="icon-check" style="display:none;"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg></span>
                             ${msg("passwordRule.lower")}
                         </li>
                         <li id="rule-digit" class="rule-item invalid">
                             <span class="icon-circle"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle></svg></span>
                             <span class="icon-check" style="display:none;"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg></span>
                             ${msg("passwordRule.digit")}
                         </li>
                         <li id="rule-special" class="rule-item invalid">
                             <span class="icon-circle"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle></svg></span>
                             <span class="icon-check" style="display:none;"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg></span>
                             ${msg("passwordRule.special")}
                         </li>
                     </ul>
                </div>
            </div>

            <div class="input-group">
                <label for="password-confirm">${msg("passwordConfirmLabel")} <span class="required">*</span></label>
                <div class="password-wrapper">
                    <svg xmlns="http://www.w3.org/2000/svg" class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                    <input type="password" id="password-confirm" name="password-confirm"
                           placeholder="${msg("placeholderConfirmPassword")}"
                           class="${messagesPerField.existsError('password-confirm')?then('error', '')}"
                           aria-invalid="${messagesPerField.existsError('password-confirm')?c}"
                    />
                    <button type="button" class="toggle-password-btn" onclick="togglePassword('password-confirm', this)">
                        <svg viewBox="0 0 24 24"><path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/></svg>
                    </button>
                </div>
                <!-- Existing Error span for Confirm Password field -->
                <span id="client-password-error" class="error-message" style="display:none;"></span>


            </div>

                <#if messagesPerField.existsError('password-confirm')>
                    <span id="input-error-password-confirm" class="error-message" aria-live="polite">
                        ${messagesPerField.get('password-confirm')}
                    </span>
                </#if>

                <style>
                    .password-rules-container {
                        margin-top: 5px;
                        font-size: 0.85rem;
                        background: rgba(15, 23, 42, 0.6);
                        border: 1px solid rgba(255, 255, 255, 0.1);
                        border-radius: 8px;
                        padding: 12px;
                        display: none; /* Hidden by default */
                        transition: opacity 0.2s;
                    }
                    .rules-header {
                        color: #cbd5e1;
                        font-weight: 600;
                        margin-bottom: 8px;
                        font-size: 0.8rem;
                    }
                    .password-rules-list {
                        list-style: none;
                        padding-left: 0;
                        margin-bottom: 0;
                    }
                    .rule-item {
                        display: flex;
                        align-items: center;
                        margin-bottom: 4px;
                        color: #94a3b8; /* Default gray */
                        font-size: 0.8rem;
                    }
                    .rule-item svg {
                        margin-right: 8px;
                    }
                    .rule-item.valid {
                        color: #10b981; /* Green */
                    }
                </style>
            <div class="identity-banner" style="padding: 16px; background-color: rgba(100, 108, 255, 0.1); border: 1px solid rgba(100, 108, 255, 0.3); border-radius: 12px; margin-bottom: 24px; display: flex; align-items: flex-start; gap: 12px; text-align: left;">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#646cff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="flex-shrink: 0; margin-top: 2px;"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                <p style="margin: 0; font-size: 14px; color: #cbd5e1; line-height: 1.4;">${msg("identityVerificationBanner")}</p>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <button class="btn-primary" type="submit">${msg("doRegister")}</button>
                </div>
            </div>

            <div class="registration-container" style="border-top: 1px solid rgba(255, 255, 255, 0.1); padding-top: 24px;">
                <a href="${url.loginUrl}" class="register-link">${msg("alreadyHaveAccount")} ${msg("signIn")}</a>
            </div>

            <div class="terms-container">
                <p>${msg("termsAndPrivacy")}</p>
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
                const passwordInput = document.getElementById('password');
                const passwordConfirmInput = document.getElementById('password-confirm');
                const submitButton = document.querySelector('button[type="submit"]');

                // Error span for Confirm Password (already exists below field in HTML or added here)
                const errorSpan = document.getElementById('client-password-error');
                // Error span for Password (below password field)
                const errorSpanTop = document.getElementById('client-password-error-top');




                // --- Validation Logic ---
                const emailInput = document.getElementById('email');
                
                const emailError = document.getElementById('client-email-error');

                function showError(input, element, msgText) {
                    if (element) {
                        element.style.display = 'block';
                        element.innerText = msgText;
                    }
                    input.classList.add('error');
                    input.setAttribute('aria-invalid', 'true');
                }

                function hideError(input, element) {
                    if (element) {
                        element.style.display = 'none';
                        element.innerText = '';
                    }
                    input.classList.remove('error');
                    input.removeAttribute('aria-invalid');
                }

                function validateField(input, errorElement, msgText) {
                    if (!input.value.trim()) {
                        showError(input, errorElement, msgText);
                        return false;
                    } else {
                        hideError(input, errorElement);
                        return true;
                    }
                }
                
                // We'll manage button state separately
                function updateButtonState() {
                    const isEmailValid = !!emailInput.value.trim();
                    
                    const password = passwordInput.value;
                    const confirm = passwordConfirmInput.value;
                    
                    // Basic rule check
                     const rules = {
                        length: password.length >= 8,
                        upper: /[A-Z]/.test(password),
                        lower: /[a-z]/.test(password),
                        digit: /[0-9]/.test(password),
                        special: /[^a-zA-Z0-9]/.test(password)
                    };
                    const isPasswordComplex = Object.values(rules).every(Boolean);
                    
                    const isPasswordMatch = confirm.length > 0 && password === confirm;

                    if (isEmailValid && isPasswordComplex && isPasswordMatch) {
                        submitButton.disabled = false;
                        submitButton.classList.remove('disabled');
                    } else {
                        submitButton.disabled = true;
                        submitButton.classList.add('disabled');
                    }
                }

                // Blur events for "Please specify X" messages
                emailInput.addEventListener('blur', () => {
                   if(!emailInput.value.trim()) showError(emailInput, emailError, "${msg('missingEmail')?no_esc}");
                });
                passwordInput.addEventListener('blur', () => {
                   if(!passwordInput.value) showError(passwordInput, errorSpanTop, "${msg('missingPassword')?no_esc}");
                });
                passwordConfirmInput.addEventListener('blur', () => {
                   if(!passwordConfirmInput.value) showError(passwordConfirmInput, errorSpan, "${msg('missingConfirmPassword')?no_esc}");
                });


                // Input events to hide error and re-evaluate button
                emailInput.addEventListener('input', () => {
                    hideError(emailInput, emailError);
                    updateButtonState();
                });

                function validatePasswordMatch() {
                    const password = passwordInput.value;
                    const confirm = passwordConfirmInput.value;
                    const isMatch = confirm.length > 0 && password === confirm;

                    if (confirm.length > 0 && !isMatch) {
                        // Mismatch
                        const mismatchMsg = "${msg('passwordMismatch')?no_esc}";
                        if(errorSpan) {
                            errorSpan.style.display = 'block';
                            errorSpan.innerText = mismatchMsg;
                        }
                        if(errorSpanTop) {
                            errorSpanTop.style.display = 'block';
                            errorSpanTop.innerText = mismatchMsg;
                        }
                        passwordConfirmInput.classList.add('error');
                        passwordInput.classList.add('error');
                        passwordConfirmInput.setAttribute('aria-invalid', 'true');
                        passwordInput.setAttribute('aria-invalid', 'true');
                    } else {
                        // Match or empty (empty handled by blur)
                        // Only clear if it's NOT empty (if empty, blur might have set 'missing')
                        // actually, input should clear 'missing' too.
                        
                        if (confirm.length > 0) {
                             if(errorSpan) errorSpan.style.display = 'none';
                             if(errorSpanTop) errorSpanTop.style.display = 'none';
                             passwordConfirmInput.classList.remove('error');
                             passwordInput.classList.remove('error');
                             passwordConfirmInput.removeAttribute('aria-invalid');
                             passwordInput.removeAttribute('aria-invalid');
                        } else {
                             // If empty, just ensure mismatch error is gone. Missing error might be there from blur.
                             // But this runs on INPUT. So if user deletes everything, we should probably hide mismatch
                             if(errorSpan && errorSpan.innerText === "${msg('passwordMismatch')?no_esc}") errorSpan.style.display = 'none';
                             if(errorSpanTop && errorSpanTop.innerText === "${msg('passwordMismatch')?no_esc}") errorSpanTop.style.display = 'none';
                             
                             // Don't remove error class if it's empty, might be required error?
                             // A bit complex. Let's simplify:
                             // On Input, we generally clear errors and re-evaluate.
                        }
                        
                         // Simplified cleanup for non-mismatch state
                        if (isMatch || confirm.length === 0) {
                            // We don't want to clear "Please specify" if we just deleted char?
                            // Actually yes we do, typing hides errors.
                            if(errorSpan) errorSpan.style.display = 'none'; 
                            if(errorSpanTop) errorSpanTop.style.display = 'none';
                            passwordConfirmInput.classList.remove('error');
                            passwordInput.classList.remove('error');
                            passwordConfirmInput.removeAttribute('aria-invalid');
                            passwordInput.removeAttribute('aria-invalid');
                        }
                    }
                    updateButtonState();
                }

                function validatePasswordRules() {
                    const password = passwordInput.value;
                    
                    // Hide "missing" error if user is typing
                    if(password.length > 0) {
                        if(errorSpanTop && errorSpanTop.innerText === "${msg('missingPassword')?no_esc}") {
                            errorSpanTop.style.display = 'none';
                            passwordInput.classList.remove('error');
                        }
                    }

                    const rules = {
                        length: password.length >= 8,
                        upper: /[A-Z]/.test(password),
                        lower: /[a-z]/.test(password),
                        digit: /[0-9]/.test(password),
                        special: /[^a-zA-Z0-9]/.test(password)
                    };

                    for (const [key, isValid] of Object.entries(rules)) {
                        const ruleItem = document.getElementById('rule-' + key);
                        const iconCircle = ruleItem.querySelector('.icon-circle');
                        const iconCheck = ruleItem.querySelector('.icon-check');
                        
                        if (isValid) {
                            ruleItem.classList.remove('invalid');
                            ruleItem.classList.add('valid');
                            iconCircle.style.display = 'none';
                            iconCheck.style.display = 'inline-block';
                        } else {
                            ruleItem.classList.remove('valid');
                            ruleItem.classList.add('invalid');
                            iconCircle.style.display = 'inline-block';
                            iconCheck.style.display = 'none';
                        }
                    }
                    updateButtonState();
                }

                // Show/Hide rules on focus/blur
                const rulesContainer = document.getElementById('password-rules');
                passwordInput.addEventListener('focus', () => {
                     rulesContainer.style.display = 'block';
                });
                passwordInput.addEventListener('blur', () => {
                     // Check if empty? Or just hide always? User said "shown only when pressed".
                     rulesContainer.style.display = 'none';
                });

                // Attach modified listeners
                // Final Event Attachment
                passwordInput.addEventListener('input', validatePasswordRules);
                passwordInput.addEventListener('input', validatePasswordMatch);
                passwordConfirmInput.addEventListener('input', validatePasswordMatch);
                
                // Initial check in case of browser autofill
                updateButtonState();
            }); // Close DOMContentLoaded

        </script>
    </#if>
</@layout.registrationLayout>