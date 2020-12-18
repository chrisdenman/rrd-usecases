@file:Suppress("unused")

package uk.co.ceilingcat.rrd.usecases

import arrow.core.Either
import arrow.core.Either.Companion.left
import arrow.core.Either.Companion.right
import uk.co.ceilingcat.rrd.entities.ServiceDetails
import uk.co.ceilingcat.rrd.usecases.NotifyOfNextServiceDateException.AllInputGatewaysFailedException
import uk.co.ceilingcat.rrd.usecases.NotifyOfNextServiceDateException.AllOutputGatewaysFailedException
import uk.co.ceilingcat.rrd.usecases.NotifyOfNextServiceDateException.NoInputGatewaysException
import uk.co.ceilingcat.rrd.usecases.NotifyOfNextServiceDateException.NoOutputGatewaysException

// @todo return Throwable?
interface NotifyOfNextServiceDate : UseCase {
    fun execute(): Either<Throwable, Unit>
}

sealed class NotifyOfNextServiceDateException : Throwable() {
    object NoInputGatewaysException : NotifyOfNextServiceDateException()
    object NoOutputGatewaysException : NotifyOfNextServiceDateException()
    object AllInputGatewaysFailedException : NotifyOfNextServiceDateException()
    object AllOutputGatewaysFailedException : NotifyOfNextServiceDateException()
}

typealias NotifyOfNextServiceDateError = NotifyOfNextServiceDateException
typealias NoInputGatewaysError = NoInputGatewaysException
typealias NoOutputGatewaysError = NoOutputGatewaysException
typealias AllInputGatewaysFailedError = AllInputGatewaysFailedException
typealias AllOutputGatewaysFailedError = AllOutputGatewaysFailedException

internal fun createNotifyOfNextServiceDate(
    currentDate: CurrentDate,
    inputGateways: List<NextUpcomingInputGateway>,
    outputGateways: List<UpcomingOutputGateway>
): Either<NotifyOfNextServiceDateError, NotifyOfNextServiceDate> =
    when {
        inputGateways.isEmpty() -> left(NoInputGatewaysError)
        outputGateways.isEmpty() -> left(NoOutputGatewaysError)
        else -> right(NotifyOfNextServiceDateUseCase(currentDate, inputGateways, outputGateways))
    }

private class NotifyOfNextServiceDateUseCase(
    private val currentDate: CurrentDate,
    private val inputGateways: List<NextUpcomingInputGateway>,
    private val outputGateways: List<UpcomingOutputGateway>,
) : NotifyOfNextServiceDate {

    private data class InputGatewayRecord(
        val atLeastOneSuccess: Boolean,
        val nextUpcoming: ServiceDetails?
    )

    override fun execute(): Either<NotifyOfNextServiceDateError, Unit> =
        inputGateways.fold(
            InputGatewayRecord(false, null)
        ) { acc, curr ->
            curr.nextUpcoming().fold({ acc }) {
                InputGatewayRecord(
                    true,
                    if ((it != null) && filterByDate(it) && (
                        (acc.nextUpcoming == null) || (acc.nextUpcoming.date > it.date)
                        )
                    ) it else acc.nextUpcoming
                )
            }
        }.run {
            if (!atLeastOneSuccess) {
                left(AllInputGatewaysFailedError)
            } else {
                if (nextUpcoming != null) {
                    if (!outputGateways.fold(false) { acc, curr ->
                        curr.notify(nextUpcoming).fold({ acc }, { true })
                    }
                    )
                        left(AllOutputGatewaysFailedError)
                    else
                        right(Unit)
                } else {
                    right(Unit)
                }
            }
        }

    private val acquireCurrentDate
        get() = currentDate.localDate

    private fun filterByDate(serviceDetails: ServiceDetails) =
        (serviceDetails.date == acquireCurrentDate.plusDays(DAYS_HENCE_TO_NOTIFY_OF))

    companion object {
        private const val DAYS_HENCE_TO_NOTIFY_OF = 1L
    }
}
