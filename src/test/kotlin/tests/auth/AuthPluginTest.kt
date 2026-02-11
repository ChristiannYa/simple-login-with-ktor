package tests.auth

import com.example.config.userPrincipal
import com.example.dto.DtoRes
import com.example.plugins.configureSerialization
import com.example.plugins.scoped.AuthPlugin
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
import mock.auth.mockJwtPayload
import mock.user.mockUserPrincipal
import org.junit.Test
import kotlin.test.assertEquals

class AuthPluginTest {
    @Test
    fun `test AuthPlugin with valid token stores UserPrincipal`() = testApplication {
        application {
            configureSerialization()

            this@application.install(Authentication.Companion) {
                jwt("auth-jwt") {
                    realm = mockJwtPayload.realm
                    verifier(createMockJwtVerifier(mockJwtPayload))
                    validate { createMockUserPrincipalFromCredential(it) }
                }
            }

            routing {
                authenticate("auth-jwt") {
                    install(AuthPlugin)

                    get("/test") {
                        val user = call.userPrincipal
                        call.respond(
                            HttpStatusCode.Companion.OK,
                            mapOf("user_id" to user.id.toString())
                        )
                    }
                }
            }
        }

        val token = createMockTestToken(mockUserPrincipal, mockJwtPayload)

        val response = client.get("/test") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.Companion.OK, response.status)
    }

    @Test
    fun `test AuthPlugin without token returns 401`() = testApplication {
        application {
            configureSerialization()

            this@application.install(Authentication.Companion) {
                jwt("auth-jwt") {
                    realm = mockJwtPayload.realm
                    verifier(createMockJwtVerifier(mockJwtPayload))
                    validate { createMockUserPrincipalFromCredential(it) }
                    challenge { _, _ ->  // defaultScheme, realm
                        call.respond(
                            HttpStatusCode.Companion.Unauthorized,
                            DtoRes.error("token is not valid or has expired")
                        )
                    }
                }
            }

            routing {
                authenticate("auth-jwt") {
                    install(AuthPlugin)

                    get("/test") {
                        call.respond(HttpStatusCode.Companion.OK, "Success")
                    }
                }
            }
        }

        val response = client.get("/test")

        assertEquals(HttpStatusCode.Companion.Unauthorized, response.status)
    }

    @Test
    fun `test AuthPlugin with invalid token returns 401`() = testApplication {
        application {
            configureSerialization()

            this@application.install(Authentication.Companion) {
                jwt("auth-jwt") {
                    realm = mockJwtPayload.realm
                    verifier(createMockJwtVerifier(mockJwtPayload))
                    validate { createMockUserPrincipalFromCredential(it) }
                    challenge { _, _ ->
                        call.respond(
                            HttpStatusCode.Companion.Unauthorized,
                            DtoRes.error("token is not valid or has expired")
                        )
                    }
                }
            }

            routing {
                authenticate("auth-jwt") {
                    install(AuthPlugin)

                    get("/test") {
                        call.respond(HttpStatusCode.Companion.OK, "Success")
                    }
                }
            }
        }

        val response = client.get("/test") {
            header(HttpHeaders.Authorization, "Bearer invalid-token-here")
        }

        assertEquals(HttpStatusCode.Companion.Unauthorized, response.status)
    }
}