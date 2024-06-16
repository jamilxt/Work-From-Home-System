/*
 * @Author 		 : Valhalla TKT (DAT OJT Batch II - Team III)
 * @Date 		 : 2024-04-24
 * @Time  		 : 21:00
 * @Project_Name : Work From Home System
 * @Contact      : tktvalhalla@gmail.com
 */
package com.kage.wfhs.model;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "capture")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Capture implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
	
    private String os_type;
    
    @Column(columnDefinition = "TEXT")
    private String operationSystem;
    
    @Column(columnDefinition = "TEXT")
    private String securityPatch;
    
    @Column(columnDefinition = "TEXT")
    private String antivirusSoftware;
    
    @Column(columnDefinition = "TEXT")
    private String antivirusPattern;
    
    @Column(columnDefinition = "TEXT")
    private String antivirusFullScan;


    @OneToOne
    @JoinColumn(name = "register_form_id")
    @ToString.Exclude
	@JsonIgnore
    private RegisterForm registerForm;

}
