package org.example.controller;

import org.example.entity.Key;
import org.example.service.KeyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keys")
public class KeyController {

    private final KeyService keyService;

    public KeyController(KeyService keyService) {
        this.keyService = keyService;
    }

    @GetMapping
    public List<Key> getAllKeys() {
        return keyService.getAllKeys();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Key createKey(@RequestBody Key key) {
        return keyService.createKey(key);
    }

    @PutMapping("/{id}")
    public Key updateKey(@PathVariable Long id, @RequestBody String newKey) {
        return keyService.updateKey(id, newKey);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKey(@PathVariable Long id) {
        keyService.deleteKey(id);
    }
}
