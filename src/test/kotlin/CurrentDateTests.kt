package uk.co.ceilingcat.rrd.usecases

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.time.LocalDate

@TestInstance(PER_CLASS)
internal class CurrentDateTests {

    @Test
    fun `That createCurrentDate() returns instances with vaguely sane values`() {
        val then = LocalDate.now()
        createCurrentDate().run {
            val result = localDate
            val now = LocalDate.now()
            Assertions.assertTrue(then <= result)
            Assertions.assertTrue(now >= result)
        }
    }
}
