package dev.mm.core.coreservice.service;

import dev.mm.core.coreservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Object usersPage() {
        return null;
    }
}
