package memoraize.domain.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import memoraize.domain.review.entity.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
	Optional<List<Place>> findByPlaceName(String placeName);
}
