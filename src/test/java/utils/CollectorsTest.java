package utils;

import org.junit.Test;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static utils.Collectors.Averageable;
import static utils.Collectors.toAverage;

public class CollectorsTest {

    private class DoubleAverageable implements Averageable {

        private double value;

        public DoubleAverageable(double value) {
            this.value = value;
        }

        @Override
        public void add(Averageable averageable) {
            this.value+=((DoubleAverageable) averageable).value;
        }

        @Override
        public void divide(int divisor) {
            this.value/=divisor;
        }

        public double getValue() {
            return value;
        }
    }

    @Test
    public void testAvgCollector() throws Exception {
        Optional<DoubleAverageable> avg = IntStream.of(1, 2, 3, 4, 5)
            .mapToObj(DoubleAverageable::new)
            .collect(toAverage());

        assertThat(avg.get().getValue(), is(3.0));
    }

    @Test
    public void testAvgCollector_whenOnlyOneElement() throws Exception {
        Optional<DoubleAverageable> avg = IntStream.of(5)
            .mapToObj(DoubleAverageable::new)
            .collect(toAverage());

        assertThat(avg.get().getValue(), is(5.0));
    }

    @Test
    public void testAvgCollector_whenNoElement() throws Exception {
        Optional<DoubleAverageable> avg = IntStream.empty()
            .mapToObj(DoubleAverageable::new)
            .collect(toAverage());

        assertThat(avg.isPresent(), is(false));
    }

    @Test
    public void testAvgCollector_manyItems() throws Exception {
        int upperLimit = 10000;
        Optional<DoubleAverageable> avg = IntStream.range(1, upperLimit)
            .mapToObj(DoubleAverageable::new)
            .collect(toAverage());

        assertThat(avg.get().getValue(), is((double)upperLimit / 2));

    }

    @Test
    public void testAvgCollector_manyItems_inParallel() throws Exception {
        int upperLimit = 10000;
        Optional<DoubleAverageable> avg = IntStream.range(1, upperLimit)
            .parallel()
            .mapToObj(DoubleAverageable::new)
            .collect(toAverage());

        assertThat(avg.get().getValue(), is((double)upperLimit / 2));

    }


}
