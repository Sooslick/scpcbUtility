package ru.sooslick.scpcb.pathfinder;

public class PathFinderFactory {

    public static PathFinder createInstance(String className) throws ReflectiveOperationException {
        Class<?> probablyClass = Class.forName(className);
        if (PathFinder.class.isAssignableFrom(probablyClass))
            return (PathFinder) probablyClass.getDeclaredConstructor().newInstance();
        else
            throw new IllegalArgumentException(className + "is not a PathFinder class");
    }
}
