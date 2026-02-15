package com.khmil.crm.ocr.service.util;

public final class Constants {
    private Constants() {
    }

    public static final String API_BASE_PATH_V1_OCR = "/v1/ocr";
    public static final String PROPERTY_PREFIX_APP_OCR = "app.ocr";
    public static final String REQUEST_PARAM_FILE = "file";
    public static final String REQUEST_PARAM_LANG = "lang";
    public static final String OUTPUT_FILE_NAME = "ocr-searchable.pdf";
    public static final String CONTENT_DISPOSITION_INLINE = "inline; filename=";

    public static final String DEFAULT_LANGUAGE = "eng";
    public static final String OCR_COMMAND_TEMPLATE_DEFAULT = "ocrmypdf --skip-text %s %s";
    public static final String PLACEHOLDER_STRING = "%s";
    public static final int TWO_PLACEHOLDERS = 2;
    public static final int THREE_PLACEHOLDERS = 3;
    public static final String LANGUAGE_PATTERN = "^[a-z0-9_+]+$";

    public static final String TMP_DIR = "/tmp/ocr";
    public static final String INPUT_FILE_PREFIX = "input-";
    public static final String OUTPUT_FILE_PREFIX = "output-";
    public static final String PDF_EXTENSION = ".pdf";

    public static final String WINDOWS_OS_NAME_TOKEN = "win";
    public static final String OS_NAME_PROPERTY = "os.name";
    public static final String EMPTY_STRING = "";

    public static final String WINDOWS_SHELL = "cmd.exe";
    public static final String WINDOWS_SHELL_FLAG = "/c";
    public static final String UNIX_SHELL = "sh";
    public static final String UNIX_SHELL_FLAG = "-c";

    public static final String WINDOWS_DOUBLE_QUOTE = "\"";
    public static final String WINDOWS_ESCAPED_DOUBLE_QUOTE = "\\\"";
    public static final String UNIX_SINGLE_QUOTE = "'";
    public static final String UNIX_ESCAPED_SINGLE_QUOTE = "'\"'\"'";

    public static final String ERROR_PROCESS_INTERRUPTED = "OCR process interrupted";
    public static final String ERROR_INVALID_COMMAND_PLACEHOLDERS = "app.ocr.command must contain 2 or 3 '%s' placeholders";
    public static final String ERROR_INVALID_LANGUAGE = "Invalid OCR language code format";
    public static final String ERROR_OCR_FAILED_PREFIX = "ocrmypdf failed (exit ";
    public static final String ERROR_OCR_FAILED_MIDDLE = "): ";

    public static final String ERROR_EMPTY_FILE = "No file uploaded or file is empty";
    public static final String ERROR_BAD_REQUEST_LOG = "Bad request: {}";
    public static final String ERROR_IO_LOG = "OCR IO error";
    public static final String ERROR_IO_RESPONSE_PREFIX = "OCR failed: ";
    public static final String ERROR_UNEXPECTED_LOG = "Unexpected error";
    public static final String ERROR_UNEXPECTED_RESPONSE = "Unexpected error occurred";

    public static final String LOG_OCR_STARTED = "OCR started; token={}, inputBytes={}, lang={}";
    public static final String LOG_OCR_COMPLETED = "OCR completed; token={}, outputBytes={}";
    public static final String LOG_EXECUTING_COMMAND = "Executing OCR command; input={}, output={}, lang={}";
    public static final String LOG_COMMAND_DEBUG = "OCR shell command: {}";
    public static final String LOG_COMMAND_FAILED = "OCR command failed; token={}, exitCode={}, output={}";
    public static final String LOG_COMMAND_SUCCESS = "OCR command finished successfully; token={}, exitCode={}";
    public static final String LOG_TEMP_CLEANUP = "OCR temp cleanup; token={}, inputDeleted={}, outputDeleted={}";
    public static final String LOG_TEMP_CLEANUP_FAILED = "OCR temp cleanup failed; token={}, message={}";
}
