package com.kakao.board.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "board")
public class Reply extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long rno;
    private String text;
    private String replyer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
}
