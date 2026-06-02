package com.timepieces.automation.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

/**
 * TestNG IAnnotationTransformer implementation to globally inject the custom 
 * RetryAnalyzer to all dynamically compiled test methods at runtime.
 * This is crucial for Cucumber where test methods are dynamically executed 
 * by the runner from Gherkin scenarios.
 */
public class RetryAnnotationTransformer implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
