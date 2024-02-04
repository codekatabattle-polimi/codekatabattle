package it.polimi.codekatabattle.utils.clock;

import java.time.Clock;

public interface ConfigurableClock {
    Clock getClock();
    void setClock(Clock clock);
    void resetClock();
}
