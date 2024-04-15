package memoraize.global.gcp.map.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisplayName {
	private String text;
	private String languageCode;

	@Override
	public String toString() {
		return "DisplayName{" +
			"text='" + text + '\'' +
			", languageCode='" + languageCode + '\'' +
			'}';
	}
}
