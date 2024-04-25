package com.kage.wfhs.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Notification implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    @ToString.Exclude
	@JsonIgnore
    private User sender;

    @ManyToOne
    @JoinColumn(name = "approver_id")
    @ToString.Exclude
	@JsonIgnore
    private User receiver;
    
    @ManyToOne
    @JoinColumn(name = "notification_type_id", referencedColumnName = "id")
    @ToString.Exclude
    @JsonIgnore
    private NotificationType notificationType;

    @ManyToOne
    @JoinColumn(name = "register_form_id", referencedColumnName = "id")
    @ToString.Exclude
    @JsonIgnore
    private RegisterForm registerForm;

}
