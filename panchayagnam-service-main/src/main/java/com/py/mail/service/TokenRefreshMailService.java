package com.py.mail.service;

import com.py.tokenrefresh.entity.TokenRefreshProgressEntity;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class TokenRefreshMailService {

    private static final Logger log = LoggerFactory.getLogger(TokenRefreshMailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    public void sendRefreshSummary(List<TokenRefreshProgressEntity> allList, List<String> toMailIds) {
        if (toMailIds == null || toMailIds.isEmpty()) {
            log.warn("No mail IDs provided — skipping refresh summary mail");
            return;
        }

        long failedCount = allList.stream()
                .filter(p -> "FAILED".equals(p.getStatus()))
                .count();
        long successCount = allList.stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()))
                .count();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(toMailIds.toArray(new String[0]));
            helper.setSubject(buildSubject(failedCount));
            helper.setText(buildHtmlBody(allList, successCount, failedCount), true);
            mailSender.send(message);
            log.info("Token refresh summary mail sent to {} | success={} failed={}",
                    toMailIds, successCount, failedCount);
        } catch (Exception e) {
            log.error("Failed to send token refresh summary mail: {}", e.getMessage());
        }
    }

    private String buildSubject(long failedCount) {
        if (failedCount == 0) {
            return "✅ Token Refresh Successful — " + java.time.LocalDate.now();
        } else {
            return "⚠️ Token Refresh Summary — " + failedCount
                    + " Failed | " + java.time.LocalDate.now();
        }
    }

    private String buildHtmlBody(List<TokenRefreshProgressEntity> allList,
            long successCount, long failedCount) {
        StringBuilder sb = new StringBuilder();

        // ── Header ────────────────────────────────────────────────────────
        sb.append("<h2 style='color:#333;'>Token Refresh Summary — ")
                .append(java.time.LocalDate.now()).append("</h2>");

        // ── Summary Counts ────────────────────────────────────────────────
        sb.append("<table cellpadding='8' cellspacing='0' style='margin-bottom:20px;'>")
                .append("<tr>")
                .append("<td style='background:#4CAF50;color:white;padding:10px 20px;border-radius:4px;'>")
                .append("✅ Success: <b>").append(successCount).append("</b></td>")
                .append("<td style='width:20px;'></td>")
                .append("<td style='background:#f44336;color:white;padding:10px 20px;border-radius:4px;'>")
                .append("❌ Failed: <b>").append(failedCount).append("</b></td>")
                .append("</tr></table>");

        // ── Detail Table ──────────────────────────────────────────────────
        sb.append("<table border='1' cellpadding='8' cellspacing='0' style='border-collapse:collapse;width:100%;'>")
                .append("<thead style='background:#37474f;color:white;'>")
                .append("<tr>")
                .append("<th>#</th>")
                .append("<th>Client ID</th>")
                .append("<th>Broker</th>")
                .append("<th>Status</th>")
                .append("<th>Remarks</th>")
                .append("</tr></thead><tbody>");

        int i = 1;
        for (TokenRefreshProgressEntity p : allList) {
            boolean failed = "FAILED".equals(p.getStatus());
            String rowColor = failed ? "#fff3f3" : "#f9fff9";
            String statusCell = failed
                    ? "<td style='color:red;font-weight:bold;'>❌ FAILED</td>"
                    : "<td style='color:green;font-weight:bold;'>✅ SUCCESS</td>";

            sb.append("<tr style='background:").append(rowColor).append(";'>")
                    .append("<td>").append(i++).append("</td>")
                    .append("<td>").append(p.getClientId()).append("</td>")
                    .append("<td>").append(p.getBrokerType()).append("</td>")
                    .append(statusCell)
                    .append("<td>").append(failed ? p.getRemarks() : "-").append("</td>")
                    .append("</tr>");
        }

        sb.append("</tbody></table>");

        if (failedCount > 0) {
            sb.append("<p style='color:red;margin-top:16px;'>")
                    .append("⚠️ Please check and fix the failed account credentials immediately.")
                    .append("</p>");
        } else {
            sb.append("<p style='color:green;margin-top:16px;'>")
                    .append("All accounts refreshed successfully!")
                    .append("</p>");
        }

        return sb.toString();
    }
}
