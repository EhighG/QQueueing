package com.qqueueing.main.waiting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnterQueueResDto {
    private Integer partitionNo;
    private Long order;
    /**
     * 테스트용 : 요청하는 client ip가 모두 같은 환경에서 대기 만료된 요청을 구분하기 위해 ip대신 사용할 값
     */
    private String idVal;

}
