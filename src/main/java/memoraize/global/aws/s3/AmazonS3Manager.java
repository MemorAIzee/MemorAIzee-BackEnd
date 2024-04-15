package memoraize.global.aws.s3;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import memoraize.domain.photo.entity.Uuid;
import memoraize.global.aws.exception.S3FileSaveException;
import memoraize.global.config.AmazonConfig;
import memoraize.global.enums.statuscode.ErrorStatus;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

	private final AmazonS3 amazonS3;
	private final AmazonConfig amazonConfig;

	public String uploadFile(String keyName, MultipartFile file) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(file.getContentType());
		metadata.setContentLength(file.getSize());
		try {
			PutObjectResult putObjectResult = amazonS3.putObject(
				new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));
			log.info("result={}", putObjectResult.getContentMd5());
		} catch (IOException e) {
			log.error("error at AmazonS3Manager uploadFile : {}", (Object)e.getStackTrace());
			throw new S3FileSaveException(ErrorStatus._S3_FILE_SAVE_ERROR);
		}

		return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
	}

	public String generatePhotoImageKeyName(Uuid uuid) {
		return amazonConfig.getPhotoImagePath() + '/' + uuid.getUuid();
	}

	public String generateReviewImageKeyName(Uuid uuid) {
		return amazonConfig.getReviewImagePath() + '/' + uuid.getUuid();
	}

}
