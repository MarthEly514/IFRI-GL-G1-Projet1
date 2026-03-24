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
    
    public static SystemStats getAdminStats() throws ApiException {
        return ApiClient.get("/stats/admin", SystemStats.class);
    }
    
    public static SystemStats getAgentStats() throws ApiException {
        return ApiClient.get("/stats/agent", SystemStats.class);
    }
 
    public static SystemLog[] getLogs(int page, int size) throws ApiException {
        return ApiClient.get("/logs?page=" + page + "&size=" + size, SystemLog[].class);
    }
 
    public static class SystemStats {
        public int    statTotalUsers;
        public int    statStudents;
        public int    statStudentsActive;
        public int    statAgents;
        public int    statAgentsActive;
        public int    statAdmins;
        public int    statInactiveUsers;
        public int    statTotalDemands;
        public int    statPendingDemands;
        public int    statApprovedDemands;
        public int    statRejectedDemands;
        public String statApprovalRate;    
        public int    statTotalActes;
        public int    statActesThisMonth;
        public int    statDownloads;

        // Nested objects from server
        public NewUser[]       newUsers;
        public RecentActivity[] recentActivity;
    }

    public static class NewUser {
        public String role;
        public boolean actif;
        public String nom;
    }

    public static class RecentActivity {
        public String title;
        public String sub;
    }
}