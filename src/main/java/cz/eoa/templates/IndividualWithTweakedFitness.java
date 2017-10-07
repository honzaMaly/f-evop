package cz.eoa.templates;

import lombok.Getter;

@Getter
public class IndividualWithTweakedFitness<V, T, K extends Comparable<K>> extends IndividualWithAssignedFitness<V, T, K> {
    private final IndividualWithAssignedFitness<V, T, K> originalIndividual;

    public IndividualWithTweakedFitness(IndividualWithAssignedFitness<V, T, K> originalIndividual, K fitness) {
        super(originalIndividual.getIndividual(), fitness);
        this.originalIndividual = originalIndividual;
    }
}
