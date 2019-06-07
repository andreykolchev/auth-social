package com.pirates.auth.exception

enum class ErrorType constructor(val code: String, val message: String) {
    INVALID_JSON_TYPE("00.00", "Invalid type: "),
    DATA_NOT_FOUND("00.01", "Data not found."),
    CONTEXT("00.02", "Context parameter not found."),
    INVALID_USER("00.03", "Invalid userId."),
    INVALID_PUBLIC_KEY_FORMAT("00.04", "Invalid public key format."),
    INVALID_PRIVATE_KEY_FORMAT("00.05", "Invalid private key format."),
    TOKEN_VERIFICATION_ERROR("00.06", "Error of verification the token."),
    INVALID_TOKEN_TYPE("00.07", "Invalid token type."),
    TOKEN_EXPIRED("00.08", "The token is expired."),
    INVALID_OPERATION_ID("00.09", "Invalid operation id."),
    INVALID_PASSWORD("00.10", "Invalid password."),
    INVALID_DATA("00.11", "Invalid data."),
    EMAIL_ALREADY_EXISTS("00.12", "Email already exists.");
}
