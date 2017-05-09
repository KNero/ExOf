package team.balam.exof.container.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import team.balam.exof.Constant;
import team.balam.exof.util.StreamUtil;

public class WebServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private ConsoleCommandHandler commandHandler;
	public static final String LOGIN_SUCCESS = "login_success";
	
	public WebServlet() {
		super();
		this.commandHandler = new ConsoleCommandHandler();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		InputStream bodyIn = null;
		
		try {
			bodyIn = req.getInputStream();
			byte[] buf = StreamUtil.read(bodyIn, bodyIn.available());
			
			String res = this.commandHandler.executeConsoleService(new String(buf));
			
			if (LOGIN_SUCCESS.equals(res)) {
				req.getSession().setAttribute(LOGIN_SUCCESS, Constant.YES);
			} 

			PrintWriter out = resp.getWriter();
			out.write(res);
			out.flush();
		} catch(Exception e) {
			throw new ServletException(e);
		} finally {
			if (bodyIn != null) {
				bodyIn.close();
			}
		}
	}
}
