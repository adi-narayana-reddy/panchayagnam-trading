
package com.py.dto;


import com.py.common.dto.BrokerRmsDto;
import com.py.common.enums.BrokerType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PyTradeGroupMemberResponseDto {
    private Long memberId;
    private Long brokerAccountId;
    private String clientId;
    private String userName;
    private BrokerType brokerType;
    private LocalDateTime addedAt;
    private boolean isLinked;
    private double cashBalance;
    private BrokerRmsDto brokerRmsDto;
    private boolean isMemberActive;
}
