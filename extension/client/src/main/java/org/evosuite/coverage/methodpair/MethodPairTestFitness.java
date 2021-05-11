package org.evosuite.coverage.methodpair;

import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.EntityWithParametersStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.Statement;

import java.util.Objects;
import java.util.Set;

public class MethodPairTestFitness extends TestFitnessFunction {
    // Variables used to track the names of called methods.
    private final String className;
    private final String methodName1;
    private final String methodName2;

    // Constructor to set values of class and method names.
    public MethodPairTestFitness(String className, String methodName1, String methodName2) {
        this.className = className;
        this.methodName1 = methodName1;
        this.methodName2 = methodName2;
    }

    // Boilerplate getters/setters.
    public String getClassName() {
        return className;
    }

    public String getMethodName1() {
        return methodName1;
    }

    public String getMethodName2() {
        return methodName2;
    }

    /**
     * @param individual - an individual test case.
     * @param result     - contains information that results from running a test case (i.e., a trace or list of exceptions raised); this is also cached in TestChromosome to prevent unneeded re-executions.
     * @return the fitness score
     */
    @Override
    public double getFitness(TestChromosome individual, ExecutionResult result) {
        double fitness = 1.0;
        boolean seenMethod1Before = false;

        /** EvoSuite stops execution when an exception is thrown by default.
         * A test may contain pairs of methods that execution did not reach.
         * We want to know about positions where exceptions were thrown.
         */
        Set<Integer> exceptionPositions = result.getPositionsWhereExceptionsWereThrown();

        // Iterate over the statements of a test and pick out the method calls.
        for (Statement stmt : result.test) {
            // Is this a method or constructor statement?
            if (stmt instanceof MethodStatement || stmt instanceof ConstructorStatement) {
                /** If it is a method or constructor call,
                 * we need the name of the class and the name of the call.
                 * Note: the name and descriptor are both needed to handle
                 * overloaded methods.
                 */

                EntityWithParametersStatement ps = (EntityWithParametersStatement) stmt;
                String className = ps.getDeclaringClassName();
                String methodName = ps.getMethodName() + ps.getDescriptor();

                // Have we seen method 1 before?
                if (seenMethod1Before) {
                    if (this.className.equals(className) && this.methodName2.equals(methodName)) {
                        // The pair has been covered.
                        fitness = 0.0;
                        break;
                    }
                } else if (this.className.equals(className) && this.methodName1.equals(methodName)) {
                    seenMethod1Before = true;
                    // If first method has been called, we are halfway to our goal.
                    fitness = 0.5;
                } else {
                    seenMethod1Before = false;
                }
            }

            // Stop looking at method pairs when an exception is thrown.
            if (exceptionPositions.contains(stmt.getPosition())) {
                break;
            }
        }

        // Update the fitness of the test case with the new score.
        updateIndividual(individual, fitness);
        return fitness;
    }

    @Override
    public int compareTo(TestFitnessFunction other) {
        /** Should return 0 if two objects compare as equal.
         * Should return +1/-1 otherwise to imply an ordering.
         * Implemented because TestFitnessFunctions are sometimes sorted.
         */
        if (other instanceof MethodPairTestFitness) {
            MethodPairTestFitness otherMethodFitness = (MethodPairTestFitness) other;
            if (className.equals(otherMethodFitness.getClassName())) {
                if (methodName1.equals(otherMethodFitness.getMethodName1())) {
                    return methodName2.compareTo(otherMethodFitness.getMethodName2());
                } else {
                    return methodName1.compareTo(otherMethodFitness.getMethodName1());
                }
            } else {
                return className.compareTo(otherMethodFitness.getClassName());
            }
        }
        return compareClassName(other);
    }

    @Override
    public int hashCode() {
        // Auto-generated from IntelliJ
        int result = className != null ? className.hashCode() : 0;
        result = 31 * result + (methodName1 != null ? methodName1.hashCode() : 0);
        result = 31 * result + (methodName2 != null ? methodName2.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object other) {
        // Auto-generated from IntelliJ
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        MethodPairTestFitness that = (MethodPairTestFitness) other;
        if (!Objects.equals(className, that.className)) return false;
        if (!Objects.equals(methodName1, that.methodName1)) return false;
        return Objects.equals(methodName2, that.methodName2);
    }

    @Override
    public String getTargetClass() {
        return className;
    }

    @Override
    public String getTargetMethod() {
        return methodName1;
    }

}