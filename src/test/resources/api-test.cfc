<cfcomponent>
    <cffunction name="health">
        <cfreturn "status:ok"/>
    </cffunction>

    <cffunction name="echo">
        <cfargument name="message" type="String" required="true">
        <cfreturn "echo: #message#"/>
    </cffunction>
</cfcomponent>