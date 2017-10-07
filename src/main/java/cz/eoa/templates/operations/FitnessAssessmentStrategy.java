package cz.eoa.templates.operations;

import cz.eoa.templates.Individual;

public interface FitnessAssessmentStrategy<T, K extends Comparable<K>> {
    K computeFitnessForIndividual(T solution);
}
