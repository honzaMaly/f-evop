package cz.eoa.templates;

import java.util.List;

public interface StatisticsCreationStrategy<V, T, K extends Comparable<K>, L extends StatisticsPerEpoch<V, T, K>> {
    L returnStatistics(int epoch, long execution, int countOfFitnessEvaluations, IndividualWithAssignedFitness<V, T, K> bestIndividual, List<IndividualWithAssignedFitness<V, T, K>> population);
}
