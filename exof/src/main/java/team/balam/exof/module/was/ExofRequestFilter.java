package team.balam.exof.module.was;

import lombok.extern.slf4j.Slf4j;
import team.balam.exof.module.service.ServiceNotFoundException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class ExofRequestFilter implements Filter {
    private WebServlet oldWebServlet = new WebServlet();

    @Override
    public void init(FilterConfig filterConfig) {
        String servicePathExtractorClassName = filterConfig.getInitParameter("servicePathExtractor");
        oldWebServlet.setServicePathExtractor(servicePathExtractorClassName);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            oldWebServlet.execute((HttpServletRequest) request, (HttpServletResponse) response);
        } catch (ServiceNotFoundException e) {
            chain.doFilter(request, response);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void destroy() {

    }
}
