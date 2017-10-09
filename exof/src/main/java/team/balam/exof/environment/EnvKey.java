package team.balam.exof.environment;

/**
 * 설정 파일의 키값을 저장.
 * @author kwonsm
 *
 */
public interface EnvKey {
	String HOME = "exof.home";

	interface Framework {
		String FRAMEWORK = "framework";
		String CONTAINER = "container";
		String SCHEDULER = "scheduler";
		
		String AUTORELOAD_SCHEDULER = "autoReload.scheduler";
		String AUTORELOAD_SERVICE_VARIABLE = "autoReload.serviceVariable";
		String EXTERNAL = "external";
	}
	
	interface Service {
		String SERVICES = "services";

		String SERVICE_PACKAGE = "servicePackage";
		String SERVICE_DIRECTORY = "serviceDirectory";
		String CLASS = "class";
		String PATH = "path";
		String PACKAGE = "package";
		
		String SERVICE_VARIABLE = "serviceVariable";
		String SERVICE_NAME = "serviceName";
		
		String VARIABLE = "variable";
		String NAME = "name";
		String VALUE = "value";
		
		String RESOURCE = "resource";
		String FILE = "file";
		
		String SCHEDULER = "scheduler";
		String ID = "id";
		String SERVICE_PATH = "servicePath";
		String CRON = "cron";
		String DUPLICATE_EXECUTION = "duplicateExecution";
		String USE = "use";
		String INIT_EXECUTION = "initExecution";
	}
	
	interface Listener {
		String LISTENER = "listener";
		String PORT = "port";
		String NUMBER = "number";
		String TYPE = "type";
		String WORKER_SIZE = "workerSize";
		String MAX_LENGTH = "maxLength";
		
		String CHANNEL_HANDLER = "channelHandler";
		String MESSAGE_TRANSFORM = "messageTransform";
		String SESSION_HANDLER = "sessionHandler";
		String CLASS = "class";

		String LENGTH_OFFSET = "lengthOffset";
		String LENGTH_SIZE = "lengthSize";
		
		String SSL = "ssl";
		String CERTIFICATE_PATH = "certificatePath";
		String PRIVATE_KEY_PATH = "privateKeyPath";
		
		String CONSOLE = "console";
		String ADMIN_CONSOLE = "adminConsole";
		String DEPLOY = "deploy";
		String ID = "id";
		String PASSWORD = "password";
		
		String JETTY = "jetty";
		String HTTP = "http";
		String HTTPS = "https";
		String MAX_IDLE = "maxIdleTime";
		String HEADER_SIZE = "requestHeaderSize";
		String DESCRIPTOR = "descriptor";
		String RESOURCE_BASE = "resourceBase";
		String CONTEXT_PATH = "contextPath";
		String SSL_CONTEXT = "sslContextClass";
	}
}
