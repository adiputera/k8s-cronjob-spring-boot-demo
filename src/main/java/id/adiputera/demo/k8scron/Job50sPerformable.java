package id.adiputera.demo.k8scron;

import org.springframework.stereotype.Component;

/**
 * @author Yusuf F. Adiputera
 */
@Component
@RegistryKey(key = "job-50s")
public class Job50sPerformable implements JobPerformable {

    @Override
    public void perform(String... args) throws Exception {
        System.out.println("[JOB-50S] Executing business logic for 50s delay job...");
    }
}
