package com.qqueueing.main.registration.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class RegistrationDto {

    private Long id;
    private String registrationUrl;
    private Integer maxCapacity;
    private Integer processingPerMinute;
    private String serviceName;
    private String queueImageUrl;
    private Boolean isActive;

}
