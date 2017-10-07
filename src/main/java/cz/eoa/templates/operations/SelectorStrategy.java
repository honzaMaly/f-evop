package cz.eoa.templates.operations;

import cz.eoa.templates.IndividualWithAssignedFitness;

import java.util.List;

public interface SelectorStrategy<V, T, K extends Comparable<K>> {
    IndividualWithAssignedFitness<V, T, K> select(List<IndividualWithAssignedFitness<V, T, K>> population);
}
