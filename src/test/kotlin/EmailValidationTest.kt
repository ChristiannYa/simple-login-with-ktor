import com.example.auth.isValidEmail
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmailValidationTest {

    @Test
    fun `valid email addresses should return true`() {
        val validEmails = listOf(
            "test@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "user_name@example-domain.com",
            "123@example.com",
            "a@example.io",
            "test@sub.domain.example.com",
            "user@example123.com"
        )

        validEmails.forEach { email ->
            assertTrue(email.isValidEmail(), "Expected '$email' to be valid")
        }
    }

    @Test
    fun `invalid email addresses should return false`() {
        val invalidEmails = listOf(
            "notanemail",              // No @ symbol
            "@example.com",            // No local part
            "user@",                   // No domain
            "user@.com",               // Domain starts with dot
            "user@domain",             // No TLD
            "user name@example.com",   // Space in local part
            "user@domain .com",        // Space in domain
            "",                        // Empty string
            "user@@example.com",       // Double @
            "user@domain..com"         // Double dot in domain
        )

        invalidEmails.forEach { email ->
            assertFalse(email.isValidEmail(), "Expected '$email' to be invalid")
        }
    }

    @Test
    fun `edge case emails`() {
        // Very long but valid
        assertTrue("a@${"b".repeat(63)}.com".isValidEmail())

        // Valid special characters in local part
        assertTrue("user+tag@example.com".isValidEmail())
        assertTrue("user.name@example.com".isValidEmail())
        assertTrue("user_name@example.com".isValidEmail())

        // Invalid - local part too long (>256 chars)
        assertFalse("${"a".repeat(257)}@example.com".isValidEmail())
    }
}