package com.ruoyi.business.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class PrompCommentDto {
    private String highSimilarityPrediction;
    private List<String> commentList;
}
