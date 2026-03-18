package dev.williancorrea.manhwa.reader.features.scheduling;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class SchedulingService {

    private final SchedulingRepository repository;

    public SchedulingService(@Lazy SchedulingRepository repository) {
        this.repository = repository;
    }

    public List<Scheduling> findAll() {
        return repository.findAll();
    }

    public List<Scheduling> findAllActive() {
        return repository.findByActiveTrue();
    }

    public Optional<Scheduling> findById(UUID id) {
        return repository.findById(id);
    }

    public Optional<Scheduling> findByName(String name) {
        return repository.findByName(name);
    }

    public Scheduling save(Scheduling entity) {
        if (entity.getId() == null) {
            entity.setCreatedAt(OffsetDateTime.now());
        }
        entity.setUpdatedAt(OffsetDateTime.now());

        if (entity.getNextExecution() == null) {
            entity.setNextExecution(calculateNextExecution(entity));
        }

        return repository.save(entity);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public OffsetDateTime calculateNextExecution(Scheduling scheduling) {
        OffsetDateTime baseTime = scheduling.getLastExecution() != null
            ? scheduling.getLastExecution()
            : OffsetDateTime.now();

        OffsetDateTime nextExecution = switch (scheduling.getSchedulingIntervalUnit()) {
            case MINUTES -> baseTime.plusMinutes(scheduling.getIntervalValue());
            case HOURS -> baseTime.plusHours(scheduling.getIntervalValue());
            case DAYS -> baseTime.plusDays(scheduling.getIntervalValue());
            case WEEKS -> baseTime.plusWeeks(scheduling.getIntervalValue());
        };

        if (hasWeekDayRestriction(scheduling)) {
            nextExecution = adjustToValidWeekDay(nextExecution, scheduling);
        }

        return nextExecution;
    }

    private boolean hasWeekDayRestriction(Scheduling scheduling) {
        return scheduling.getMonday() || scheduling.getTuesday() ||
               scheduling.getWednesday() || scheduling.getThursday() ||
               scheduling.getFriday() || scheduling.getSaturday() ||
               scheduling.getSunday();
    }

    private OffsetDateTime adjustToValidWeekDay(OffsetDateTime dateTime, Scheduling scheduling) {
        OffsetDateTime adjusted = dateTime;
        int maxAttempts = 7;

        while (maxAttempts > 0 && !isValidWeekDay(adjusted, scheduling)) {
            adjusted = adjusted.plusDays(1);
            maxAttempts--;
        }

        return adjusted;
    }

    private boolean isValidWeekDay(OffsetDateTime dateTime, Scheduling scheduling) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();

        return switch (dayOfWeek) {
            case MONDAY -> scheduling.getMonday();
            case TUESDAY -> scheduling.getTuesday();
            case WEDNESDAY -> scheduling.getWednesday();
            case THURSDAY -> scheduling.getThursday();
            case FRIDAY -> scheduling.getFriday();
            case SATURDAY -> scheduling.getSaturday();
            case SUNDAY -> scheduling.getSunday();
        };
    }

    public void updateLastExecution(UUID schedulingId) {
        findById(schedulingId).ifPresent(scheduling -> {
            scheduling.setLastExecution(OffsetDateTime.now());
            scheduling.setNextExecution(calculateNextExecution(scheduling));
            repository.save(scheduling);
        });
    }
}
