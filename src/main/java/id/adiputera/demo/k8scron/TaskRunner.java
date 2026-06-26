package id.adiputera.demo.k8scron;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TaskRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) {
            System.err.println("Error: No job name provided as argument!");
            System.exit(1);
        }

        String jobName = args[0];
        System.out.println("Starting execution for: " + jobName);

        try {
            switch (jobName) {
                case "job-10s":
                    System.out.println("[JOB-10S] Executing business logic for 10s delay job...");
                    break;
                case "job-30s":
                    System.out.println("[JOB-30S] Executing business logic for 30s delay job...");
                    break;
                case "job-50s":
                    System.out.println("[JOB-50S] Executing business logic for 50s delay job...");
                    break;
                default:
                    System.err.println("Error: Unknown job '" + jobName + "'!");
                    System.exit(1);
            }

            System.out.println("Job " + jobName + " completed successfully!");
            System.exit(0);

        } catch (Exception e) {
            System.err.println("Job " + jobName + " failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
