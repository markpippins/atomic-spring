package com.angrysurfer.atomic.user;

import lombok.Data;

@Data
public class CommentDTO extends AbstractContentDTO {

    private String postId;

    private String parentId;

    public CommentDTO() {
        super();
    }
}
