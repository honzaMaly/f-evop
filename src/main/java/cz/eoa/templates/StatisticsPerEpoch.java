package cz.eoa.templates;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StatisticsPerEpoch<V, T, K extends Comparable<K>> {
    protected final int epoch;
    protected final long execution;
    protected final int countOfFitnessEvaluations;
    protected final IndividualWithAssignedFitness<V, T, K> bestIndividual;
    protected final List<IndividualWithAssignedFitness<V, T, K>> population;

    public String getSummary() {
        return "Epoch " + epoch + ", best fitness: " + bestIndividual.getFitness().toString() + ", #fitness evaluations: " + countOfFitnessEvaluations + ", execution time:" + execution;
    }
}
