package com.kakao.review.service;

import com.kakao.review.domain.Movie;
import com.kakao.review.domain.MovieImage;
import com.kakao.review.dto.MovieDTO;
import com.kakao.review.dto.PageRequestDTO;
import com.kakao.review.dto.PageResponseDTO;
import com.kakao.review.persistence.MovieImageRepository;
import com.kakao.review.persistence.MovieRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieImageRepository movieImageRepository;

    @Override
    @Transactional // save를 두 개 사용. 처음에 save를 하고나서 튕겨버리면 안되기에 그 다음 save 또한 할 수 있도록 트랜잭션 처리
    public Long register(MovieDTO movieDTO) {
        log.warn("movieDTO" + movieDTO);

        Map<String, Object> entityMap = dtoToEntity(movieDTO);
        // 영화 와 영화 이미지 정보 찾아오기
        Movie movie = (Movie) entityMap.get("movie");
        List<MovieImage> movieImageList = (List<MovieImage>) entityMap.get("imgList");
        movieRepository.save(movie);
        movieImageList.forEach(movieImage -> {
            movieImageRepository.save(movieImage);
        });
        return movie.getMno();
    }

    @Override
    public PageResponseDTO<MovieDTO, Object[]> getList(PageRequestDTO requestDTO) {
        // 정렬 조건을 추가해서 Pageable 객체를 생성
        Pageable pageable = requestDTO.getPageable(Sort.by("mno").descending());
        // 데이터베이스에 요청
        Page<Object[]> result = movieRepository.getList(pageable);
        // Object 배열을 MovieDTO 타입으로 변경하기 위해서 함수를 생성
        // 첫번째 데이터가 Movie
        // 두번째 데이터가 List<MovieImage>
        // 세번째 데이터가 평점의 평균
        // 네번째 데이터가 리뷰의 개수
        Function<Object[], MovieDTO> fn = (arr -> entitiesToDTO(
                // Object 배열이므로 각 배열요소마다 DTO에 맞게 형변환을 해주어야 함.
                (Movie) arr[0],
                (List<MovieImage>) (Arrays.asList((MovieImage) arr[1])),
                (Double) arr[2],
                (Long) arr[3]
        ));

        return new PageResponseDTO<>(result, fn);
    }

    @Override
    public MovieDTO getMovie(Long mno) {
        // 데이터베이스에서 결과 가져오기
        List<Object[]> result = movieRepository.getMovieWithAll(mno);

        Movie movie = (Movie) result.get(0)[0];
        List<MovieImage> movieImageList = new ArrayList<>();
        result.forEach(arr -> {
            MovieImage movieImage = (MovieImage) arr[1];
            movieImageList.add(movieImage);
        });

        double avg = (Double) result.get(0)[2];
        Long reviewCnt = (Long) result.get(0)[3];

        return entitiesToDTO(movie, movieImageList, avg, reviewCnt);
    }
}
