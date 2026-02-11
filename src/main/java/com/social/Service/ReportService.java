package com.social.Service;

import com.social.Model.Report;
import com.social.Model.User;

import java.util.List;

public interface ReportService {

    void reportUser(User reporter, User reported, String reason);

    List<Report> getPendingReports();
    
    void resolveReport(Long reportId);
}
