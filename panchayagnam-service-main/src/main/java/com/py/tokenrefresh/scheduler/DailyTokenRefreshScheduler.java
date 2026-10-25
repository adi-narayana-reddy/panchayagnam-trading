package com.py.tokenrefresh.scheduler;

import com.py.common.enums.BrokerType;
import com.py.mail.service.TokenRefreshMailService;
import com.py.tokenrefresh.entity.PyTokenRefreshScheduleEntity;
import com.py.tokenrefresh.entity.TokenRefreshProgressEntity;
import com.py.tokenrefresh.repository.PyTokenRefreshScheduleRepository;
import com.py.utils.NextTriggerTimeUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyTokenRefreshScheduler {

    private static final Logger log = LoggerFactory.getLogger(DailyTokenRefreshScheduler.class);

    @Autowired
    private TokenRefreshSchedulerService tokenRefreshSchedulerService;

    @Autowired
    private PyTokenRefreshScheduleRepository scheduleRepository;

    @Autowired
    private TokenRefreshMailService mailService;

    // ── On server startup: catch up any missed triggers ───────────────────────
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("===== Token Refresh Startup Check =====");
        List<PyTokenRefreshScheduleEntity> allSchedules = scheduleRepository.findAll();

        for (PyTokenRefreshScheduleEntity schedule : allSchedules) {
            if (LocalDateTime.now().isAfter(schedule.getNextTriggerTime())) {
                log.info("Missed trigger detected on startup | brokerType={} scheduledAt={}",
                        schedule.getBrokerType(), schedule.getNextTriggerTime());
                executeAndUpdate(schedule);
            } else {
                log.info("Next trigger upcoming | brokerType={} at={}",
                        schedule.getBrokerType(), schedule.getNextTriggerTime());
            }
        }
    }

    // ── Every 1 min: check if any broker trigger time has arrived ─────────────
    @Scheduled(fixedRate = 300000)
    public void pollAndRefresh() {
        List<PyTokenRefreshScheduleEntity> allSchedules = scheduleRepository.findAll();

        for (PyTokenRefreshScheduleEntity schedule : allSchedules) {
            if (LocalDateTime.now().isAfter(schedule.getNextTriggerTime())) {
                log.info("Trigger time reached | brokerType={}", schedule.getBrokerType());
                executeAndUpdate(schedule);
            }
        }
    }

    // ── Execute refresh + update next trigger time in DB ─────────────────────
    private void executeAndUpdate(PyTokenRefreshScheduleEntity schedule) {
        BrokerType brokerType = schedule.getBrokerType();
        List<TokenRefreshProgressEntity> allProgress = new ArrayList<>();

        try {
            allProgress = tokenRefreshSchedulerService.refreshByBrokerType(brokerType);

            schedule.setLastExecutedAt(LocalDateTime.now());
            schedule.setLastStatus("SUCCESS");
            schedule.setLastRemarks("Token refresh completed successfully");

        } catch (Exception e) {
            log.error("Token refresh execution failed | brokerType={} error={}", brokerType, e.getMessage());
            schedule.setLastExecutedAt(LocalDateTime.now());
            schedule.setLastStatus("FAILED");
            schedule.setLastRemarks(e.getMessage());
        }

        // ── Send summary mail ─────────────────────────────────────────────────
        if (!allProgress.isEmpty()) {
            mailService.sendRefreshSummary(allProgress, schedule.getNotifyEmailList());
        }

        // ── Update next trigger time ──────────────────────────────────────────
        LocalDateTime nextTrigger = NextTriggerTimeUtil.computeNextTrigger(brokerType);
        schedule.setNextTriggerTime(nextTrigger);
        scheduleRepository.save(schedule);

        log.info("Next trigger updated | brokerType={} nextTriggerTime={}", brokerType, nextTrigger);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void testMailOnStartup() {
        log.info("===== Test Mail Started =====");

        // ── Static dummy progress list ────────────────────────────────────
        List<TokenRefreshProgressEntity> testList = new ArrayList<>();

        TokenRefreshProgressEntity p1 = new TokenRefreshProgressEntity();
        p1.setBrokerAccountId(1L);
        p1.setClientId("ROJS1323");
        p1.setBrokerType(BrokerType.MOTILAL);
        p1.setStatus("SUCCESS");
        p1.setRemarks(null);
        p1.setRefreshDate(LocalDateTime.now());
        testList.add(p1);

        TokenRefreshProgressEntity p2 = new TokenRefreshProgressEntity();
        p2.setBrokerAccountId(2L);
        p2.setClientId("ROJS1361");
        p2.setBrokerType(BrokerType.MOTILAL);
        p2.setStatus("FAILED");
        p2.setRemarks("Motilal login failed: Invalid TOTP");
        p2.setRefreshDate(LocalDateTime.now());
        testList.add(p2);

        TokenRefreshProgressEntity p3 = new TokenRefreshProgressEntity();
        p3.setBrokerAccountId(3L);
        p3.setClientId("A1234567");
        p3.setBrokerType(BrokerType.ANGELONE);
        p3.setStatus("SUCCESS");
        p3.setRemarks(null);
        p3.setRefreshDate(LocalDateTime.now());
        testList.add(p3);

        // ── Static mail IDs ───────────────────────────────────────────────
        List<String> toMailIds = List.of("garikapatigopi00@gmail.com","chinna3028@gmail.com","pavankumar10036@gmail.com");

        // ── Send test mail ────────────────────────────────────────────────
        mailService.sendRefreshSummary(testList, toMailIds);

        log.info("===== Test Mail Completed =====");
    }
}
