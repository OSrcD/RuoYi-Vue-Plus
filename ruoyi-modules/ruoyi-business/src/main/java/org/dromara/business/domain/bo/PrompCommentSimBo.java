package org.dromara.business.domain.bo;

import lombok.Data;

import java.util.List;

@Data
public class PrompCommentSimBo {
    private List<CommentSimilarityBo> commentList;
}
