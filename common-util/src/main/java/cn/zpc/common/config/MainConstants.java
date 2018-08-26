package cn.zpc.common.config;

/**
 * Description:常量类
 * Author: sukai
 * Date: 2017-09-06
 */

public interface MainConstants {

    public final static String ROOT_DIR = "/WEB-INF/view";

    /**
     * 校验码
     */
    public final static int SUCCESS = 1000;

    /**
     * 身份信息校验失败
     */
    public final static int AUTH_FAILURE = 1001;

    /**
     * 表单信息缺失
     */
    public final static int INFO_NULL = 1002;

    /**
     * 令牌校验失败
     */
    public final static int TOKEN_AUTH_FAILURE = 1003;

    /**
     * 超过范围
     */
    public final static int INFO_OVER_FLOW = 1004;

    /**
     * 未知原因错误
     */
    public final static int UNKNOWN_INFO = 2000;

    /**
     * 找不到该信息
     */
    public final static int INFO_404 = 2001;

    /**
     * 错误的参数
     */
    public final static int INCORRECT_PARA = 2002
            ;
    /**
     * ip地址信息异常
     */
    public final static int IP_EXCEPTION = 2003;



    public final static String APP_VERSION = "app_version";
    public final static String APP_OS = "app_os";


    /**
     * 主数据源名称：系统主数据源
     */
    String db_dataSource_main = "db.source.main";



    String config_scan_jar = "config.scan.jar";

    String config_scan_package = "config.scan.package";

}
