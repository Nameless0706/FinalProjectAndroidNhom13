package com.nhom13.phonemart.model.interfaces;

import com.nhom13.phonemart.dto.BranchDto;

import java.util.List;

public interface BranchCallback {
    void onSuccess(List<BranchDto> branches);
    void onError(Throwable t);
}

