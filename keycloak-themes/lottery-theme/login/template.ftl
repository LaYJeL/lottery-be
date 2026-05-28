<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false>
    <!DOCTYPE html>
    <html lang="uk">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>${msg("loginTitle",(realm.displayName!''))}</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <#if properties.styles?has_content>
            <#list properties.styles?split(' ') as style>
                <link href="${url.resourcesPath}/${style}" rel="stylesheet" />
            </#list>
        </#if>
    <style>
            /* Force override for dropdown behavior (fixes persistent hover cache issues) */
            /* Only hide on hover if the .show class is NOT present */
            .dropdown:hover .dropdown-content:not(.show) {
                display: none !important;
            }
            .dropdown-content.show {
                display: block !important;
            }
        </style>
    </head>

    <body>
    <div class="login-container">
        <#if realm.internationalizationEnabled && locale.supported?size gt 1>
            <div class="language-picker">
                <div class="dropdown">
                    <button class="dropbtn" type="button">
                        <#if locale.currentLanguageTag?starts_with("uk")>
                            🇺🇦 UA
                        <#else>
                            🇺🇸 EN
                        </#if>
                    </button>
                    <div class="dropdown-content">
                        <#list locale.supported as l>
                            <a href="${l.url}" class="lang-item">
                                <#if l.languageTag?starts_with("uk")>
                                    🇺🇦 UA (Українська)
                                <#elseif l.languageTag?starts_with("en")>
                                    🇺🇸 EN (English)
                                <#else>
                                    ${l.label}
                                </#if>
                            </a>
                        </#list>
                    </div>
                </div>
            </div>
        </#if>

        <div class="login-card">
            <div class="login-header">
                <#nested "header">
            </div>

            <#if displayMessage && message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
                <div class="alert alert-${message.type}">
                    ${kcSanitize(message.summary)?no_esc}
                </div>
            </#if>

            <#nested "form">
        </div>
    </div>
    <script>
        (function() {
            // Dropdown Toggle
            const dropBtn = document.querySelector('.dropbtn');
            const dropdownContent = document.querySelector('.dropdown-content');

            if (dropBtn && dropdownContent) {
                dropBtn.addEventListener('click', function(e) {
                    e.stopPropagation();
                    e.preventDefault(); // Prevent accidental form submission or anything
                    dropdownContent.classList.toggle('show');
                    console.log('Toggled dropdown:', dropdownContent.classList.contains('show'));
                });

                window.addEventListener('click', function(e) {
                    if (!e.target.matches('.dropbtn') && !e.target.closest('.dropdown')) {
                        if (dropdownContent.classList.contains('show')) {
                            dropdownContent.classList.remove('show');
                        }
                    }
                });
            } else {
                console.error('Dropdown elements not found', { btn: !!dropBtn, content: !!dropdownContent });
            }
        })();
    </script>
    </body>
    </html>
</#macro>