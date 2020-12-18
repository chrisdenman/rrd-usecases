package uk.co.ceilingcat.rrd.usecases

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.time.LocalDate

@TestInstance(PER_CLASS)
internal class CurrentDateTests {

    @Test
    fun `That createCurrentDate() returns instances with ane values`() {
        createCurrentDate().run {
            localDate <= LocalDate.now()
        }
    }
}
