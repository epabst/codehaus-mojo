package moduleB;

import moduleA.ProvidedByA;

public class ConsumesA {
    public static final String WORLD = ProvidedByA.HELLO;
}