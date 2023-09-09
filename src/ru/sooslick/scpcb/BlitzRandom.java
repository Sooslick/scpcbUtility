package ru.sooslick.scpcb;

public class BlitzRandom {

    static final int RND_A = 48271;
    static final int RND_M = 2147483647;
    static final int RND_Q = 44488;
    static final int RND_R = 3399;

    static int rnd_state;

    public static void bbSeedRnd(int seed) {
        seed &= 0x7fffffff;
        rnd_state = (seed != 0) ? seed : 1;
    }

    public static int bbRand(int from, int to) {
        if (to < from) {
            int a = from;
            from = to;
            to = a;
        }
        return (int) (rnd() * (to - from + 1)) + from;
    }

    public static double bbRnd(float from, float to) {
        return rnd() * (to - from) + from;
    }

    private static double rnd() {
        rnd_state = RND_A * (rnd_state % RND_Q) - RND_R * (rnd_state / RND_Q);
        if (rnd_state < 0) rnd_state += RND_M;
        return (rnd_state & 65535) / 65536.0f + (.5f / 65536.0f);
    }
}
