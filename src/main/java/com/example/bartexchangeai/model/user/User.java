package com.example.bartexchangeai.model.user;

import com.example.bartexchangeai.model.BaseEntity;
import com.example.bartexchangeai.model.offer.Offer;
import com.example.bartexchangeai.model.exchange.Exchange;
import com.example.bartexchangeai.model.exchange.Message;
import com.example.bartexchangeai.model.group.Group;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString(exclude = {"offers", "reviewsGiven", "reviewsReceived", "messages", "groups", "initiatedExchanges", "participatedExchanges"})
@EqualsAndHashCode(callSuper = false, exclude = {"offers", "reviewsGiven", "reviewsReceived", "messages", "groups", "initiatedExchanges", "participatedExchanges"})
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column
    private Float rating;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Offer> offers;
    
    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Review> reviewsGiven;
    
    @OneToMany(mappedBy = "reviewedUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Review> reviewsReceived;
    
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Message> messages;
    
    @ManyToMany(mappedBy = "members")
    @JsonIgnore
    private Set<Group> groups;
    
    @OneToMany(mappedBy = "initiator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Exchange> initiatedExchanges;
    
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Exchange> participatedExchanges;
}