package task.mentorship.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import task.mentorship.application.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

}
