package cz.eoa.cycle;

import cz.eoa.configuration.EvolutionConfiguration;
import cz.eoa.templates.Individual;
import cz.eoa.templates.IndividualWithAssignedFitness;
import cz.eoa.templates.IndividualWithTweakedFitness;
import cz.eoa.templates.StatisticsPerEpoch;
import cz.eoa.templates.operations.IndividualFitnessUpdater;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EvolutionExecutor<V, T, K extends Comparable<K>, L extends StatisticsPerEpoch<V, T, K>> {
    private final EvolutionConfiguration<V, T, K, L> configuration;
    private final static Logger logger = Logger.getLogger(EvolutionExecutor.class.getName());
    private final static Random RANDOM = new Random();

    public EvolutionExecutor(EvolutionConfiguration<V, T, K, L> configuration) {
        this.configuration = configuration;
    }

    public List<L> run() {
        List<L> epochStatistics = new ArrayList<>();

        //start init first population
        long start = System.currentTimeMillis();
        Stream<Individual<V, T>> populationStream = IntStream.range(0, configuration.getPopulationSize()).boxed()
                .map(integer -> configuration.getPopulationInitialization().initialize());
        Stream<IndividualWithAssignedFitness<V, T, K>> populationWithAssignedFitness = assignFitnessToIndividuals(populationStream);

        //recompute fitness if tweaking is set
        if (configuration.getFitnessTweakingStrategy().isPresent()) {
            populationWithAssignedFitness = recomputeFitnessForPopulation((configuration.isParallel() ? populationWithAssignedFitness.parallel() : populationWithAssignedFitness).collect(Collectors.toList()));
        }

        List<IndividualWithAssignedFitness<V, T, K>> currentPopulation = (configuration.isParallel() ? populationWithAssignedFitness.parallel() : populationWithAssignedFitness).collect(Collectors.toList());
        epochStatistics.add(createNewStatistics(0, start, currentPopulation.size(), currentPopulation));
        logger.info(epochStatistics.get(epochStatistics.size() - 1).getSummary());
        //end init first population

        //execute epochs, terminate when user's condition is met
        int epoch = 1;
        while (configuration.getTerminationCondition().shouldTerminate(epochStatistics)) {
            start = System.currentTimeMillis();
            int fitnessEvaluations = 0;

            //seed of new generation
            List<IndividualWithAssignedFitness<V, T, K>> newIndividuals = configuration.getReplacement()
                    .getIndividualsToIncludeInNextGeneration(Collections.unmodifiableList(currentPopulation));
            if (configuration.getFitnessTweakingStrategy().isPresent()) {
                newIndividuals = newIndividuals.stream().map(ind -> ((IndividualWithTweakedFitness<V, T, K>) ind).getOriginalIndividual())
                        .collect(Collectors.toList());
            }

            //init rest of the population in new generation
            while (newIndividuals.size() < configuration.getPopulationSize()) {
                int pairs = ((configuration.getPopulationSize() - newIndividuals.size()) % 2 == 0 ? 0 : 1) + ((configuration.getPopulationSize() - newIndividuals.size()) / 2);

                //chain operations as much as possible - to run them in parallel if configured
                List<IndividualWithAssignedFitness<V, T, K>> pool = Collections.unmodifiableList(currentPopulation);
                Stream<List<IndividualWithAssignedFitness<V, T, K>>> streamOfParentsPairs = IntStream.range(0, pairs).boxed()
                        .map(integer -> {
                            IndividualWithAssignedFitness<V, T, K> firstParent = configuration.getSelector().select(pool), secondParent = null;
                            while (secondParent == null || firstParent.equals(secondParent)) {
                                secondParent = configuration.getSelector().select(pool);
                            }
                            return Stream.of(firstParent, secondParent).collect(Collectors.toList());
                        });
                Stream<IndividualWithFitnessAssessmentStatus> newBatchOfIndividualsStream = streamOfParentsPairs.flatMap(parents -> {
                    if (configuration.getCrossover().isPresent() && RANDOM.nextDouble() <= configuration.getProbabilityOfCrossover()) {
                        Stream<Individual<V, T>> offspring = configuration.getCrossover().get().crossover(parents.get(0).getIndividual(), parents.get(1).getIndividual()).stream();
                        if (configuration.getMutation().isPresent()) {
                            offspring = offspring
                                    .map(individual -> configuration.getMutation().get().mutation(individual))
                                    .filter(Optional::isPresent)
                                    .map(Optional::get);
                        }
                        return assignFitnessToIndividuals(offspring).map(ind -> new IndividualWithFitnessAssessmentStatus(ind, true));
                    } else {
                        if (configuration.getMutation().isPresent()) {
                            return parents.stream()
                                    .map(individual -> {
                                        Optional<Individual<V, T>> mutated = configuration.getMutation().get().mutation(individual.getIndividual());
                                        if (mutated.isPresent() && !mutated.get().equals(individual.getIndividual())) {
                                            return new IndividualWithFitnessAssessmentStatus(new IndividualWithAssignedFitness<>(mutated.get(),
                                                    configuration.getFitnessAssessment().computeFitnessForIndividual(mutated.get().decode(configuration.getDecoding()))), true);
                                        }
                                        return new IndividualWithFitnessAssessmentStatus(individual, false);
                                    });
                        } else {
                            return parents.stream().map(ind -> new IndividualWithFitnessAssessmentStatus(ind, false));
                        }
                    }
                });
                List<IndividualWithFitnessAssessmentStatus> newBatchOfIndividuals = (configuration.isParallel() ? newBatchOfIndividualsStream.parallel() : newBatchOfIndividualsStream).collect(Collectors.toList());
                fitnessEvaluations = fitnessEvaluations + (int) newBatchOfIndividuals.stream().filter(IndividualWithFitnessAssessmentStatus::isFitnessRecomputed).count();
                newIndividuals.addAll(newBatchOfIndividuals.stream().map(IndividualWithFitnessAssessmentStatus::getIndividual).collect(Collectors.toList()));
            }

            //recompute fitness if tweaking is set
            if (configuration.getFitnessTweakingStrategy().isPresent()) {
                newIndividuals = recomputeFitnessForPopulation(newIndividuals).collect(Collectors.toList());
            }

            //make new generation and statistics
            //trim population if needed based on fitness
            if (newIndividuals.size() > configuration.getPopulationSize()) {
                Collections.sort(newIndividuals);
                if (configuration.isFitnessIsMaximized()) {
                    Collections.reverse(newIndividuals);
                }
                currentPopulation = newIndividuals.subList(0, configuration.getPopulationSize());
            } else {
                currentPopulation = newIndividuals;
            }
            epochStatistics.add(createNewStatistics(epoch, start, currentPopulation.size(), currentPopulation));
            logger.info(epochStatistics.get(epochStatistics.size() - 1).getSummary());

            epoch++;
        }

        return epochStatistics;
    }

    @AllArgsConstructor
    @Getter
    private class IndividualWithFitnessAssessmentStatus {
        private final IndividualWithAssignedFitness<V, T, K> individual;
        private final boolean isFitnessRecomputed;
    }

    private L createNewStatistics(int epoch, long startTime, int countOfFitnessEvaluation, List<IndividualWithAssignedFitness<V, T, K>> currentPopulation) {
        return configuration.getStatisticsCreation().returnStatistics(epoch, System.currentTimeMillis() - startTime,
                countOfFitnessEvaluation, (configuration.isFitnessIsMaximized() ? currentPopulation.stream().max(Comparator.comparing(ind -> ind)) :
                        currentPopulation.stream().min(Comparator.comparing(ind -> ind))).get(), Collections.unmodifiableList(currentPopulation));
    }

    private Stream<IndividualWithAssignedFitness<V, T, K>> assignFitnessToIndividuals(Stream<Individual<V, T>> individualsStream) {
        return individualsStream.map(vIndividual -> new IndividualWithAssignedFitness<>(vIndividual, configuration.getFitnessAssessment().computeFitnessForIndividual(vIndividual.decode(configuration.getDecoding()))));
    }

    private Stream<IndividualWithAssignedFitness<V, T, K>> recomputeFitnessForPopulation(List<IndividualWithAssignedFitness<V, T, K>> population) {
        if (configuration.getFitnessTweakingStrategy().isPresent()) {
            IndividualFitnessUpdater<V, T, K> updater = configuration.getFitnessTweakingStrategy().get().getIndividualUpdater(Collections.unmodifiableList(population));
            return IntStream.range(0, population.size()).boxed()
                    .map(integer -> population.get(integer).updateFitness(integer, population, updater));
        }
        return population.stream();
    }

}
