package it.polimi.codekatabattle.utils.clock;

import org.springframework.context.annotation.Primary;

import java.time.Clock;

@Primary
public class RealClock implements ConfigurableClock {
    private Clock clock;

    public RealClock() {
        this.clock = Clock.systemDefaultZone();
    }

    @Override
    public Clock getClock() {
        return clock;
    }

    @Override
    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void resetClock() {
        this.clock = Clock.systemDefaultZone();
    }
}
