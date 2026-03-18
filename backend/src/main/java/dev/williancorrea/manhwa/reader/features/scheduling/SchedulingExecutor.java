package dev.williancorrea.manhwa.reader.features.scheduling;

import java.time.OffsetDateTime;
import java.util.List;
import dev.williancorrea.manhwa.reader.features.chapter.notify.ChapterNotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulingExecutor {

    private final SchedulingService schedulingService;
    private final ChapterNotifyService chapterNotifyService;

    /**
     * Executa a cada 1 minutos para verificar agendamentos pendentes
     */
    @Scheduled(fixedDelay = 60000) // 1 minuto
    public void executeScheduledTasks() {
        log.debug("[SchedulingExecutor][executeScheduledTasks] Checking for scheduled tasks");

        List<Scheduling> activeSchedules = schedulingService.findAllActive();

        OffsetDateTime now = OffsetDateTime.now();

        for (Scheduling schedule : activeSchedules) {
            if (shouldExecute(schedule, now)) {
                executeTask(schedule);
            }
        }
    }

    private boolean shouldExecute(Scheduling schedule, OffsetDateTime now) {
        if (schedule.getNextExecution() == null) {
            return true;
        }
        return now.isAfter(schedule.getNextExecution()) || now.isEqual(schedule.getNextExecution());
    }

    private void executeTask(Scheduling schedule) {
        log.debug("[SchedulingExecutor][executeTask] Executing scheduled task: {}", schedule.getName());

        try {
            switch (schedule.getName()) {
                case CHAPTER_NOTIFICATION:
                    chapterNotifyService.processAndSendNotifications();
                    break;
                case MEDIOCRESCAN_SCRAPER:
                    log.debug("[SchedulingExecutor][executeTask] MEDIOCRESCAN_SCRAPER execution would run here");
                    // TODO: Implementar execucao do scraper
                    break;
                default:
                    log.warn("[SchedulingExecutor][executeTask] Unknown scheduled task: {}", schedule.getName());
            }

            schedulingService.updateLastExecution(schedule.getId());
            log.info("[SchedulingExecutor][executeTask] Successfully executed task: {}", schedule.getName());

        } catch (Exception e) {
            log.error("[SchedulingExecutor][executeTask] Error executing task: {}", schedule.getName(), e);
        }
    }
}
