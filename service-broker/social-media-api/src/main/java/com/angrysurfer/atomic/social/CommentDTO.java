package com.angrysurfer.atomic.social;

import lombok.Data;

@Data
public class CommentDTO extends AbstractContentDTO {

    private String postId;

    private String parentId;

    public CommentDTO() {
        super();
    }
}
