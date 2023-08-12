package ru.practicum.shareit.enums;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static void from(String state) {
        try {
            State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (State value : State.values()) {
                if (value.name().equals(state)) {
                    return;
                }
            }
            throw new IllegalArgumentException(String.format("Unknown state: %s", state));
        }
    }
}
