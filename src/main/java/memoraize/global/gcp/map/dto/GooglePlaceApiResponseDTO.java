package memoraize.global.gcp.map.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class GooglePlaceApiResponseDTO {

	private List<Place> places;


	@Override
	public String toString() {
		return "GooglePlaceApiResponseDTO{" +
			"places=" + places +
			'}';
	}
}

