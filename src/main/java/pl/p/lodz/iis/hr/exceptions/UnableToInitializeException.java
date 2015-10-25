package pl.p.lodz.iis.hr.exceptions;

import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnableToInitializeException extends RuntimeException {

    private static final long serialVersionUID = 7987593403448732742L;
    private static final Logger LOGGER = LoggerFactory.getLogger(UnableToInitializeException.class);

    public UnableToInitializeException(Class<?> clazz, @NonNls String message, Throwable cause) {
        super("Unable to initialize! " + message, cause);

        LOGGER.error(String.format("Unable to initialize %s! Exception thrown.", clazz.getSimpleName()));
        LOGGER.error(String.format("Message: %s.", message));
        LOGGER.error(String.format("Cause.toString(): %s.", cause.toString()));
    }
}
