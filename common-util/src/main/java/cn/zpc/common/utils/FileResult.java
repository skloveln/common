package cn.zpc.common.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileResult {

    private Map<String, String> fields = new HashMap<String, String>();
    private Map<String, File> files = new HashMap<String, File>();

    public FileResult(Map<String, String> fields, Map<String, File> files){
        this.fields = fields;
        this.files = files;
    }



}
