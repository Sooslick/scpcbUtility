package ru.sooslick.scpcb.pathfinder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PathFinderFactory {

    public static PathFinder createInstance(String className) throws ReflectiveOperationException {
        Class<?> probablyClass = Class.forName(className);
        if (PathFinder.class.isAssignableFrom(probablyClass))
            return (PathFinder) probablyClass.getDeclaredConstructor().newInstance();
        else
            throw new IllegalArgumentException(className + "is not a PathFinder class");
    }

    public static void detectPathFinders() {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream("ru/sooslick/scpcb/pathfinder");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        reader.lines()
                .filter(line -> line.endsWith(".class"))
                .filter(PathFinderFactory::isPathFinder)
                .forEach(System.out::println);
    }

    private static boolean isPathFinder(String className) {
        try {
            Class<?> probablyClass = Class.forName("ru.sooslick.scpcb.pathfinder." + className.substring(0, className.lastIndexOf('.')));
            return PathFinder.class.isAssignableFrom(probablyClass);
        } catch (Exception e){
            return false;
        }
    }
}
