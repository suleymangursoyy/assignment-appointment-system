package nl.gerimedica.assignment.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HospitalUtils {

    private static int usageCounter = 0;

    public static void recordUsage(String context) {
        usageCounter++;
        log.info("HospitalUtils used. Counter: {} | Context: {}", usageCounter, context);
    }
}
