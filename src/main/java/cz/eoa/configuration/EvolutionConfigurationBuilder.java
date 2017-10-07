package cz.eoa.configuration;

import cz.eoa.templates.StatisticsCreationStrategy;
import cz.eoa.templates.StatisticsPerEpoch;
import cz.eoa.templates.operations.*;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
public class EvolutionConfigurationBuilder<V, T, K extends Comparable<K>, L extends StatisticsPerEpoch<V, T, K>> {
    private Optional<FitnessTweakingStrategy<V, T, K>> fitnessTweakingStrategy = Optional.empty();
    private FitnessAssessmentStrategy<T, K> fitnessAssessment = null;
    private PopulationInitializationStrategy<V, T> populationInitialization = null;
    private Optional<CrossoverStrategy<V, T>> crossover = Optional.empty();
    private Optional<MutationStrategy<V, T>> mutation = Optional.empty();
    //replace whole population
    private ReplacementStrategy<V, T, K> replacement = null;
    private SelectorStrategy<V, T, K> selector = null;
    private boolean isParallel = true;
    private boolean isFitnessIsMaximized = true;
    private StatisticsCreationStrategy<V, T, K, L> statisticsCreation = null;
    private int populationSize = 50;
    private double probabilityOfCrossover = 0.75;
    private TerminationCondition<V, T, K, L> terminationCondition = null;
    private DecodingStrategy<V, T> decoding = null;

    public EvolutionConfigurationBuilder<V, T, K, L> fitnessTweakingStrategy(FitnessTweakingStrategy<V, T, K> fitnessTweakingStrategy) {
        this.fitnessTweakingStrategy = Optional.ofNullable(fitnessTweakingStrategy);
        return copy();
    }

    public EvolutionConfigurationBuilder<V, T, K, L> fitnessAssessment(FitnessAssessmentStrategy<T, K> fitnessAssessment) {
        this.fitnessAssessment = fitnessAssessment;
        return copy();
    }

    public EvolutionConfigurationBuilder<V, T, K, L> populationInitialization(PopulationInitializationStrategy<V, T> populationInitialization) {
        this.populationInitialization = populationInitialization;
        return copy();
    }

    public EvolutionConfigurationBuilder<V, T, K, L> crossover(CrossoverStrategy<V, T> crossover) {
        this.crossover = Optional.ofNullable(crossover);
        return copy();
    }

    public EvolutionConfigurationBuilder<V, T, K, L> mutation(MutationStrategy<V, T> mutation) {
        this.mutation = Optional.ofNullable(mutation);
        return copy();
    }

    public EvolutionConfigurationBuilder<V, T, K, L> replacement(ReplacementStrategy<V, T, K> replacement) {
        this.replacement = replacement;
        return copy();
    }

    public EvolutionConfigurationBuilder<V, T, K, L> selector(SelectorStrategy<V, T, K> selector) {
        this.selector = selector;
        return copy();
    }

    public EvolutionConfigurationBuilder<V, T, K, L> parallel(boolean parallel) {
        isParallel = parallel;
        return copy();
    }

    public EvolutionConfigurationBuilder<V, T, K, L> fitnessIsMaximized(boolean fitnessIsMaximized) {
        isFitnessIsMaximized = fitnessIsMaximized;
        return copy();
    }

    public EvolutionConfigurationBuilder<V, T, K, L> statisticsCreation(StatisticsCreationStrategy<V, T, K, L> statisticsCreation) {
        this.statisticsCreation = statisticsCreation;
        return copy();
    }

    public EvolutionConfigurationBuilder<V, T, K, L> populationSize(int populationSize) {
        this.populationSize = populationSize;
        return copy();
    }

    public EvolutionConfigurationBuilder<V, T, K, L> probabilityOfCrossover(double probabilityOfCrossover) {
        this.probabilityOfCrossover = probabilityOfCrossover;
        return copy();
    }

    public EvolutionConfigurationBuilder<V, T, K, L> terminationCondition(TerminationCondition<V, T, K, L> terminationCondition) {
        this.terminationCondition = terminationCondition;
        return copy();
    }

    public EvolutionConfigurationBuilder<V, T, K, L> decoding(DecodingStrategy<V, T> decoding) {
        this.decoding = decoding;
        return copy();
    }

    private EvolutionConfigurationBuilder<V, T, K, L> copy() {
        return new EvolutionConfigurationBuilder<>(fitnessTweakingStrategy, fitnessAssessment, populationInitialization,
                crossover, mutation, replacement, selector, isParallel, isFitnessIsMaximized, statisticsCreation, populationSize,
                probabilityOfCrossover, terminationCondition, decoding);
    }

    private EvolutionConfigurationBuilder(Optional<FitnessTweakingStrategy<V, T, K>> fitnessTweakingStrategy, FitnessAssessmentStrategy<T, K> fitnessAssessment,
                                          PopulationInitializationStrategy<V, T> populationInitialization, Optional<CrossoverStrategy<V, T>> crossover,
                                          Optional<MutationStrategy<V, T>> mutation, ReplacementStrategy<V, T, K> replacement,
                                          SelectorStrategy<V, T, K> selector, boolean isParallel, boolean isFitnessIsMaximized,
                                          StatisticsCreationStrategy<V, T, K, L> statisticsCreation, int populationSize, double probabilityOfCrossover,
                                          TerminationCondition<V, T, K, L> terminationCondition,
                                          DecodingStrategy<V, T> decoding) {
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

    public EvolutionConfiguration<V, T, K, L> build() {

        //TODO check

        return new EvolutionConfiguration<>(fitnessTweakingStrategy, fitnessAssessment, populationInitialization,
                crossover, mutation, replacement, selector, isParallel, isFitnessIsMaximized, statisticsCreation, populationSize,
                probabilityOfCrossover, terminationCondition, decoding);
    }

}
