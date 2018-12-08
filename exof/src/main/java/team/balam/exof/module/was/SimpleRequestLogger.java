package team.balam.exof.module.was;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * http 정보를 간단하게 로그로 남겨준다.<br>
 *
 * 예)<br>
 * &ensp; GET /index.html HTTP/1.1<br>
 * &ensp; Connection: close<br>
 * &ensp; Host: localhost<br>
 */
@Slf4j
public class SimpleRequestLogger implements ServicePathExtractor {
    @Override
    public String extract(HttpServletRequest request) {
        StringBuilder header = new StringBuilder();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            header.append(name).append(": ").append(request.getHeader(name)).append("\n");
        }

        log.info("http request ====>\n{} {} {}\n{}", request.getMethod(), request.getRequestURI(), request.getProtocol(), header);
        return request.getRequestURI();
    }
}
