package cz.eoa.templates.operations;

import cz.eoa.templates.Individual;

public interface PopulationInitializationStrategy<V, T> {
    Individual<V, T> initialize();
}
