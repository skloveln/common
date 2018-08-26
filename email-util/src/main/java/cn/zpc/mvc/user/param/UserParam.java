package cn.zpc.mvc.user.param;

import cn.zpc.common.utils.StringUtils;
import cn.zpc.mvc.user.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Range;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description:用户需完善信息，全部非必填选项
 * Author: sukai
 * Date: 2017-09-06
 */
@ApiModel(value = "用户参数", description = "用户描述参数")
public class UserParam {

    @ApiModelProperty(value = "性别 0-男 1-女")
    @Range(max = 1, message = "{user.param.gender}")
    private Integer gender; // 性别

    @ApiModelProperty(value = "昵称")
    @Size(max = 18, min = 1, message = "{user.param.nickname.size}")
    private String nickname; // 昵称

    @ApiModelProperty(value = "职业")
    @Size(max = 15, message = "{user.param.profession.size}")
    private String profession; // 职业

//    @ApiModelProperty(value = "出生日期")
//    @Pattern(regexp = "[12]\\d{3}-(0[1-9]|1[0-2])-([0-3][0-9])", message = "{user.param.birth}")
//    private String birth; // 出生日期

//    @ApiModelProperty(value = "邮件")
//    @Size(max = 200)
//    @Email
//    private String email; // 邮件

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

//    public String getBirth() {
//        return birth;
//    }
//
//    public void setBirth(String birth) {
//        this.birth = birth;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }

    public User getUser(){
        User user = new User();
        user.setGender(gender);
        user.setNickname(nickname);
        user.setProfession(profession);
//        Date birthDay = null;
//        try {
//            if(StringUtils.isNotEmpty(birth)){
//                birthDay = new SimpleDateFormat("yyyy-MM-dd").parse(birth);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        user.setAge(birthDay);
//        user.setEmail(email);
        return user;
    }
}
