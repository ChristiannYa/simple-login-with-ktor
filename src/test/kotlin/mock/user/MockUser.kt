package mock.user

import com.example.domain.UserPrincipal
import com.example.domain.UserType
import java.util.*

val mockUserPrincipal = UserPrincipal(
    id = UUID.randomUUID(),
    type = UserType.USER,
    isPremium = false
)

val mockPremiumUserPrincipal = UserPrincipal(
    id = UUID.randomUUID(),
    type = UserType.USER,
    isPremium = true
)