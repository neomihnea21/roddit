package obj;
import java.time.LocalDateTime;
import java.io.*;
public class Report {
    private User reporter;
    protected Post postReported;
    String reason;
    LocalDateTime timestamp;

    Report(User reporter, Post postReported, String reason) {
        this.reporter = reporter;
        this.postReported = postReported;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
        logReport(); // Automatically logs when created
    }
    private void logReport() {
        try (FileWriter fw = new FileWriter("reports.log", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
             
            out.println("[" + timestamp + "] "
                      + "Raporteaza: " + reporter.getName() + ", "
                      + "Post ID: " + postReported.getBrief(60) + ", "
                      + "Motiv: " + reason);
        } catch (IOException e) {
            System.err.println("Nu se poate scrie log: " + e.getMessage());
        }
    }
}