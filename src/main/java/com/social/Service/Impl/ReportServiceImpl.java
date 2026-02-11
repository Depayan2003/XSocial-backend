package com.social.Service.Impl;

import com.social.Model.Report;
import com.social.Model.User;
import com.social.Repository.ReportRepository;
import com.social.Service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    public void reportUser(User reporter, User reported, String reason) {
        Report r = new Report();
        r.setReporter(reporter);
        r.setReportedUser(reported);
        r.setReason(reason);
        r.setCreatedAt(LocalDateTime.now());

        reportRepository.save(r);
    }

    @Override
    public List<Report> getPendingReports() {
        return reportRepository.findByResolvedFalse();
    }
    
    @Override
    public void resolveReport(Long reportId) {
        Report r = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));

        r.setResolved(true);
        reportRepository.save(r);
    }

}
