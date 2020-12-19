package uk.co.ceilingcat.rrd.usecases

import java.time.LocalDate

/**
 * Represents a use case that produces the current local date.
 */
interface CurrentDate : UseCase {
    /**
     * Produce the current local date.
     */
    val localDate: LocalDate
}

/**
 * Creates a `CurrentDate` use case.
 */
fun createCurrentDate(): CurrentDate = CurrentDateUseCase()

private class CurrentDateUseCase : CurrentDate {
    override val localDate: LocalDate
        get() = LocalDate.now()
}
