package com.pec.mob.statmonit.object;

public enum ChartType {
    Bar_Unsucc(1),
    Bar_Succ(2),
    Pie_Left_unsucc(3),
    Pie_Left_succ(4),
    Pie_Right_unsucc(5),
    Pie_Right_succ(6),
    Bar_Bank(7),
    Bubble_Trans(8),
    Bar_Mob_top(9),
    Bar_Mob_1(10),
    Bar_Mob_7(11),
    Pie_Mob_top(12),
    Pie_Mob_1(13),
    Pie_Mob_7(14),
    Bar_Agency(15),
    Bar_Inspection(16),
    Bar_Installation(17),
    Bar_Fall(18),
    Bar_Rise(19)
            ;

    private int id;

    ChartType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
