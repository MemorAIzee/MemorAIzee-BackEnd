package memoraize.domain.review.converter;

import java.util.ArrayList;

import memoraize.domain.review.entity.Place;

public class PlaceConverter {
	public static Place toPlace(String placeName) {
		return Place.builder()
			.placeName(placeName)
			.photoList(new ArrayList<>())
			.reviewList(new ArrayList<>())
			.build();
	}
}
