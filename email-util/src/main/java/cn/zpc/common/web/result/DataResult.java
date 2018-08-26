package cn.zpc.common.web.result;

/**
 * Description:数据结果
 * Author: sukai
 * Date: 2017-08-15
 */
public class DataResult<T> extends MessageResult {

    private T data;

    public DataResult(){

    }

    public DataResult(int code, String message, T data){
        super.code = code;
        super.message = message;
        this.data = data;
    }

    public DataResult(int code, T data){
        this.code = code;
        this.data = data;
    }

    public DataResult(T data){
        this.data = data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public static Result getNormal(Object data){
        DataResult result = new DataResult();
        result.setMessage("获取数据成功");
        result.setCode(Result.NORMAL);
        result.setData(data);
        return result;
    }


}
