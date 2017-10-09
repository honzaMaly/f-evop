# f-evop

The f-evop is a simple plug&play "library" for classical evolutionary algorithms. The main advantage of the library is the ability to embed problem and processes easily. By default, most operations in the evolutionary cycle are executed in parallel.

Let's consider problem of finding x, that x maximise expression f(x) = 2.0 + (x / 50.0) + sin(x) + 2.0 * sin(pi * x / 5.0), x in (0, 150). Here is example of main class to do that using evolutionary approach:

```java
public class Main {

    //parameters + configuration
    private static final Random RANDOM = new Random();
    private static final int MAX_GENES = 24;
    private static final double MIN_X = 0.0;
    private static final double MAX_X = 150.0;
    private static final double RANGE_X = MAX_X - MIN_X;
    private final static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        //types by order: genes, decoded genes - solution, fitness, container with statistics
        EvolutionConfiguration<int[], Double, Double, MyStatisticsPerEpoch> evolutionConfiguration = (new EvolutionConfigurationBuilder<int[], Double, Double, MyStatisticsPerEpoch>())
                //uniform crossover
                .crossover((firstParent, secondParent) -> {
                    int[] firstSetOfGenes = new int[MAX_GENES], secondSetOfGenes = new int[MAX_GENES];
                    // Each gene is inherited either from the 1st or the 2nd parent
                    for (int i = 0; i < MAX_GENES; i++) {
                        if (RANDOM.nextBoolean()) {
                            firstSetOfGenes[i] = firstParent.getGenes()[i];
                        } else {
                            firstSetOfGenes[i] = secondParent.getGenes()[i];
                        }

                        if (RANDOM.nextBoolean()) {
                            secondSetOfGenes[i] = secondParent.getGenes()[i];
                        } else {
                            secondSetOfGenes[i] = firstParent.getGenes()[i];
                        }
                    }
                    return Stream.of(new Individual<int[], Double>(firstSetOfGenes), new Individual<int[], Double>(secondSetOfGenes)).collect(Collectors.toList());
                })
                //simple bit-flip mutation
                .mutation(individual -> {
                    int[] genes = individual.getGenes().clone();
                    for (int i = 0; i < genes.length; i++) {
                        // Mutate each gene with probability Pm.
                        if (RANDOM.nextDouble() < 0.01) {
                            genes[i] = (genes[i] + 1) % 2;    // swap between 0 and 1
                        }
                    }
                    return Optional.of(new Individual<>(genes));
                })
                //tournament selection
                .selector(population -> {
                    //First member of tournament selection.
                    int winnerIndex = RANDOM.nextInt(population.size());

                    // Try and check another n randomly chosen individuals.
                    for (int i = 0; i < 3; i++) {
                        int candidate = RANDOM.nextInt(population.size());
                        if (population.get(candidate).getFitness() > population.get(winnerIndex).getFitness()) {
                            winnerIndex = candidate;
                        }
                    }

                    return population.get(winnerIndex);
                })
                //generational replacement strategy. keep nothing from previous population
                .replacement(currentPopulation -> new ArrayList<>())
                //strategy to initialize single individual - do it randomly
                .populationInitialization(() -> {
                    // Allocate memory for genes array.
                    int[] genes = new int[MAX_GENES];

                    // Randomly initialise genes of the individual.
                    for (int i = 0; i < MAX_GENES; i++) {
                        if (RANDOM.nextBoolean()) {
                            genes[i] = 0;
                        } else {
                            genes[i] = 1;
                        }
                    }
                    return new Individual<>(genes);
                })
                //strategy how to decode genes
                .decoding(Main::decode)
                //how fitness is computed
                .fitnessAssessment(Main::calculateFitness)
                .fitnessIsMaximized(true)
                .parallel(true)
                .probabilityOfCrossover(0.75)
                .populationSize(50)
                //when to terminate evolution, after 100 epochs has been reached
                .terminationCondition(epochs -> epochs.size() < 100)
                //use own statistics
                .statisticsCreation(MyStatisticsPerEpoch::new)
                .build();
        EvolutionExecutor<int[], Double, Double, MyStatisticsPerEpoch> evolutionExecutor = new EvolutionExecutor<>(evolutionConfiguration);
        List<MyStatisticsPerEpoch> statistics = evolutionExecutor.run();
        long time = statistics.stream().mapToLong(StatisticsPerEpoch::getExecution).sum();
        MyStatisticsPerEpoch bestEpoch = statistics.stream().max(Comparator.comparing(stats -> stats.getBestIndividual().getFitness())).get();
        logger.info("Executed in " + time + ", best solution in epoch " + bestEpoch.getEpoch());
    }

    /**
     * Decodes genotype stored in 'genes'.
     *
     * @return an value coded by binary genes
     */
    private static double decode(int[] genes) {
        double pom = Math.pow(2.0, MAX_GENES) - 1.0;
        int power = 1;
        double temp = 0.0;
        for (int i = 0; i < MAX_GENES; i++) {
            temp += (power * genes[i]);
            power *= 2;
        }
        return MIN_X + (temp / pom) * RANGE_X;
    }

    /**
     * Fitness Function Definition - highly multimodal function.
     * f(x) = 2.0 + (x / 50.0) + Math.sin(x) + 2.0 * Math.sin(Math.PI * x / 5.0), x in (0, 150)
     *
     * @param x
     */
    private static double calculateFitness(double x) {
        return 2.0 + (x / 50.0) + Math.sin(x) + 2.0 * Math.sin(Math.PI * x / 50.0);
    }

    /**
     * Own implementation of class with statistics, most important is method getSummary(). It is used to store and print results
     */
    private static class MyStatisticsPerEpoch extends StatisticsPerEpoch<int[], Double, Double> {

        MyStatisticsPerEpoch(int epoch, long execution, int countOfFitnessEvaluations, IndividualWithAssignedFitness<int[], Double, Double> bestIndividual, List<IndividualWithAssignedFitness<int[], Double, Double>> population) {
            super(epoch, execution, countOfFitnessEvaluations, bestIndividual, population);
        }

        public String getSummary() {
            return "Epoch " + epoch + ", avg. fitness: " + population.stream().mapToDouble(IndividualWithAssignedFitness::getFitness).average().orElse(0) + ", #fitness evaluations: " + countOfFitnessEvaluations + ", execution time:" + execution + "\n"
                    + "result: " + decode(bestIndividual.getGenes()) + ", best fitness: " + bestIndividual.getFitness().toString();
        }
    }

}
```