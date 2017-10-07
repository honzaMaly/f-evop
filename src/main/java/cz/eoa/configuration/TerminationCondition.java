package cz.eoa.configuration;

import cz.eoa.templates.StatisticsPerEpoch;

import java.util.List;

public interface TerminationCondition<V, T, K extends Comparable<K>, L extends StatisticsPerEpoch<V, T, K>> {
    boolean shouldTerminate(List<L> epochs);
}
