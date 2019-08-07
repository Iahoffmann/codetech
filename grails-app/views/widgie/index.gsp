<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main">
        <g:set var="entityName" value="${message(code: 'widgie.label', default: 'Widgie')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#list-widgie" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
            <form>
                <g:field name="query" value="${params.query}" type="text" />
                <button type="submit">Search</button>
            </form>
        <div id="list-widgie" class="content scaffold-list" role="main">
            <g:if test="${params.query}">
                <h1>'${params.query}' <g:message code="default.list.label" args="[entityName]" /></h1>
            </g:if>
            <g:else>
                <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            </g:else>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <f:table collection="${widgieList}" />

            <div class="pagination">
                <g:paginate total="${widgieCount ?: 0}" />
            </div>
        </div>
    </body>
</html>
