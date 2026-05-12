package io.github.danarrigo.if20502026k01g1doneate.enums;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;

public enum UserRole {
    DONATOR,
    RECIPIENT;

    public static UserRole of(User user) {
        if (user instanceof Donator) {
            return DONATOR;
        }
        if (user instanceof Recipient) {
            return RECIPIENT;
        }
        throw new IllegalStateException("Unknown user type: " + user.getClass().getSimpleName());
    }
}
