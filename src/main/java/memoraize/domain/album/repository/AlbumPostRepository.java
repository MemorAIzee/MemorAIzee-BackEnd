package memoraize.domain.album.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import memoraize.domain.album.entity.Album;

public interface AlbumPostRepository extends JpaRepository<Album, Long> {
	Page<Album> findAll(Pageable pageable);
}
