package com.excelr.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;
   private String username;
   private String password;
   private long phone;
   private String role;  
}
