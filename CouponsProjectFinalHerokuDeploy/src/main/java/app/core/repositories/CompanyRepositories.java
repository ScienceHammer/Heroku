package app.core.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import app.core.entities.Company;

public interface CompanyRepositories extends JpaRepository<Company, Integer> {

	boolean existsByName(String name);

	Optional<Company>  findByEmail(String email);

	boolean existsByIdIsNotAndEmail(int id, String email);

	Optional<Company> findByEmailAndPassword(String email, String password);
	
}
