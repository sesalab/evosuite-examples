package org.evosuite.coverage.methodpair;

import jdk.internal.org.objectweb.asm.Type;
import org.evosuite.Properties;
import org.evosuite.setup.TestUsageChecker;
import org.evosuite.testsuite.AbstractFitnessFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MethodPairCoverageFactory extends AbstractFitnessFactory<MethodPairTestFitness> {

    @Override
    public List<MethodPairTestFitness> getCoverageGoals() {
        List<MethodPairTestFitness> goals = new ArrayList<>();
        String className = Properties.TARGET_CLASS;
        Class<?> clazz = Properties.getInitializedTargetClass();
        Set<String> constructors = getUsableConstructors(clazz);
        Set<String> methods = getUsableMethods(clazz);

        // Pair each constructor with each method and add to goals.
        for (String constructor : constructors) {
            for (String method : methods) {
                goals.add(new MethodPairTestFitness(className, constructor, method));
            }
        }

        // Pair each method with each other method and add to goals.
        for (String method1 : methods) {
            for (String method2 : methods) {
                goals.add(new MethodPairTestFitness(className, method1, method2));
            }
        }
        return goals;
    }

    /**
     * Returns a list of constructors in the correct format (name + descriptor).
     * Uses reflection to get a list of constructors declared by CUT.
     * For each, produces name and descriptor.
     *
     * @param clazz - class under test
     * @return set of constructors
     */
    protected Set<String> getUsableConstructors(Class<?> clazz) {
        Set<String> constructors = new LinkedHashSet<>();
        Constructor<?>[] allConstructors = clazz.getDeclaredConstructors();
        for (Constructor<?> c : allConstructors) {
            if (TestUsageChecker.canUse(c)) {
                String methodName = "<init>" + Type.getConstructorDescriptor(c);
                constructors.add(methodName);
            }
        }
        return constructors;
    }

    /**
     * Returns a list of methods in the correct format (name + descriptor).
     * Uses reflection to get a list of methods declared by CUT.
     * For each, produces name and descriptor.
     *
     * @param clazz - class under test
     * @return set of constructors
     */
    protected Set<String> getUsableMethods(Class<?> clazz) {
        Set<String> methods = new LinkedHashSet<>();
        Method[] allMethods = clazz.getDeclaredMethods();
        for (Method m : allMethods) {
            if (TestUsageChecker.canUse(m)) {
                String methodName = m.getName() + Type.getMethodDescriptor(m);
                methods.add(methodName);
            }
        }
        return methods;
    }
}