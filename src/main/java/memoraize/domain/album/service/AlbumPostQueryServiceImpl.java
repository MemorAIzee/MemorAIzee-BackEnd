package memoraize.domain.album.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import memoraize.domain.album.converter.AlbumPostConverter;
import memoraize.domain.album.entity.Album;
import memoraize.domain.album.enums.SortStatus;
import memoraize.domain.album.exception.InvalidSortStatusException;
import memoraize.domain.album.repository.AlbumPostRepository;
import memoraize.domain.album.web.dto.AlbumPostRequestDTO;
import memoraize.domain.album.web.dto.AlbumPostResponseDTO;
import memoraize.global.enums.statuscode.ErrorStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlbumPostQueryServiceImpl implements AlbumPostQueryService {
	private final AlbumPostRepository albumPostRepository;

	@Override
	@Transactional
	public AlbumPostResponseDTO.AlbumPostPreviewResultPageDTO getAlbumPage(
		AlbumPostRequestDTO.getAlbumPostPageDTO request) {
		Sort sort;
		if (request.getSortStatus() == SortStatus._LATEST) {
			sort = Sort.by(Sort.Direction.DESC, "createdAt");
		} else if (request.getSortStatus() == SortStatus._POPULAR) {
			sort = Sort.by(Sort.Direction.DESC, "viewCount");
		} else {
			throw new InvalidSortStatusException(ErrorStatus._INVALID_SORT_STATUS);
		}
		Pageable pageable = PageRequest.of(request.getPage(), request.getAlbumCount(), sort);
		Page<Album> result;

		// TODO 앨범 공개 범위별로

		result = albumPostRepository.findAll(pageable);
		return AlbumPostConverter.toAlbumPostPreviewResultPageDTO(result);
	}
}
