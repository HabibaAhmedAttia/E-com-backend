package task.mentorship.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import task.mentorship.application.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

}
