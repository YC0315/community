$(function () {
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});

// entityType: 1-帖子; 2-评论
function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType, "entityId":entityId, "entityUserId":entityUserId, "postId":postId},
        function (data) {
            // LikeController 返回的map就是data
            data = $.parseJSON(data);
            // 成功 0
            if (data.code == 0) {
                $(btn).children("i").text(data.likeCount); // 改赞的数量
                $(btn).children("b").text(data.likeStatus == 1 ? '已赞' : "赞"); // 改赞的状态
            } else {
                alert(data.msg);
            }
        }
    );
}

// 置顶
function setTop() {
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#topBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    );
}


// 加精
function setWonderful() {
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#wonderfulBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    );
}

// 删除
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg);
            }
        }
    );
}