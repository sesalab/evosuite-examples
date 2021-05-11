package org.evosuite.ga.operators.crossover;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ConstructionFailedException;

public class MiddlePointCrossOver<T extends Chromosome<T>> extends CrossOverFunction<T> {
    @Override
    public void crossOver(T parent1, T parent2) throws ConstructionFailedException {
        if (parent1.size() < 2 || parent2.size() < 2) {
            return;
        }
        int middle1 = (int) Math.round(parent1.size() / 2.0);
        int middle2 = (int) Math.round(parent2.size() / 2.0);
        T t1 = parent1.clone();
        T t2 = parent2.clone();
        parent1.crossOver(t2, middle1, middle2);
        parent2.crossOver(t1, middle2, middle1);
    }
}
