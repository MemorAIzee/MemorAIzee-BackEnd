package memoraize.domain.photo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import memoraize.domain.album.entity.Album;
import memoraize.domain.review.entity.Place;
import memoraize.global.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Photo extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "photo_id")
	private Long id;


	@Column(name = "photo_url", nullable = false)
	private String imageUrl;


	@Column(name = "photo_comment")
	private String comment;

	@OneToMany(mappedBy = "photo", cascade = CascadeType.ALL)
	private List<PhotoHashTag> photoHashTagList = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "album_id", nullable = false)
	private Album album;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	private Place place;

	// 연관관계 편의 메서드
	public void setAlbum(Album album){
		this.album = album;
	}

	public void setPlace(Place place){
		this.place = place;
	}
}
