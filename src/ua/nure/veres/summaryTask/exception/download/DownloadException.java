package ua.nure.veres.summaryTask.exception.download;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on download error.
 */
public class DownloadException extends AppException {

    private static final long serialVersionUID = 999123656758456L;

    public static final String MESSAGE_DOWNLOAD_ERROR_OCCURRED
            = "Cannot download the file: server problem was occurred";

    public DownloadException(String message) {
        super(message);
    }

}
