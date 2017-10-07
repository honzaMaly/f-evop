package cz.eoa.templates.operations;

import cz.eoa.templates.Individual;

import java.util.Optional;

public interface MutationStrategy<V, T> {
    Optional<Individual<V, T>> mutation(Individual<V, T> individual);
}
