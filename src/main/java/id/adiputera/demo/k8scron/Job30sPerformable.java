package id.adiputera.demo.k8scron;

import org.springframework.stereotype.Component;

/**
 * @author Yusuf F. Adiputera
 */
@Component
@RegistryKey(key = "job-30s")
public class Job30sPerformable implements JobPerformable {

    @Override
    public void perform(String... args) throws Exception {
        System.out.println("[JOB-30S] Executing business logic for 30s delay job...");
    }
}
