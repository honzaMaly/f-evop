package cz.eoa.templates.operations;

import cz.eoa.templates.IndividualWithAssignedFitness;

import java.util.List;

public interface IndividualFitnessUpdater<V, T, K extends Comparable<K>> {
    K computeNewFitness(IndividualWithAssignedFitness<V, T, K> individualToRecomputeFitness, List<IndividualWithAssignedFitness<V, T, K>> restOfPopulation);
}
