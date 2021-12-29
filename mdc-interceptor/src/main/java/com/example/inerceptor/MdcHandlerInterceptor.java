package com.example.inerceptor;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.example.Constants.CORRELATION_ID_HEADER;
import static com.example.Constants.CORRELATION_ID_KEY;

@Service
@RequiredArgsConstructor
public class MdcHandlerInterceptor implements HandlerInterceptor {

    private final IdGenerator idGenerator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            MDC.put(CORRELATION_ID_KEY, idGenerator.generateId().toString());
        } else {
            MDC.put(CORRELATION_ID_KEY, correlationId);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.remove(CORRELATION_ID_KEY);
    }
}
