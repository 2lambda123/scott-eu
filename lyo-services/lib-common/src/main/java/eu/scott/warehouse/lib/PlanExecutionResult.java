package eu.scott.warehouse.lib;

import java.time.Duration;

/**
 * Created on 2018-03-06
 *
 * @author Andrew Berezovskyi (andriib@kth.se)
 * @version $version-stub$
 * @since 0.0.1
 */
public class PlanExecutionResult {
    private final boolean isSuccessful;
    private final Duration planExecutionTime;

    public PlanExecutionResult(final boolean isSuccessful, final Duration planExecutionTime) {
        this.isSuccessful = isSuccessful;
        this.planExecutionTime = planExecutionTime;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public Duration getPlanExecutionTime() {
        return planExecutionTime;
    }
}
