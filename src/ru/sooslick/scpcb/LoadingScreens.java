package ru.sooslick.scpcb;

import java.util.Arrays;

public enum LoadingScreens {
    MTF(1, "Mobile Task Forces"),
    NVGS(2, "Night Vision Goggles"),
    SCP012(3, "SCP-012"),
    SCP035(4, "SCP-035"),
    SCP049(5, "SCP-049"),
    SCP066(6, "SCP-066"),
    SCP079(7, "SCP-079"),
    SCP096(8, "SCP-096"),
    SCP106(9, "SCP-106"),
    SCP173(10, "SCP-173"),
    SCP205(11, "SCP-205"),
    SCP294(12, "SCP-294"),
    SCP372(13, "SCP-372"),
    SCP427(14, "SCP-427"),
    SCP500(15, "SCP-500"),
    SCP513(16, "SCP-513"),
    SCP682(17, "SCP-682"),
    SCP714(18, "SCP-714"),
    SCP860(19, "SCP-860"),
    SCP895(20, "SCP-895"),
    SCP914(21, "SCP-914"),
    SCP939(22, "SCP-939"),
    SCP966(23, "SCP-966"),
    SCP970(24, "SCP-970"),
    SCP1025(25, "SCP-1025"),
    SCP1123(26, "SCP-1123"),
    SCP1162(27, "SCP-1162"),
    SCP1499(28, "SCP-1499"),
    CHAOS(29, "The Chaos Insurgency"),
    CLASSD(30, "Class-D Personnel"),
    CWM(31, "CWM"),
    SCP(32, "The SCP Foundation");

    public final int number;
    public final String header;

    LoadingScreens(int number, String header) {
        this.number = number;
        this.header = header;
    }

    public static String find(int b) {
        return Arrays.stream(LoadingScreens.values())
                .filter(s -> s.number == b)
                .findFirst()
                .map(s -> s.header)
                .orElse("");
    }
}
