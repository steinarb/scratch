package no.priv.bang.modeling.modelstore.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * An immutable bean that is used to hold error messages logged when modelstore
 * catches (and represses) exceptions.
 *
 * @author Steinar Bang
 *
 */
public class ErrorBean {

    private final Date date;
    private final String message;
    private final String fileOrStream;
    private final Exception exception;

    /**
     * Create an ErrorBean instances.
     *
     * @param date the date and time when the error occurred, with millisecond accuracy
     * @param message a human readable message describing where in the code the exception occurred
     * @param fileOrStream a string description of the File or {@link InputStream}/{@link OutputStream} object involved, or null if no such object was involved
     * @param exception the caught exception causing this error to be logged
     */
    public ErrorBean(Date date, String message, Object fileOrStream, Exception exception) {
        this.date = date;
        this.message = message;
        this.fileOrStream = fileOrStream == null ? null : fileOrStream.toString();
        this.exception = exception;
    }

    /**
     * Error time stamp
     *
     * @return a {@link Date} object describing a date and time with millisecond accuracy
     */
    public Date getDate() {
        return date;
    }

    /**
     * Human readable error message
     *
     * @return a {@link String} describing the error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * The File or {@link InputStream}/{@link OutputStream} object involved in
     * the error.
     *
     * @return a {@link String} description of the file/stream object involved in the error, or null if none are involved
     */
    public String getFileOrStream() {
        return fileOrStream;
    }

    /**
     * The exception that was caught and caused the error to be logged.
     *
     * @return an {@link Exception} object
     */
    public Exception getException() {
        return exception;
    }

}
