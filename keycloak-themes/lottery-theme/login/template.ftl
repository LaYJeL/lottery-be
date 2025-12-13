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
    </head>

    <body>
    <div class="login-container">
        <#if realm.internationalizationEnabled && locale.supported?size gt 1>
            <div class="language-picker">
                <div class="dropdown">
                    <button class="dropbtn" type="button">
                        ${(locale.current == 'Українська')?string('UK', 'EN')}
                    </button>
                    <div class="dropdown-content">
                        <#list locale.supported as l>
                        <#-- Важливо: використовуємо l.url, щоб Keycloak сам перенаправив на зміну мови -->
                            <a href="${l.url}" class="lang-item">
                                <#if l.label == 'Українська'>🇺🇦 UK <#elseif l.label == 'English'>🇺🇸 EN <#else>${l.label}</#if>
                            </a>
                        </#list>
                    </div>
                </div>
            </div>
        </#if>

        <div class="login-card">
            <div class="login-header">
                <div class="logo">🎰</div>
                <h1>${realm.displayName}</h1>
            </div>

            <#if displayMessage && message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
                <div class="alert alert-${message.type}">
                    ${kcSanitize(message.summary)?no_esc}
                </div>
            </#if>

            <#nested "form">
        </div>
    </div>
    </body>
    </html>
</#macro>