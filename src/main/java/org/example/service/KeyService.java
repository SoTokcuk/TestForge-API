package org.example.service;

import org.example.entity.Key;
import org.example.repository.KeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KeyService {
    private final KeyRepository keyRepository;

    public KeyService(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    @Transactional(readOnly = true)
    public List<Key> getAllKeys() {
        return keyRepository.findAll();
    }

    @Transactional
    public Key updateKey(Long id, String key) {
        Key oldKey = keyRepository.getReferenceById(id);
        oldKey.setKey(key);
        return keyRepository.save(oldKey);
    }

    @Transactional
    public void deleteKey(Long id) {
        Key key = keyRepository.getReferenceById(id);
        keyRepository.delete(key);
    }

    @Transactional
    public Key createKey(Key key) {
        return keyRepository.save(key);
    }
}
