package com.py.service;

import com.py.common.dto.ApiResponse;
import com.py.dto.PyTradeGroupRequestDto;
import com.py.dto.PyTradeGroupResponseDto;

import java.util.List;

public interface PyTradeGroupService {

    ApiResponse<?> groupOperations(PyTradeGroupRequestDto requestDto);

    ApiResponse<PyTradeGroupResponseDto> getGroupById(Long groupId, boolean getBalance);

    ApiResponse<List<PyTradeGroupResponseDto>> getAllGroupsByUser(Long pyId, boolean getBalance);
}
