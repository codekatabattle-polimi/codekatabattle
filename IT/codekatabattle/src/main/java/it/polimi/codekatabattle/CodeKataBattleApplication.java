package it.polimi.codekatabattle;

import it.polimi.codekatabattle.utils.clock.ConfigurableClock;
import it.polimi.codekatabattle.utils.clock.RealClock;
import it.polimi.codekatabattle.utils.clock.TestClock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@SpringBootApplication
public class CodeKataBattleApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeKataBattleApplication.class, args);
	}

	@Bean
	@ConditionalOnProperty(name = "ckb.test", havingValue = "false", matchIfMissing = true)
	public ConfigurableClock defaultClock() {
		return new RealClock();
	}

	@Bean
	@ConditionalOnProperty(name = "ckb.test", havingValue = "true")
	public ConfigurableClock testClock() {
		Clock fixedClock = Clock.fixed(LocalDate.of(2024, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
		ConfigurableClock testClock = new TestClock();
		testClock.setClock(fixedClock);
		return testClock;
	}

}
