package com.timepieces.automation.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import lombok.extern.log4j.Log4j2;

/**
 * TestNG IAnnotationTransformer implementation to globally inject the custom
 * RetryAnalyzer to all dynamically compiled test methods at runtime.
 * This is crucial for Cucumber where test methods are dynamically executed
 * by the runner from Gherkin scenarios.
 * 
 * @version 1.0
 * @author Avinash Rath
 * @since 2026-07-03
 */

@Log4j2
public class RetryAnnotationTransformer implements IAnnotationTransformer {

    @Override
    @SuppressWarnings("rawtypes")
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
