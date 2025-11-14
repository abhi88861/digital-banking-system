package com.bank.digital_banking.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserRegisteredEvent {
    private final Long userId;
    private final String username;
}
