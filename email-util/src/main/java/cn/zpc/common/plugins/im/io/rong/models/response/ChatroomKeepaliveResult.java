package cn.zpc.common.plugins.im.io.rong.models.response;

import cn.zpc.common.plugins.im.io.rong.models.Result;
import cn.zpc.common.plugins.im.io.rong.util.GsonUtil;

public class ChatroomKeepaliveResult extends Result{
    private String[] chatroomIds;

    public ChatroomKeepaliveResult(Integer code, String msg, String[] chatroomIds) {
        super(code, msg);
        this.chatroomIds = chatroomIds;
    }

    public String[] getChatroomIds() {
        return this.chatroomIds;
    }

    public void setChatroomIds(String[] chatroomIds) {
        this.chatroomIds = chatroomIds;
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, ChatroomDemotionMsgResult.class);
    }
}
