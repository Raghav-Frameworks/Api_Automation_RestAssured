package com.framework.core.filters;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Retries requests when server responds with 429/5xx codes.
 * Uses exponential backoff + jitter.
 */
public class RetryFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(RetryFilter.class);

    private final int maxRetries;
    private final long baseDelayMs;
    private final List<Integer> retryStatusCodes = List.of(429, 500, 502, 503, 504);

    public RetryFilter(int maxRetries, long baseDelayMs) {
        this.maxRetries = maxRetries;
        this.baseDelayMs = baseDelayMs;
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        int attempt = 0;
        Response response;
        while (true) {
            attempt++;
            response = ctx.next(requestSpec, responseSpec);
            int code = response.getStatusCode();
            if (!retryStatusCodes.contains(code) || attempt > maxRetries) {
                return response;
            }
            long sleep = backoff(attempt);
            log.warn("HTTP {} received (attempt {}/{}) - retrying in {} ms",
                    code, attempt, maxRetries, sleep);
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return response;
            }
        }
    }

    private long backoff(int attempt) {
        long expo = (long) (baseDelayMs * Math.pow(2, attempt - 1));
        long jitter = ThreadLocalRandom.current().nextLong(0, baseDelayMs);
        return Math.min(expo + jitter, 15000); // cap wait at 15s
    }
}
