package com.epam.hw.storage;

import com.epam.hw.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class Auth {
    User loggedInUser = null;
}
