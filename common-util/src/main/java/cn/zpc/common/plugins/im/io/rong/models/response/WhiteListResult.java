package cn.zpc.common.plugins.im.io.rong.models.response;

import cn.zpc.common.plugins.im.io.rong.models.Result;
import cn.zpc.common.plugins.im.io.rong.models.user.UserModel;
import cn.zpc.common.plugins.im.io.rong.util.GsonUtil;

/**
 * @author RongCloud
 */
public class WhiteListResult extends Result{

    private UserModel[] members;

    public WhiteListResult(Integer code, String msg, UserModel[] members) {
        super(code, msg);
        this.members = members;
    }

    public WhiteListResult(UserModel[] members) {
        this.members = members;
    }

    public UserModel[] getMembers() {
        return this.members;
    }

    public void setMembers(UserModel[] members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, WhiteListResult.class);
    }

}
