package cn.zpc.common.utils;

import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.user.utils.UserUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * Author: sukai
 * Date: 2017-09-06
 */
public class FileUtils<T> extends org.apache.commons.io.FileUtils {

    private T entity;
    private static HttpServletRequest request;
    private static Map<String, String> fields = new HashMap<String, String>();
    private static Map<String, File> files = new HashMap<String, File>();
    private static FileResult fileResult;
    private String SAVE_PATH = request.getServletContext().getRealPath("/WEB-INF/upload");

    /**
     * 创建文件目录
     * @param filePath
     * @param fileName
     * @throws IOException
     */
    public static void makeFileDir(String filePath, String fileName) throws IOException {
        File dir = new File(filePath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File targetFile = new File(filePath + File.separator + fileName);
        if(!targetFile.exists()){
            targetFile.createNewFile();
        }
    }

    /**
     * 转储文件,将sourceFile转转储为targetFile
     * @param sourceFile
     * @param targetFile
     * @throws IOException
     */
    public static void transferTo(MultipartFile sourceFile, File targetFile) throws IOException {

        FileUtils.makeFileDir(targetFile.getParent(), targetFile.getName());
        sourceFile.transferTo(targetFile);
    }

    /**
     * 将mutipart接收的文件重命名并转换为File
     * @param file
     * @param userId
     * @param savaPath
     * @return
     */
    public static File transferFile(MultipartFile file, Integer userId, String savaPath){
        String originalFilename = file.getOriginalFilename(); // 获取原文件名
        String fileName = UserUtils.generateToken(String.valueOf(userId));  // 根据ID生成新的文件名
        if (originalFilename.contains(".")) { // 加文件后缀
            String fileExt = originalFilename.substring(originalFilename.lastIndexOf("."));
            fileName += fileExt;
        }
        File targetFile = new File(savaPath, fileName);
        try {
            FileUtils.transferTo(file, targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GlobalExceptionResult("upload.file.exception", Result.EXCEPTION);
        }
        return targetFile;
    }

    public static FileResult getResource(HttpServletRequest request) {



        return new FileResult(fields, files);
    }

    /**
     * 下载文件到本地
     *
     * @param urlString 被下载的文件地址
     * @param filename  文件名(全路径)
     * @throws Exception 各种异常
     */
    public static void download(String urlString, String filename){

        File file = new File(filename);
        if(!file.exists()){
            file.getParentFile().mkdirs();
        }

        try {
            // 构造URL
            URL url = new URL(urlString);
            // 打开连接
            URLConnection con = url.openConnection();
            // 输入流
            InputStream is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            OutputStream os = new FileOutputStream(filename);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
