package id.adiputera.demo.k8scron;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Yusuf F. Adiputera
 */
@Component
public class JobRegistry {

    private final Map<String, JobPerformable> registry = new HashMap<>();

    public JobRegistry(List<JobPerformable> jobs) {
        for (JobPerformable job : jobs) {
            RegistryKey annotation = AnnotationUtils.findAnnotation(job.getClass(), RegistryKey.class);
            if (annotation != null) {
                String key = annotation.key();
                if (registry.containsKey(key)) {
                    throw new IllegalStateException("Duplicate job registry key found: '" + key + "'");
                }
                registry.put(key, job);
                System.out.println("[JobRegistry] Registered job: '" + key + "' -> " + job.getClass().getName());
            } else {
                System.err.println("[JobRegistry] Warning: Class " + job.getClass().getName() + " implements JobPerformable but is not annotated with @RegistryKey");
            }
        }
    }

    public Optional<JobPerformable> getJob(String key) {
        return Optional.ofNullable(registry.get(key));
    }
}
