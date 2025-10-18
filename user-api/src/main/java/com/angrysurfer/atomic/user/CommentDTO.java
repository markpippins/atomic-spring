package com.angrysurfer.atomic.user;

import lombok.Data;

@Data
public class CommentDTO extends AbstractContentDTO {

    private Long postId;

    private Long parentId;

    public CommentDTO() {
        super();
    }
}
