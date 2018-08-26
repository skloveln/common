package cn.zpc.common.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;

/**
 * Author: sukai
 * Date: 2017/8/20.
 */
public class FileUpload {

    /**
     * 得到上传的文件
     * @param session
     * @param file
     * @return
     */
    public static File getFile(HttpSession session, MultipartFile file){
        String path = session.getServletContext().getRealPath("/upload");
        File imageFile = new File(path, file.getOriginalFilename());
        try {
            if (!imageFile.getParentFile().exists()) {  //判断父目录路径是否存在
                imageFile.getParentFile().mkdirs(); //不存在则创建父目录
                imageFile.createNewFile();
            }
            file.transferTo(imageFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        return imageFile;
    }

}
