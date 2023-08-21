package ru.practicum.shareit.enums;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State from(String state) {
        try {
            return State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (State value : State.values()) {
                if (value.name().equals(state)) {
                    return value;
                }
            }
            throw new IllegalArgumentException(String.format("Unknown state: %s", state));
        }
    }
}
