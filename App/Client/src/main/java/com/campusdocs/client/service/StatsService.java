/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.service;
 
import com.campusdocs.client.api.ApiClient;
import com.campusdocs.client.api.ApiException;
import com.campusdocs.client.model.SystemLog;
 
public class StatsService {
 
    public static SystemStats getStats() throws ApiException {
        return ApiClient.get("/stats", SystemStats.class);
    }
 
    public static SystemLog[] getLogs(int page, int size) throws ApiException {
        return ApiClient.get("/logs?page=" + page + "&size=" + size, SystemLog[].class);
    }
 
    // Matches your server's /api/stats response shape
    public static class SystemStats {
        public int totalUsers;
        public int totalStudents;
        public int totalAgents;
        public int totalAdmins;
        public int totalDemandes;
        public int pendingDemandes;
        public int approvedDemandes;
        public int rejectedDemandes;
        public int totalActes;
        public int approvalRate;
    }
}