package com.kakao.board.service;

import com.kakao.board.domain.Board;
import com.kakao.board.domain.Member;
import com.kakao.board.dto.BoardDTO;
import com.kakao.board.dto.PageRequestDTO;
import com.kakao.board.dto.PageResponseDTO;

public interface BoardService {
    // 게시글 등록
    Long register(BoardDTO dto);

    // 게시글 목록 보기
    PageResponseDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO);

    // 게시글 번호를 가지고 댓글을 삭제
    void removeWithReplies(Long bno);

    // 게시글 상세 보기
    BoardDTO get(Long bno);

    // 게시물 수정
    Long modify(BoardDTO dto);

    // DTO -> Entity로 변환해주는 메서드 - Entity 모양새로 DB에 저장하려고 쓰는 것
    default Board dtoToEntity(BoardDTO dto) {
        Member member = Member.builder()
                .email(dto.getWriterEmail())
                .build();
        Board board = Board.builder()
                .bno(dto.getBno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(member)
                .build();
        return board;
    }

    // Entity -> DTO로 변환해주는 메서드
    default BoardDTO entityToDTO(Board board, Member member, Long replyCount) {
        BoardDTO dto = BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .modDate(board.getModDate())
                .regDate(board.getRegDate())
                .writerEmail(member.getEmail())
                .writerName(member.getName())
                .replyCount(replyCount.intValue())
                .build();
        return dto;
    }
}
