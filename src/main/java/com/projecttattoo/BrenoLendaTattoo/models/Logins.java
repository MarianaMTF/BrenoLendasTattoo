package com.projecttattoo.BrenoLendaTattoo.models;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.projecttattoo.BrenoLendaTattoo.enums.Roles;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@Entity
@Table(name = "logins")
public class Logins implements UserDetails{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "email", nullable = false, length = 255)
	private String email;

	@Column(name = "nome", nullable = false, length = 100)
	private String nome;

	@Column(name = "senha", nullable = false)
	private String senha;

	@Column(name = "token", length = 255)
	private String token;

	@OneToOne
	@JoinColumn(name = "id_Cliente")
	private Cliente cliente;
	
	@OneToOne
	@JoinColumn(name = "id_artista")
	private Artista artista;

	@Column(name = "userRole")
	private Roles userRole;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if(this.userRole == Roles.ADMIN) {
			return List.of(
					new SimpleGrantedAuthority("ROLE_ADMIN"),
					new SimpleGrantedAuthority("ROLE_USER")
			);
		}
		
		return List.of(
				new SimpleGrantedAuthority("ROLE_USER")
		);
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return senha;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
