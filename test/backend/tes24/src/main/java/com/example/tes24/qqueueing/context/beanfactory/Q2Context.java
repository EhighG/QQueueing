package com.example.tes24.qqueueing.context.beanfactory;

import com.example.tes24.qqueueing.annotation.Q2AutoConfiguration;
import com.example.tes24.qqueueing.annotation.Q2Bean;
import com.example.tes24.qqueueing.context.loader.Q2ClassLoader;
import com.example.tes24.qqueueing.context.loader.Q2PropertiesLoader;
import com.example.tes24.qqueueing.exception.InstantiateFailureException;
import com.example.tes24.qqueueing.properties.Q2HttpHeaderProperties;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Q2Context implements Q2BeanFactory {
    static {
        INSTANCE = new Q2Context();
    }

    private static final Q2Context INSTANCE;

    public static Q2Context getInstance() {
        if (!isLoaded) {
            loadProperties();
            loadAutoConfiguration();
            isLoaded = true;
        }

        return INSTANCE;
    }

    private static Map<Class<?>, Object> instanceContainer;
    private static Q2ClassLoader q2ClassLoader;
    private static Q2PropertiesLoader q2PropertiesLoader;
    private static boolean isLoaded = false;

    private Q2Context() {
        this.instanceContainer = new HashMap<>();
        this.q2ClassLoader = new Q2ClassLoader();
        this.q2PropertiesLoader = new Q2PropertiesLoader();
    }

    @Override
    public Object get(Class<?> clazz) {
        return instanceContainer.get(clazz);
    }

    private static void loadProperties() {
        Q2HttpHeaderProperties q2HttpHeaderProperties = q2PropertiesLoader.getLoadedProperties();
        instanceContainer.put(q2PropertiesLoader.getLoadedProperties().getClass(), q2HttpHeaderProperties);
    }

    private static void loadAutoConfiguration() {
        List<?> autoConfigurationList = q2ClassLoader.getLoadedClasses().stream()
                .filter(clazz -> clazz.getAnnotation(Q2AutoConfiguration.class) != null)
                .map(autoConfigurableClass -> Instantiator.instantiate(autoConfigurableClass))
                .peek(autoConfiguration -> instanceContainer.put(autoConfiguration.getClass(), autoConfiguration))
                .collect(Collectors.toList());

        Instantiator.instantiate(autoConfigurationList).stream()
                .forEach(instance -> instanceContainer.put(instance.getClass(), instance));
    }

    private static class Instantiator {
        static Object instantiate(Class<?> targetClass) {
            return ForkJoinPool.commonPool().submit(new ClassInstantiateRecursiveTask(targetClass)).join();
        }

        static class ClassInstantiateRecursiveTask extends RecursiveTask<Object> {
            private final Class<?> clazz;

            private ClassInstantiateRecursiveTask(Class<?> clazz) {
                this.clazz = clazz;
            }

            @Override
            protected Object compute() {
                if (clazz.isInterface() || clazz.isArray() || clazz.isPrimitive()) {
                    throw new InstantiateFailureException("Can not instantiate ".concat(clazz.getSimpleName()));
                }

                Constructor constructor = Arrays.stream(clazz.getConstructors())
                        .filter(constructor1 -> Modifier.isPublic(constructor1.getModifiers()))
                        .sorted(Comparator.comparing(Constructor::getParameterCount))
                        .skip(clazz.getConstructors().length - 1)
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("There are no proper constructors to create instance of ".concat(clazz.getSimpleName())));

                Object instance;
                try {
                    if (constructor.getParameterCount() > 0) {
                        List<ForkJoinTask<?>> subtaskList = new ArrayList<>();
                        for (Parameter parameters : constructor.getParameters()) {
                            subtaskList.add(new ClassInstantiateRecursiveTask(parameters.getType()).fork());
                        }

                        Object[] parameters = new Object[constructor.getParameterCount()];
                        int i = 0;
                        for (ForkJoinTask<?> subtask : subtaskList) {
                            parameters[i++] = subtask.join();
                        }

                        instance = constructor.newInstance(parameters);
                    } else {
                        instance = constructor.newInstance();
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new InstantiateFailureException("Can not instantiate ".concat(clazz.getSimpleName()), e);
                }

                return instance;
            }
        }

        static List<?> instantiate(List<?> autoConfigurationList) {
            final Map<Class<?>, Method> methodMap = autoConfigurationList.stream()
                    .map(Object::getClass)
                    .map(Class::getMethods)
                    .flatMap(methods -> Stream.of(methods))
                    .filter(method -> method.getAnnotation(Q2Bean.class) != null)
                    .collect(Collectors.toUnmodifiableMap(method -> method.getReturnType(), method -> method));

            List<ForkJoinTask<?>> taskList = new ArrayList<>();
            for (Map.Entry<Class<?>, Method> entry : methodMap.entrySet()) {
                taskList.add(ForkJoinPool.commonPool().submit(new MethodInstantiateRecursiveTask(entry.getValue(), methodMap)));
            }

            return taskList.stream()
                    .map(ForkJoinTask::join)
                    .collect(Collectors.toList());
        }

        static class MethodInstantiateRecursiveTask extends RecursiveTask<Object> {
            private final Object autoConfiguration;
            private final Method method;
            private final Map<Class<?>, Method> methodMap;

            private MethodInstantiateRecursiveTask(Method method, Map<Class<?>, Method> methodMap) {
                this.autoConfiguration = instanceContainer.get(method.getDeclaringClass());
                this.method = method;
                this.methodMap = methodMap;
            }

            @Override
            protected Object compute() {
                List<Object> parameterList = new ArrayList<>();
                for (Class<?> parameterType : method.getParameterTypes()) {
                    Object parameter = instanceContainer.get(parameterType);

                    if (parameter == null) {
                        Method target = methodMap.get(parameterType);

                        if (target == null) throw new InstantiateFailureException("Can not find instance of ".concat(parameterType.getName()));

                        parameterList.add(new MethodInstantiateRecursiveTask(target, methodMap).fork().join());
                    } else {
                        parameterList.add(parameter);
                    }
                }

                try {
                    return method.invoke(autoConfiguration, parameterList.toArray());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new InstantiateFailureException("Can not instantiate ".concat(method.getReturnType().getName()), e);
                }
            }
        }
    }

}
