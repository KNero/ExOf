package team.balam.exof.module.service.component.http;

public enum HttpMethod {
	GET, POST, PUT, DELETE, PATCH;

	public HttpMethod get(String method) {
		switch(method) {
			case "GET":
				return GET;
			case "POST":
				return POST;
			case "PUT":
				return PUT;
			case "DELETE":
				return DELETE;
			case "PATCH":
				return PATCH;
			default:
				throw new UnsupportedOperationException("method:" + method);
		}
	}
}
