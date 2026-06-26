Tidak perlu membuat 1 service/project terpisah untuk setiap 1 cronjob. Kalau kamu punya 5 atau 10 cronjob, membuat 10 project berbeda akan menjadi mimpi buruk saat melakukan maintenance kode dan CI/CD pipeline.
Strategi terbaiknya adalah: Buat 1 Project Spring Boot khusus (kita sebut saja "Cron Runner App") yang berisi semua logika cronjob kamu.
Lalu bagaimana cara membedakan job mana yang mau dijalankan di Kubernetes? Kamu bisa menggunakan fitur Spring Boot Arguments atau Spring Profiles.
Berikut adalah cara setup agar 1 service bisa menangani multiple cronjobs:
Strategi: Menggunakan Arguments (Sangat Direkomendasikan)
Kamu bisa mempassing argument dari manifest Kubernetes CronJob untuk menentukan class atau method mana yang harus dieksekusi.
1. Di Sisi Spring Boot (Pemberi Komando)
Ubah CommandLineRunner kamu menjadi sebuah selector (pemilih) berdasarkan argument yang dikirim saat aplikasi dinyalakan.



Java
package com.example.cronjob;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class CronRunnerApplication implements CommandLineRunner {

    @Autowired
    private SyncDataTask syncDataTask;

    @Autowired
    private SendEmailTask sendEmailTask;

    public static void main(String[] args) {
        SpringApplication.run(CronRunnerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) {
            System.out.println("Error: Tolong masukkan nama job sebagai argument!");
            System.exit(1);
        }

        String jobName = args[0];
        System.out.println("Mulai menjalankan Job: " + jobName);

        try {
            switch (jobName) {
                case "sync-data":
                    syncDataTask.execute();
                    break;
                case "send-email":
                    sendEmailTask.execute();
                    break;
                default:
                    System.out.println("Error: Job '" + jobName + "' tidak ditemukan!");
                    System.exit(1);
            }
            
            System.out.println("Job " + jobName + " sukses!");
            System.exit(0); // Sukses
            
        } catch (Exception e) {
            System.err.println("Job " + jobName + " gagal: " + e.getMessage());
            System.exit(1); // Gagal (Kubernetes akan tahu ini error karena exit code bukan 0)
        }
    }
}


2. Di Sisi Kubernetes (Membuat Banyak CronJob dari 1 Image)
Di Kubernetes, kamu tinggal membuat beberapa file manifest CronJob (atau digabung dalam satu file dipisah ---). Semuanya menggunakan Docker Image yang sama, tapi beda di bagian args-nya.
CronJob 1: Untuk Sinkronisasi Data (cronjob-sync.yaml)



YAML
apiVersion: batch/v1
kind: CronJob
metadata:
  name: cron-sync-data
spec:
  schedule: "0 1 * * *" # Tiap jam 1 pagi
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: cron-runner
            image: 123456789012.dkr.ecr.ap-southeast-1.amazonaws.com/cron-runner-app:latest
            args: ["sync-data"] # <--- Menentukan job yang jalan
          restartPolicy: OnFailure


CronJob 2: Untuk Kirim Email Harian (cronjob-email.yaml)



YAML
apiVersion: batch/v1
kind: CronJob
metadata:
  name: cron-send-email
spec:
  schedule: "0 8 * * *" # Tiap jam 8 pagi
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: cron-runner
            image: 123456789012.dkr.ecr.ap-southeast-1.amazonaws.com/cron-runner-app:latest
            args: ["send-email"] # <--- Menentukan job yang jalan
          restartPolicy: OnFailure


Keuntungan Pola 1 Image untuk Banyak Job:
Satu Pipeline CI/CD: Kamu cukup membuat 1 pipeline build Docker image. Setiap ada perubahan kode di job manapun, kamu cukup push 1 image terbaru ke ECR.
Resource Efisien: Kode program berkumpul di satu tempat, memudahkan code sharing (misal beberapa job sama-sama butuh akses ke UserRepository atau utility class yang sama).
Manajemen Terpusat: Di Kubernetes, mereka tetap terlihat sebagai resource yang terpisah (cron-sync-data dan cron-send-email), punya metrik masing-masing, dan bisa di-scale atau diubah jadwalnya secara independen.
