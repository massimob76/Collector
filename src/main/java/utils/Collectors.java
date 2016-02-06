package utils;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class Collectors {

    public interface Averageable {
        void add(Averageable averageable);
        void divide(int divisor);
    }

    private static class AverageableAcc {
        private Averageable soFar;
        private int counter;
    }

    public static <T extends Averageable> Collector<T, AverageableAcc, Optional<T>> toAverage() {
        return new Collector<T, AverageableAcc, Optional<T>>() {

            @Override
            public Supplier<AverageableAcc> supplier() {
                return AverageableAcc::new;
            }

            @Override
            public BiConsumer<AverageableAcc, T> accumulator() {
                return (acc, averageable) -> {
                    if (acc.soFar == null) {
                        acc.soFar = averageable;
                    } else {
                        acc.soFar.add(averageable);
                    }
                    acc.counter++;
                };
            }

            @Override
            public BinaryOperator<AverageableAcc> combiner() {
                return (acc1, acc2) -> {
                    acc1.soFar.add(acc2.soFar);
                    acc1.counter+=acc2.counter;
                    return acc1;
                };
            }

            @Override
            public Function<AverageableAcc, Optional<T>> finisher() {
                return acc -> {
                    if (acc.soFar == null) {
                        return Optional.empty();
                    } else {
                        acc.soFar.divide(acc.counter);
                        return Optional.of((T) acc.soFar);
                    }

                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.CONCURRENT);
            }
        };
    }

}
