package edu.kdmk.model.game;

public enum GameType {
    BOARD_GAME("BoardGame"),
    COMPUTER_GAME("ComputerGame");

    private final String typeName;

    GameType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public static GameType fromString(String type) {
        for (GameType gameType : GameType.values()) {
            if (gameType.getTypeName().equalsIgnoreCase(type)) {
                return gameType;
            }
        }
        throw new IllegalArgumentException("Unknown GameType: " + type);
    }
}