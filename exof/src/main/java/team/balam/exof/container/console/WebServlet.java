package team.balam.exof.container.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.function.Function;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import team.balam.exof.Constant;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.util.StreamUtil;

public class WebServlet extends HttpServlet implements Function<Command, Boolean> {
	private static final long serialVersionUID = 1L;
	
	private ConsoleCommandHandler commandHandler;
	public static final String LOGIN_SUCCESS = "login_success";
	
	public WebServlet() {
		super();
		this.commandHandler = new ConsoleCommandHandler();
		this.commandHandler.setFilter(this);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestContext.set(RequestContext.HTTP_SERVLET_REQ, req);
		RequestContext.set(RequestContext.HTTP_SERVLET_RES, resp);
		resp.setContentType("text/plain; charset=utf-8");
		
		InputStream bodyIn = null;
		
		try {
			bodyIn = req.getInputStream();
			byte[] buf = StreamUtil.read(bodyIn, bodyIn.available());
			
			String res = this.commandHandler.executeConsoleService(new String(buf));
			
			if (res != null) {
				PrintWriter out = resp.getWriter();
				out.write(res);
				out.flush();
			}
		} catch(Exception e) {
			throw new ServletException(e);
		} finally {
			RequestContext.remove();
			
			if (bodyIn != null) {
				bodyIn.close();
			}
		}
	}

	@Override
	public Boolean apply(Command t) {
		if (ServiceList.LOGIN_ADMIN_CONSOLE.value().equals(t.getType())) {
			return true;
		} else {
			HttpServletRequest httpReq = RequestContext.get(RequestContext.HTTP_SERVLET_REQ);
			if (Constant.YES.equals(httpReq.getSession().getAttribute(WebServlet.LOGIN_SUCCESS))) {
				return true;
			}
		}
		
		return false;
	}
}
