package it.polimi.codekatabattle.utils.clock;

import org.springframework.context.annotation.Primary;

import java.time.Clock;

public class TestClock implements ConfigurableClock {
    private Clock initialClock;

    private Clock clock;

    public TestClock() {
        this.clock = Clock.systemDefaultZone();
    }

    @Override
    public Clock getClock() {
        return clock;
    }

    @Override
    public void setClock(Clock clock) {
        this.clock = clock;
        this.initialClock = clock;
    }

    @Override
    public void resetClock() {
        this.clock = this.initialClock;
    }
}
