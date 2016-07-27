Todo:
 * Setup Maven run configuration (Specify -Dpippo.mode=dev VM arg)
 * Do more advanced template usage (proper HTML, etc)
    * Serving static files
    * Forms (CSRF)
 * Add authentication
     * Users DB table
     * UsersController
     * Forms for sign up and login
     * Throttle logins/signups based on IP
     * Use Mailgun to send emails (confirmation, password reset)
     * Add form validation

 Done:
 * Setup DB:
    * H2 for DB
    * Sql2o for DB queries
    * Flyway for migrations
    * HikariCP for connection pooling
 * Investigate Log4j Rollover
 * Switch from Freemarker to Pebble
 * Try Controller routing
 * Setup Guice for DI
 
 Possible Pippo Contributions:
 * Bugfix with updated Pebble template dev mode cache
 * Add JavaDoc (such as to RouteContext)

Properties available in template:
 * session (if present)
 * flash
 * prettyTime
 * formatTime
 * webjarsAt
 * publicAt
 * i18n
