package memoraize.gcp.map;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.drew.lang.GeoLocation;
import com.google.maps.errors.ApiException;

import memoraize.domain.photo.service.PhotoCommandServiceImpl;
import memoraize.global.gcp.map.GoogleMapManager;

@SpringBootTest
public class GoogleMapManagerTest {
	@Autowired
	GoogleMapManager googleMapManager;

	@Autowired
	PhotoCommandServiceImpl photoCommandService;

	@Test
	public void reverseGeocodingTest() throws IOException, InterruptedException, ApiException {
		double latitude = 33.25850;
		double longitude = 131.53114;
		GeoLocation geoLocation = new GeoLocation(latitude, longitude);
		String location = googleMapManager.reverseGeocodingWithGoogleMap(geoLocation);
		System.out.println(location);

	}

	@Test
	public void placeSearchTest() {
		double latitude = 33.25850;
		double longitude = 131.53114;
		GeoLocation geoLocation = new GeoLocation(latitude, longitude);
		System.out.println(googleMapManager.placeSearchWithGoogleMap(geoLocation));
	}

}
