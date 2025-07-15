package com.interview.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class QuestionAsyncTaskService {
    private final Map<String, TaskStatus> taskMap = new ConcurrentHashMap<>();

    public String submitTask() {
        String taskId = UUID.randomUUID().toString();
        taskMap.put(taskId, new TaskStatus("pending", null));
        return taskId;
    }

    @Async
    public void runTask(String taskId, Callable<List<Map<String, Object>>> task) {
        try {
            List<Map<String, Object>> result = task.call();
            taskMap.put(taskId, new TaskStatus("done", result));
        } catch (Exception e) {
            taskMap.put(taskId, new TaskStatus("error", null));
        }
    }

    public TaskStatus getTaskStatus(String taskId) {
        return taskMap.getOrDefault(taskId, new TaskStatus("not_found", null));
    }

    public static class TaskStatus {
        public String status; // pending, done, error, not_found
        public List<Map<String, Object>> questions;

        public TaskStatus(String status, List<Map<String, Object>> questions) {
            this.status = status;
            this.questions = questions;
        }
    }
} 