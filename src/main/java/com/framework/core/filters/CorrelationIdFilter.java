package com.framework.core.filters;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * Adds a correlation ID header to every request
 * and logs it in MDC for tracking.
 */
public class CorrelationIdFilter implements Filter {
    public static final String HEADER = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        String cid = UUID.randomUUID().toString();
        requestSpec.header(HEADER, cid);
        MDC.put(MDC_KEY, cid);
        try {
            return ctx.next(requestSpec, responseSpec);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
