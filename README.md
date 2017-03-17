# Example of Java KeyCloak Client using password Grant

This is an example of how to set up a standalone Java application to connect to a Keycloak server to obtain tokens using the OpenID Connect's [Resource Owner Password Credentials Grant](http://oauthlib.readthedocs.io/en/latest/oauth2/grants/password.html).

The implemented code is mostly based on [this sample](https://github.com/wpic/sample-keycloak-getting-token).


## Covered use-cases
This is particularly suitable in these use-cases :

 * A Machine-to-Machine scenario where a machine (standalone app, server, ..) wants to use another machine's API through an API Gateway (APIMan for instance, which is protected by Keycloak).
 * A Machine-to-Machine scenario where a machine (standalone app, server, ..) wants to user another machine directly (although protected by a keycloak filter)

In these use case :

 - No browser or UI is available.
 - Trust relationship between the client and the backend (privileged application)


## Pre-requesite :

- a keycloak server is up and running (on localhost). Realm, and client is configured
- a backend (API server) is publishing its API (or any other endpoint.
