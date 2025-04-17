package ckollmeier.de.asterixapi.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdService {
    /**
     * @return generated ID string
     */
    public String generateId() {
        return UUID.randomUUID().toString();
    }
}
