package uk.co.ceilingcat.rrd.usecases

import arrow.core.Either
import uk.co.ceilingcat.rrd.entities.ServiceDetails

/**
 * An abstract input gateway.
 */
interface InputGateway

/**
 * An input gateway that produces details of the next upcoming service.
 */
interface NextUpcomingInputGateway : InputGateway {

    fun nextUpcoming(): Either<Throwable, ServiceDetails?>
}

/**
 * An abstract output gateway.
 */
interface OutputGateway<T>

/**
 * An output gateway that produces notifications of a service.
 */
interface UpcomingOutputGateway : OutputGateway<ServiceDetails> {

    // @todo return Throwable?
    fun notify(serviceDetails: ServiceDetails): Either<Throwable, Unit>
}
