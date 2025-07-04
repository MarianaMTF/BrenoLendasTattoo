package com.projecttattoo.BrenoLendaTattoo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projecttattoo.BrenoLendaTattoo.models.Artista;

public interface ArtistaRepository extends JpaRepository<Artista, Integer>{
	public Artista findById(int id);
	public Artista findByEmail(String email);
	public boolean existsByEmail(String email);
}
