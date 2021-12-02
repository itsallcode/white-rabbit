package org.itsallcode.whiterabbit.plugin.pmsmart.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.function.BooleanSupplier;

import org.junit.jupiter.api.Test;

class TenaciousCheckTest
{

    @Test
    void immediateSuccess()
    {
        final Testee testee = new Testee(0);
        assertThat(testee.check(Duration.ofSeconds(20))).isTrue();
        assertThat(testee.getIterations()).isEqualTo(1);
        assertThat(testee.getDuration().toMillis()).isLessThan(200);
    }

    @Test
    void failure()
    {
        final Testee testee = new Testee(Driver.NUMBER_OF_TENACIOUS_CHECKS);
        assertThat(testee.check(Duration.ofMillis(300))).isFalse();
        assertThat(testee.getIterations()).isEqualTo(Driver.NUMBER_OF_TENACIOUS_CHECKS);
        assertThat(testee.getDuration().toMillis()).isGreaterThan(299);
    }

    @Test
    void delayedSuccess()
    {
        final Testee testee = new Testee(3);
        assertThat(testee.check(Duration.ofMillis(300))).isTrue();
        assertThat(testee.getIterations()).isEqualTo(4);
        assertThat(testee.getDuration().toMillis()).isGreaterThan(29);
    }

    private static class Testee
    {
        private final int failures;
        private int iterations = 0;
        private final Driver driver;
        // private final TenaciousChecker checker;
        private Duration duration = Duration.ZERO;

        public Testee(int failures)
        {
            this.failures = failures;
            this.driver = new Driver(null, null);
            // checker = new TenaciousChecker(condition());
        }

        public Duration getDuration()
        {
            return duration;
        }

        public int getIterations()
        {
            return iterations;
        }

        private BooleanSupplier condition()
        {
            return new BooleanSupplier()
            {
                @Override
                public boolean getAsBoolean()
                {
                    return ++iterations > failures;
                }
            };
        }

        public boolean check(Duration timeout)
        {
            final long start = System.currentTimeMillis();
            // final boolean result = checker.check(timeout);
            final boolean result = driver.tenaciousCheck(timeout, condition());
            this.duration = Duration.ofMillis(System.currentTimeMillis() - start);
            return result;
        }
    }

}
