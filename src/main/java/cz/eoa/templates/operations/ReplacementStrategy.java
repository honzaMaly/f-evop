package cz.eoa.templates.operations;

import cz.eoa.templates.IndividualWithAssignedFitness;

import java.util.List;

public interface ReplacementStrategy<V, T, K extends Comparable<K>> {
    List<IndividualWithAssignedFitness<V, T, K>> getIndividualsToIncludeInNextGeneration(List<IndividualWithAssignedFitness<V, T, K>> currentPopulation);
}
