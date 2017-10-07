package cz.eoa.configuration;

import cz.eoa.templates.StatisticsCreationStrategy;
import cz.eoa.templates.StatisticsPerEpoch;
import cz.eoa.templates.operations.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Optional;

@Getter
public class EvolutionConfiguration<V, T, K extends Comparable<K>, L extends StatisticsPerEpoch<V, T, K>> {
    private final Optional<FitnessTweakingStrategy<V, T, K>> fitnessTweakingStrategy;
    private final FitnessAssessmentStrategy<T, K> fitnessAssessment;
    private final PopulationInitializationStrategy<V, T> populationInitialization;
    private final Optional<CrossoverStrategy<V, T>> crossover;
    private final Optional<MutationStrategy<V, T>> mutation;
    private final ReplacementStrategy<V, T, K> replacement;
    private final SelectorStrategy<V, T, K> selector;
    private final boolean isParallel;
    private final boolean isFitnessIsMaximized;
    private final StatisticsCreationStrategy<V, T, K, L> statisticsCreation;
    private final int populationSize;
    private final double probabilityOfCrossover;
    private final TerminationCondition<V, T, K, L> terminationCondition;
    private final DecodingStrategy<V, T> decoding;

    EvolutionConfiguration(Optional<FitnessTweakingStrategy<V, T, K>> fitnessTweakingStrategy,
                                   FitnessAssessmentStrategy<T, K> fitnessAssessment,
                                   PopulationInitializationStrategy<V, T> populationInitialization, Optional<CrossoverStrategy<V, T>> crossover,
                                   Optional<MutationStrategy<V, T>> mutation, ReplacementStrategy<V, T, K> replacement,
                                   SelectorStrategy<V, T, K> selector, boolean isParallel, boolean isFitnessIsMaximized,
                                   StatisticsCreationStrategy<V, T, K, L> statisticsCreation, int populationSize,
                                   double probabilityOfCrossover,
                                   TerminationCondition<V, T, K, L> terminationCondition, DecodingStrategy<V, T> decoding) {
        this.fitnessTweakingStrategy = fitnessTweakingStrategy;
        this.fitnessAssessment = fitnessAssessment;
        this.populationInitialization = populationInitialization;
        this.crossover = crossover;
        this.mutation = mutation;
        this.replacement = replacement;
        this.selector = selector;
        this.isParallel = isParallel;
        this.isFitnessIsMaximized = isFitnessIsMaximized;
        this.statisticsCreation = statisticsCreation;
        this.populationSize = populationSize;
        this.probabilityOfCrossover = probabilityOfCrossover;
        this.terminationCondition = terminationCondition;
        this.decoding = decoding;
    }

}
