package model.player;

public enum Position {
    SOUTH(0, "Nam"),
    WEST(1, "Tây"),
    NORTH(2, "Bắc"),
    EAST(3, "Đông");

    private int order;
    private String name;

    Position(int order, String name) {
        this.order = order;
        this.name = name;
    }

    public int getOrder() {return order;}

    public String getName() {
        return name;
    }
}
