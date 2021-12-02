package org.itsallcode.whiterabbit.plugin.pmsmart.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

public class TenaciousCheckerTest
{
    @Test
    void immediateSuccess()
    {
        final CountingCondition condition = new CountingCondition(0);
        final TenaciousChecker testee = new TenaciousChecker(condition);
        final long start = System.currentTimeMillis();

        assertThat(testee.check(Duration.ofSeconds(20))).isTrue();
        assertThat(condition.iterations).isEqualTo(1);
        assertThat(System.currentTimeMillis() - start).isLessThan(200);
    }

    @Test
    void failure()
    {
        final CountingCondition condition = new CountingCondition(TenaciousChecker.NUMBER_OF_CHECKS);
        final TenaciousChecker testee = new TenaciousChecker(condition);
        final long start = System.currentTimeMillis();

        assertThat(testee.check(Duration.ofMillis(300))).isFalse();
        assertThat(condition.iterations).isEqualTo(TenaciousChecker.NUMBER_OF_CHECKS);
        assertThat(System.currentTimeMillis() - start).isGreaterThan(299);
    }

    @Test
    void delayedSuccess()
    {
        final CountingCondition condition = new CountingCondition(3);
        final TenaciousChecker testee = new TenaciousChecker(condition);
        final long start = System.currentTimeMillis();

        assertThat(testee.check(Duration.ofMillis(300))).isTrue();
        assertThat(condition.iterations).isEqualTo(4);
        assertThat(System.currentTimeMillis() - start).isGreaterThan(29);
    }

    private static class CountingCondition implements Supplier<Boolean>
    {
        private final int failures;
        int iterations = 0;

        CountingCondition(int failures)
        {
            this.failures = failures;
        }

        @Override
        public Boolean get()
        {
            return ++iterations > failures;
        }
    }
}
