package cz.eoa.templates;

import cz.eoa.templates.operations.IndividualFitnessUpdater;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor
@EqualsAndHashCode(of = "individual")
@Getter
public class IndividualWithAssignedFitness<V, T, K extends Comparable<K>> implements Comparable<IndividualWithAssignedFitness<V, T, K>> {
    private final Individual<V, T> individual;
    private final K fitness;

    public IndividualWithTweakedFitness<V, T, K> updateFitness(int individualsIndex, List<IndividualWithAssignedFitness<V, T, K>> population, IndividualFitnessUpdater<V, T, K> fitnessUpdater) {
        return new IndividualWithTweakedFitness<>(this, fitnessUpdater.computeNewFitness(this, IntStream.range(0, population.size())
                .filter(value -> value != individualsIndex)
                .boxed()
                .map(population::get)
                .collect(ImmutableListCollector.toImmutableList())));
    }

    public V getGenes() {
        return individual.getGenes();
    }

    @Override
    public int compareTo(@NotNull IndividualWithAssignedFitness<V, T, K> other) {
        return this.getFitness().compareTo(other.getFitness());
    }

    private static class ImmutableListCollector {
        static <t> Collector<t, List<t>, List<t>> toImmutableList() {
            return Collector.of(ArrayList::new, List::add, (left, right) -> {
                left.addAll(right);
                return left;
            }, Collections::unmodifiableList);
        }
    }
}
