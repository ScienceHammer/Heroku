package app.core.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import app.core.entities.Customer;

public interface CustomerRepositories extends JpaRepository<Customer, Integer> {

	Optional<Customer> findByEmail(String email);

	boolean existsByIdIsNotAndEmail(int id, String email);

	Optional<Customer> findByEmailAndPassword(String email, String password);
}
