package team.balam.exof.module.service;

import team.balam.exof.Constant;
import team.balam.exof.module.service.annotation.RestServices;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.Services;
import team.balam.exof.module.service.component.http.RestService;

import java.lang.reflect.Method;
import java.util.*;

public class TreeServiceNodeMaker {
//    public static final Map<Class, Function<Method, String>> SERVICE_NAME_GETTER = new HashMap<>();
//    static {
//        SERVICE_NAME_GETTER.put(Service.class, method ->  {
//            Service serviceAnnotation = method.getAnnotation(Service.class);
//            String serviceName = "";
//
//            if (!serviceAnnotation.value().isEmpty()) {
//                serviceName = serviceAnnotation.value();
//            }
//
//            if (!serviceAnnotation.name().isEmpty()) {
//                serviceName = serviceAnnotation.name();
//            }
//
//            return serviceName;
//        });
//        SERVICE_NAME_GETTER.put(RestService.class, method -> {
//            RestService serviceAnnotation = method.getAnnotation(RestService.class);
//            return serviceAnnotation.name();
//        });
//    }

    private TreeServiceNodeMaker() {

    }

    static List<ServiceGroup> make(ServiceDirectory serviceDirectory) throws Exception {
        HashMap<String, ServiceGroup> groupList = new HashMap<>();

        Class serviceDirectoryClass = serviceDirectory.getHost().getClass();
        Method[] methods = serviceDirectoryClass.getMethods();

        for (Method method : methods) {
            Set<String> serviceNameList = getServiceName(method);
            serviceNameList.addAll(getRestServiceName(method));

            for (String serviceName : serviceNameList) {
                ServiceGroup group = groupList.getOrDefault(serviceName, new ServiceGroup(serviceDirectory.getHost(), serviceName));
                group.add(method, serviceDirectory.isInternal());

                groupList.putIfAbsent(serviceName, group);
            }
        }

        return new ArrayList<>(groupList.values());
    }

    private static Set<String> getServiceName(Method method) {
        Set<String> list = new HashSet<>();

        Service service = method.getAnnotation(Service.class);
        if (service != null) {
            list.add(getServiceName(service));
        }

        Services services = method.getAnnotation(Services.class);
        if (services != null) {
            for (Service ann : services.value()) {
                list.add(getServiceName(ann));
            }
        }

        return list;
    }

    private static String getServiceName(Service serviceAnnotation) {
        String serviceName = "";

        if (!serviceAnnotation.value().isEmpty()) {
            serviceName = serviceAnnotation.value();
        }

        if (!serviceAnnotation.name().isEmpty()) {
            serviceName = serviceAnnotation.name();
        }

        return standardizeServiceName(serviceName);
    }

    private static Set<String> getRestServiceName(Method method) {
        Set<String> list = new HashSet<>();

        RestService serviceAnnotation = method.getAnnotation(RestService.class);
        if (serviceAnnotation != null) {
            list.add(standardizeServiceName(serviceAnnotation.name()));
        }

        RestServices restServices = method.getAnnotation(RestServices.class);
        if (restServices != null) {
            for (RestService service : restServices.value()) {
                list.add(standardizeServiceName(service.name()));
            }
        }

        return list;
    }

//    public static String extractServiceName(Class serviceAnnotation, Method method) {
//        Function<Method, String> serviceNameGetter = SERVICE_NAME_GETTER.get(serviceAnnotation);
//        if (serviceNameGetter != null) {
//            return serviceNameGetter.apply(method);
//        } else {
//            throw new IllegalArgumentException("Not supported annotation. " + serviceAnnotation);
//        }
//    }

    public static String standardizeServiceName(String serviceName) {
        if (serviceName.isEmpty()) {
            return "";
        }

        String checkName = serviceName;
        if (checkName.startsWith(Constant.SERVICE_SEPARATE)) {
            checkName = checkName.substring(1);
        }

        if (checkName.endsWith(Constant.SERVICE_SEPARATE)) {
            checkName = checkName.substring(0, checkName.length() - 1);
        }

        return checkName;
    }
}
