package mock.auth

import com.example.domain.UserPrincipal
import com.example.domain.UserType
import io.ktor.server.auth.jwt.*
import java.util.*

fun createMockUserPrincipalFromCredential(credential: JWTCredential): UserPrincipal? {
    val userId = credential.payload.getClaim("userId").asString()
    val userType = credential.payload.getClaim("type").asString()
    val isPremium = credential.payload.getClaim("isPremium").asBoolean()

    return if (userId != null && userType != null && isPremium != null) {
        UserPrincipal(
            id = UUID.fromString(userId),
            type = UserType.valueOf(userType),
            isPremium = isPremium
        )
    } else null
}