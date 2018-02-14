package team.balam.exof.module.was;

import javax.servlet.http.HttpServletRequest;

public interface ServicePathExtractor {
	String extract(HttpServletRequest request);
}
