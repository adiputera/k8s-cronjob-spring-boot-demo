package id.adiputera.demo.k8scron;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author Yusuf F. Adiputera
 */
@Component
public class TaskRunner implements CommandLineRunner {

    private final JobRegistry jobRegistry;

    public TaskRunner(JobRegistry jobRegistry) {
        this.jobRegistry = jobRegistry;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("Error: No job name provided as argument!");
        }

        String jobName = args[0];
        System.out.println("Starting execution for: " + jobName);

        try {
            JobPerformable job = jobRegistry.getJob(jobName)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown job '" + jobName + "'!"));

            // Extract the rest of the arguments to pass to the job
            String[] jobArgs = new String[args.length - 1];
            System.arraycopy(args, 1, jobArgs, 0, jobArgs.length);

            job.perform(jobArgs);

            System.out.println("Job " + jobName + " completed successfully!");
            // Application will terminate naturally with exit code 0

        } catch (Exception e) {
            System.err.println("Job " + jobName + " failed: " + e.getMessage());
            throw e; // Spring Boot will translate this to a non-zero exit code
        }
    }
}

