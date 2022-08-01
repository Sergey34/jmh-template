package seko.recursice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Threads(1)
public class TestBenchmark {

    @Test
    public void foo() throws Exception {
        setup();
        List<String> tables = getTablesRO(f20, new ArrayList<>());
        Assertions.assertThat(tables).hasSize(20);
    }

    @Test
    public void runBenchmarks() throws Exception {
        Options options = new OptionsBuilder()
                .include(this.getClass().getName() + ".*")
                .mode(Mode.AverageTime)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(6)
                .threads(1)
                .measurementIterations(6)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();

        new Runner(options).run();
    }


    static FilterConfig f20;

    @Setup
    public static void setup() {
        FilterConfig f1 = new FilterConfig(null, "1");
        FilterConfig f2 = new FilterConfig(List.of(f1), "2");
        FilterConfig f3 = new FilterConfig(List.of(f2), "3");
        FilterConfig f4 = new FilterConfig(List.of(f3), "4");
        FilterConfig f5 = new FilterConfig(null, "5");
        FilterConfig f6 = new FilterConfig(null, "6");
        FilterConfig f7 = new FilterConfig(List.of(f6, f5, f4), "7");
        FilterConfig f8 = new FilterConfig(List.of(f7), "8");
        FilterConfig f9 = new FilterConfig(List.of(f8), "9");
        FilterConfig f10 = new FilterConfig(List.of(f9), "10");
        FilterConfig f11 = new FilterConfig(List.of(f10), "11");
        FilterConfig f12 = new FilterConfig(List.of(f11), "12");
        FilterConfig f13 = new FilterConfig(null, "13");
        FilterConfig f14 = new FilterConfig(null, "14");
        FilterConfig f15 = new FilterConfig(null, "15");
        FilterConfig f16 = new FilterConfig(List.of(f12, f13, f14, f15), "16");
        FilterConfig f17 = new FilterConfig(List.of(f16), "17");
        FilterConfig f18 = new FilterConfig(List.of(f17), "18");
        FilterConfig f19 = new FilterConfig(List.of(f18), "19");
        f20 = new FilterConfig(List.of(f19), "20");
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void recursive(Blackhole blackhole) throws Exception {
        List<String> tables = getTablesR(f20);
        Assertions.assertThat(tables).hasSize(20);
        blackhole.consume(tables);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void recursiveO(Blackhole blackhole) throws Exception {
        List<String> tables = getTablesRO(f20, new ArrayList<>());
        Assertions.assertThat(tables).hasSize(20);
        blackhole.consume(tables);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void loop(Blackhole blackhole) throws Exception {
        List<String> tables = getTablesL(f20);
        Assertions.assertThat(tables).hasSize(20);
        blackhole.consume(tables);
    }

    private List<String> getTablesRO(FilterConfig f20, List<String> tables) {
        tables.add(f20.getTable());

        if (f20.getFilterConfigs() != null) {
            for (FilterConfig filterConfig : f20.getFilterConfigs()) {
                getTablesRO(filterConfig, tables);
            }
        }
        return tables;
    }

    private List<String> getTablesL(FilterConfig f20) {
        List<String> result = new ArrayList<>();

        Queue<FilterConfig> queue = new ArrayDeque<>();
        queue.offer(f20);
        while (!queue.isEmpty()) {
            var current = queue.poll();
            result.add(current.getTable());
            if (current.getFilterConfigs() != null) {
                queue.addAll(current.getFilterConfigs());
            }
        }
        return result;
    }

    private List<String> getTablesR(FilterConfig f20) {
        List<String> strings = new ArrayList<>();
        if (f20.getFilterConfigs() != null) {
            for (FilterConfig filterConfig : f20.getFilterConfigs()) {
                strings.addAll(getTablesR(filterConfig));
            }
        }
        strings.add(f20.getTable());
        return strings;
    }

}
