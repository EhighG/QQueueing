package com.qqueueing.main.log.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SearchRequestCountResDto {

    private String tomcatRequestCount;
}
