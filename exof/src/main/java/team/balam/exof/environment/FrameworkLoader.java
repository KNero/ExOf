package team.balam.exof.environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

/**
 * framework.yaml을 SystemSetting으로 저장.
 *
 * @author kwonsm
 */
@Slf4j
public class FrameworkLoader implements Loader {
    @Override
    @SuppressWarnings("unchecked")
    public void load(String _envPath) throws LoadEnvException {
        FileInputStream frameworkFile = null;

        File file = new File(_envPath + "/framework.yaml");
        if (!file.exists() || file.length() == 0) {
            log.warn("file not found or contents is empty. {}", file.getAbsolutePath());
            return;
        }

        try {
            log.info("Loading framework.yaml. {}", file.getAbsoluteFile());

            frameworkFile = new FileInputStream(file);

            Yaml yamlParser = new Yaml();
            Map<String, ?> root = (Map<String, ?>) yamlParser.load(frameworkFile);
            Map<String, ?> fw = (Map<String, ?>) root.get(EnvKey.Framework.FRAMEWORK);

            if (fw == null || fw.keySet().isEmpty()) {
                log.info("file contents is empty. {}", file.getAbsolutePath());
                return;
            }

            fw.keySet().forEach(_key -> {
                if (EnvKey.Framework.SCHEDULER.equals(_key)) {
                    Properties sp = new Properties();
                    SystemSetting.setFramework(EnvKey.Framework.SCHEDULER, sp);

                    Map<String, Object> scheduler = (Map<String, Object>) fw.get(EnvKey.Framework.SCHEDULER);
                    log.info("scheduler: {}", scheduler);

                    scheduler.keySet().forEach(_quartzKey -> {
                        Object value = scheduler.get(_quartzKey).toString();
                        sp.put(_quartzKey, value);
                    });
                } else {
                    Object values = fw.get(_key);

                    if (EnvKey.Framework.EXTERNAL.equals(_key)) {
                        log.info("external key: {}, value: {}", _key, values);
                        SystemSetting.setExternal(values);
                    } else if (values instanceof Map) {
                        Map<String, ?> mapValues = (Map<String, ?>) values;
                        mapValues.keySet().forEach(_mapKey -> {
                            String key = _key + "." + _mapKey;
                            Object value = mapValues.get(_mapKey);

                            log.info("framework key: {}, value: {}", key, value);
                            SystemSetting.setFramework(key, value);
                        });
                    } else {
                        SystemSetting.setFramework(_key, values);
                    }
                }
            });
        } catch (Exception e) {
            throw new LoadEnvException("framework.yaml", e);
        } finally {
            try {
                if (frameworkFile != null) frameworkFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
