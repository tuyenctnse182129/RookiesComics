package application.aicomic.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Không cần xử lý gì khi khởi tạo filter, nhưng vẫn phải override
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        log.info("Nhận request từ MoMo: {} {}", req.getMethod(), req.getRequestURI());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Không cần xử lý gì khi filter bị hủy, nhưng vẫn phải override
    }
}

