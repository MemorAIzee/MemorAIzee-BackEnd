package memoraize.global.gcp.map;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.drew.lang.GeoLocation;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import memoraize.global.gcp.map.dto.GooglePlaceApiResponseDTO;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleMapManager {

	@Value("${cloud.google.map.api-key}")
	private String apiKey;

	public String reverseGeocodingWithGoogleMap(GeoLocation geoLocation) throws
		IOException,
		InterruptedException,
		ApiException {
		GeoApiContext context = new GeoApiContext.Builder()
			.apiKey(apiKey)
			.build();

		LatLng coordinates = new LatLng(geoLocation.getLatitude(), geoLocation.getLongitude());
		GeocodingResult[] results = GeocodingApi.reverseGeocode(context, coordinates)
			.await();

		for (GeocodingResult res : results) {
			log.info(res.formattedAddress);
		}

		if (results.length > 0) {
			return results[0].formattedAddress;
		} else
			return null;
	}

	public String placeSearchWithGoogleMap(GeoLocation geoLocation) {
		GeoApiContext context = new GeoApiContext.Builder()
			.apiKey(apiKey)
			.build();

		// LatLng coordinates = new LatLng(geoLocation.getLatitude(), geoLocation.getLongitude());



		/*
		NearbySearchRequest nearbySearchRequest = PlacesApi.nearbySearchQuery(context, coordinates);
		nearbySearchRequest.radius(500);
		nearbySearchRequest.header("X-Goog-FieldMask", "places.displayName");
		nearbySearchRequest.maxPrice(PriceLevel.FREE)

		PlacesSearchResult[] result = null;
		try {
			 result = nearbySearchRequest.await().results;

		} catch (Exception e){
			log.info("주변 검색 API 오류 발생");
			log.error(e.toString());
			return null;
		}
		 */

		WebClient webClient = WebClient.create();

		String requestBody = "{\n"
			+ "  \"maxResultCount\": 5,\n"
			+ "  \"locationRestriction\": {\n"
			+ "    \"circle\": {\n"
			+ "      \"center\": {\n"
			+ "        \"latitude\": " + geoLocation.getLatitude() + ",\n"
			+ "        \"longitude\":" + geoLocation.getLongitude() + "},\n"
			+ "      \"radius\": 200.0\n"
			+ "    }\n"
			+ "  },\n"
			+ "  \"languageCode\": \"ko\"\n"
			+ "}";

		GooglePlaceApiResponseDTO result = webClient.post()
			.uri("https://places.googleapis.com/v1/places:searchNearby")
			.header("Content-Type", "application/json")
			.header("X-Goog-Api-Key", apiKey)
			.header("X-Goog-FieldMask", "places.displayName")
			.bodyValue(requestBody)
			.retrieve()
			.bodyToMono(GooglePlaceApiResponseDTO.class)
			.block();

		result.getPlaces().stream().forEach(place -> {
			log.info("nearby search => {}", place.getDisplayName().getText());
		});

		if (result.getPlaces().isEmpty())
			return null;
		return result.getPlaces().get(0).getDisplayName().getText();
		/*

		AutocompletePlacesRequest.LocationRestriction locationRestriction = AutocompletePlacesRequest.LocationRestriction.newBuilder()
			.setCircle(
				Circle.newBuilder().
				setCenter(com.google.type.LatLng.newBuilder().setLatitude(geoLocation.getLatitude())
				.setLongitude(geoLocation.getLongitude()).build()).setRadius(GoogleMapConstants.RADIUS)
				.build()
			)
			.build();


		GooglePlaceApiResponseDTO response = client.post()
			.contentType(MediaType.APPLICATION_JSON)
			.header("X-Goog-Api-Key", apiKey)
			.header("X-Goog-FieldMask", GoogleMapConstants.FIELDMASK)
			.bodyValue(GooglePlaceApiRequestDTO.builder()
				.maxResultCount(GoogleMapConstants.MAX_RESULT_COUNT)
				.locationRestriction(locationRestriction)
				.build()
			)
			.retrieve()
			.bodyToMono(GooglePlaceApiResponseDTO.class)
			.block();
		*/
	}
}
