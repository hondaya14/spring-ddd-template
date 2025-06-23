package com.nqvno.springdddtemplate.api.handler

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class ApiExceptionHandler(private val tracer: Tracer) : ResponseEntityExceptionHandler() {

    // MethodNotSupported: 400
    override fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return handleExceptionInternal(ex, BAD_REQUEST, headers, HttpStatus.BAD_REQUEST, request)
    }

    // MissingServletRequestParameter: 400
    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return handleExceptionInternal(ex, BAD_REQUEST, headers, HttpStatus.BAD_REQUEST, request)
    }

    // MethodArgumentNotValid: 400
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return handleExceptionInternal(ex, BAD_REQUEST, headers, HttpStatus.BAD_REQUEST, request)
    }

    // NoResourceFoundException: 400
    override fun handleNoResourceFoundException(
        ex: NoResourceFoundException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return handleExceptionInternal(ex, BAD_REQUEST, headers, HttpStatus.BAD_REQUEST, request)
    }

    // HttpMessageNotReadable: 400
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return handleExceptionInternal(ex, BAD_REQUEST, headers, HttpStatus.BAD_REQUEST, request)
    }

    // HttpMediaTypeNotSupported: 400
    override fun handleHttpMediaTypeNotSupported(
        ex: HttpMediaTypeNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return handleExceptionInternal(ex, BAD_REQUEST, headers, HttpStatus.BAD_REQUEST, request)
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {

        if (statusCode.is4xxClientError) {
            logger.warn(ex.message, ex)
        } else if (statusCode.is5xxServerError) {
            logger.error(ex.message, ex)
        }

        return ResponseEntity(body, headers, statusCode)
    }
}
