package com.social.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.social.Model.Report;
import com.social.Model.User;
import com.social.Repository.UserRepository;
import com.social.Service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth =
            SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 👤 USER → REPORT USER
    @PostMapping("/user/{reportedUserId}")
    public void reportUser(
            @PathVariable Long reportedUserId,
            @RequestBody Map<String, String> body
    ) {
        User reporter = getCurrentUser();

        User reported = userRepository.findById(reportedUserId)
            .orElseThrow(() -> new RuntimeException("Reported user not found"));

        reportService.reportUser(
            reporter,
            reported,
            body.get("reason")
        );
    }

    // 🛡️ ADMIN → VIEW PENDING REPORTS
    @GetMapping("/pending")
    public List<Report> getPendingReports() {
        return reportService.getPendingReports();
    }
    
    /* ================= ADMIN: RESOLVE ================= */

    @PostMapping("/{reportId}/resolve")
    public void resolveReport(@PathVariable Long reportId) {
        reportService.resolveReport(reportId);
    }
}
