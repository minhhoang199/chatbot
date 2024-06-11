package com.example.chatwebproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50, nullable = false)
    @NotBlank(message = "Name may not be blank")
    private String username;

    @Column(name = "password", length = 100, nullable = false)
    @NotEmpty(message = "Password may not be empty")
    @Size(min = 8, message = "Password's length must be higher 8")
    private String password;

    @Column(name = "phone", length = 20, nullable = false)
    @Pattern(regexp = "^0\\d{9}$|^84\\d{9}$", message = "Invalid phone")
    private String phone;

    @JsonIgnore
    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    private Set<Room> rooms = new HashSet<>();

    @OneToMany(mappedBy = "followingUser", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = { "followingUser", "followedUser" }, allowSetters = true)
    private Set<Connection> followingConnections = new HashSet<>();

    @OneToMany(mappedBy = "followedUser", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = { "followingUser", "followedUser" }, allowSetters = true)
    private Set<Connection> followedConnections = new HashSet<>();

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private Set<Message> messages = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getPhone(), user.getPhone());
    }
}
