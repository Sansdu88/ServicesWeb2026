package M2fst.Guide_Michelin.repository;
import M2fst.Guide_Michelin.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Integer> {
}
