package team.balam.exof.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoadedServiceDto {
    private String directoryClass;
    private String serviceName;
    private String serviceGroupId;
    private String method;
}
