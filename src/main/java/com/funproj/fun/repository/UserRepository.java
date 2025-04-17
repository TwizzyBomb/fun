package com.funproj.fun.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import com.funproj.fun.model.User;

/**
 * Reactive repository for {@link User} entities providing CRUD operations
 * and custom query methods.
 *
 * <p>This repository extends {@link ReactiveCrudRepository} to inherit reactive
 * CRUD operations and adds custom query methods for user-specific operations.
 *
 * <p>All methods return reactive types (Mono/Flux) for non-blocking operations.
 */
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {

    /**
     * Finds a user by their unique username.
     *
     * @param username the username to search for (case-sensitive)
     * @return a Mono emitting the found User or empty if not found
     *
     * <p>Example usage:
     * <pre>{@code
     * userRepository.findByUsername("admin")
     *     .subscribe(user -> System.out.println("Found user: " + user));
     * }</pre>
     */
    Mono<User> findByUsername(String username);
}