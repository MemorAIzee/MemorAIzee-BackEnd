package memoraize.global.gcp.map.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Place {
	private DisplayName displayName;

	@Override
	public String toString() {
		return "Place{" +
			"displayName=" + displayName +
			'}';
	}
}
