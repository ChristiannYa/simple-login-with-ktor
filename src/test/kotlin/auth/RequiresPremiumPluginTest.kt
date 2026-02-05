package auth

import com.example.plugins.configureSerialization
import com.example.plugins.requiresPremium
import com.example.plugins.withAuth
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
import mock.auth.mockJwtConfig
import mock.user.mockPremiumUserPrincipal
import mock.user.mockUserPrincipal
import kotlin.test.Test
import kotlin.test.assertEquals

class RequiresPremiumPluginTest {
    @Test
    fun `test RequiresPremiumPlugin allows premium users`() = testApplication {
        application {
            configureSerialization()

            this@application.install(Authentication) {
                jwt("auth-jwt") {
                    realm = mockJwtConfig.realm
                    verifier(createMockJwtVerifier(mockJwtConfig))
                    validate { createMockUserPrincipalFromCredential(it) }
                }
            }

            routing {
                withAuth {
                    requiresPremium {
                        get("/premium-content") {
                            call.respond(HttpStatusCode.OK, "Success")
                        }
                    }
                }
            }
        }

        val token = createMockTestToken(mockPremiumUserPrincipal, mockJwtConfig)

        val response = client.get("/premium-content") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test RequiresPremiumPlugin blocks non-premium users`() = testApplication {
        application {
            configureSerialization()

            this@application.install(Authentication) {
                jwt("auth-jwt") {
                    realm = mockJwtConfig.realm
                    verifier(createMockJwtVerifier(mockJwtConfig))
                    validate { createMockUserPrincipalFromCredential(it) }
                }
            }

            routing {
                withAuth {
                    requiresPremium {
                        get("premium-content") {
                            call.respond(HttpStatusCode.OK, "Premium content")
                        }
                    }
                }
            }
        }

        val token = createMockTestToken(mockUserPrincipal, mockJwtConfig)

        val response = client.get("/premium-content") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
    }
}