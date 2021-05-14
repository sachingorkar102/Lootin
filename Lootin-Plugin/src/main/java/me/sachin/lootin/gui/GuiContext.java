package me.sachin.lootin.gui;


public enum GuiContext {
    CHEST(27),
    DOUBLE_CHEST(54),
    MINECART(27);

    private GuiContext(int size){
        this.size = size;
    }

    private int size;
}
