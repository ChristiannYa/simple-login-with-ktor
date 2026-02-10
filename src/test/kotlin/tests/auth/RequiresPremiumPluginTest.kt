package tests.auth

import com.example.plugins.configureSerialization
import com.example.plugins.withAuth
import com.example.plugins.withPremium
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import mock.auth.createMockJwtVerifier
import mock.auth.createMockTestToken
import mock.auth.createMockUserPrincipalFromCredential
import mock.auth.mockJwtContent
import mock.user.mockPremiumUserPrincipal
import mock.user.mockUserPrincipal
import kotlin.test.Test
import kotlin.test.assertEquals

class RequiresPremiumPluginTest {
    @Test
    fun `test RequiresPremiumPlugin allows premium users`() = testApplication {
        application {
            configureSerialization()

            this@application.install(Authentication.Companion) {
                jwt("auth-jwt") {
                    realm = mockJwtContent.realm
                    verifier(createMockJwtVerifier(mockJwtContent))
                    validate { createMockUserPrincipalFromCredential(it) }
                }
            }

            routing {
                withAuth {
                    withPremium {
                        get("/premium-content") {
                            call.respond(HttpStatusCode.Companion.OK, "Success")
                        }
                    }
                }
            }
        }

        val token = createMockTestToken(mockPremiumUserPrincipal, mockJwtContent)

        val response = client.get("/premium-content") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.Companion.OK, response.status)
    }

    @Test
    fun `test RequiresPremiumPlugin blocks non-premium users`() = testApplication {
        application {
            configureSerialization()

            this@application.install(Authentication.Companion) {
                jwt("auth-jwt") {
                    realm = mockJwtContent.realm
                    verifier(createMockJwtVerifier(mockJwtContent))
                    validate { createMockUserPrincipalFromCredential(it) }
                }
            }

            routing {
                withAuth {
                    withPremium {
                        get("premium-content") {
                            call.respond(HttpStatusCode.Companion.OK, "Premium content")
                        }
                    }
                }
            }
        }

        val token = createMockTestToken(mockUserPrincipal, mockJwtContent)

        val response = client.get("/premium-content") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.Companion.Forbidden, response.status)
    }
}