package org.evosuite.coverage.methodpair;

import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteFitnessFunction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodPairSuiteFitness extends TestSuiteFitnessFunction {
    // Track the method pairs.
    private final Set<MethodPairTestFitness> allMethodPairs = new HashSet<>();

    public MethodPairSuiteFitness() {
        // Get method pairs from the fitness factory.
        allMethodPairs.addAll(new MethodPairCoverageFactory().getCoverageGoals());
    }

    @Override
    public double getFitness(TestSuiteChromosome suite) {
        double fitness = 0.0;

        // Run all tests and gather the execution results.
        List<ExecutionResult> results = runTestSuite(suite);
        Set<MethodPairTestFitness> coveredMethodPairs = new HashSet<>();

        // Go through and look for covered goals.
        for (MethodPairTestFitness goal : allMethodPairs) {
            for (ExecutionResult result : results) {
                if (goal.isCovered(result)) {
                    coveredMethodPairs.add(goal);
                    break;
                }
            }
        }

        // Fitness is the total number of goals - the number of covered goals.
        // If all goals are covered, fitness will be 0.
        fitness = allMethodPairs.size() - coveredMethodPairs.size();

        // Penalize fitness if the test suite times out.
        for (ExecutionResult result : results) {
            if (result.hasTimeout() || result.hasTestException()) {
                fitness = allMethodPairs.size();
                break;
            }
        }

        // Update the fitness score for the suite.
        updateIndividual(suite, fitness);
        suite.setNumOfCoveredGoals(this, coveredMethodPairs.size());
        if (!allMethodPairs.isEmpty()) {
            suite.setCoverage(this, (double) coveredMethodPairs.size() / (double) allMethodPairs.size());
        } else {
            suite.setCoverage(this, 1.0);
        }
        return fitness;
    }
}