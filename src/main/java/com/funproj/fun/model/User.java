package com.funproj.fun.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Represents a user entity in the system.
 *
 * <p>This class models the user data that will be persisted to the database,
 * with fields representing the core attributes of a system user. The class
 * is mapped to the "users" table in the database.
 *
 * <p>Uses Lombok's {@code @Data} to automatically generate:
 * <ul>
 *   <li>Getters for all fields</li>
 *   <li>Setters for all non-final fields</li>
 *   <li>{@code toString()}, {@code equals()}, and {@code hashCode()} methods</li>
 * </ul>
 */
@Data
@Table("users")
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private String role;

}
