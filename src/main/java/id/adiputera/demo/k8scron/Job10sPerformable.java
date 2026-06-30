package id.adiputera.demo.k8scron;

import org.springframework.stereotype.Component;

/**
 * @author Yusuf F. Adiputera
 */
@Component
@RegistryKey(key = "job-10s")
public class Job10sPerformable implements JobPerformable {

    @Override
    public void perform(String... args) throws Exception {
        System.out.println("[JOB-10S] Executing business logic for 10s delay job...");
    }
}
