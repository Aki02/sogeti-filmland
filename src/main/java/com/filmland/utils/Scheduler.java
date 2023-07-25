package com.filmland.utils;

import com.filmland.models.services.CategoryService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;


public class Scheduler {

    @Autowired
    private CategoryService categoryService;

    @PostConstruct
    public void onStartup() {
        executeTask();
    }

    @Scheduled(cron="0 0 0 1 1/1 *")
    // Expire all active subscriptions on 1st day every month @ 00:00
    public void onSchedule() {
        executeTask();
    }

    public void executeTask() {
        categoryService.updateRemainingContent();
    }


}
