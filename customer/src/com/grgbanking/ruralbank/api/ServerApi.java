package com.grgbanking.ruralbank.api;

import com.grgbanking.ruralbank.DemoCache;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ServerApi {

    /**
     * 用户登陆
     */
    public static void login(String phone, String password, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("phone", phone));
        list.add(new BasicNameValuePair("password", password));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/bankUserApi/login", entity, handler);
    }

    /**
     * 用户去登陆
     *
     * @param userid
     * @param handler
     */
    public static void logout(String userid, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userId", userid));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/bankUserApi/logOut", entity, handler);
    }

    /**
     * 获取用户资料
     */
    public static void getUserInfo(String userId, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userId", userId));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/bankUserApi/detail", entity, handler);
    }


    /**
     * 更新用户资料
     */
    public static void updateUserInfo(String userId, String phone, String email, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userId", userId));
        list.add(new BasicNameValuePair("email", email));
        list.add(new BasicNameValuePair("phone", phone));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/bankUserApi/modifyDetail", entity, handler);
    }

    /**
     * 更新密码
     */
    public static void updatePassword(String userId, String oldpassword, String newpassword, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userId", userId));
        list.add(new BasicNameValuePair("oldPassword", oldpassword));
        list.add(new BasicNameValuePair("newPassword", newpassword));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/bankUserApi/modifyPassword", entity, handler);
    }

    /**
     * 重置密码
     */
    public static void resetPassword(String name, String phone, String email, String password, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("name", name));
        list.add(new BasicNameValuePair("phone", phone));
        list.add(new BasicNameValuePair("email", email));
        list.add(new BasicNameValuePair("newPassword", password));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/bankUserApi/forgetPassword", entity, handler);
    }

    /**
     * 文件上传
     *
     * @param params
     * @param handler
     */
    public static void uploadFile(RequestParams params, AsyncHttpResponseHandler handler) {
        ApiHttpClient.post2("/FileUploadServlet", params, handler);
    }

    /**
     * 签到接口
     */
    public static void sign(String jobOrderId, String address, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("userId", DemoCache.getUserid());
        params.put("jobOrderId", jobOrderId);
        params.put("address", address);
        ApiHttpClient.post("/supJobOrder/sign", params, handler);
    }

    /**
     * 获取设备列表
     */
    public static void getdeviceTypeList(AsyncHttpResponseHandler handler) {
        ApiHttpClient.post("/bankJobOrder/supListsByDeviceType", handler);
    }

    /**
     * 根据供应商获取客服列表
     */
    public static void getBankForwardList(String supplierId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("supplierId", supplierId);
        ApiHttpClient.post("/bankJobOrder/forwardList", params, handler);
    }

    /**
     * 2.4.1.获取工单
     */
    public static void jobOrderList(String userId, int page, int size, String state, String deviceTypeId, String supplierId, String startTime, String endTime, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", page);//当前页
        params.put("size", size);//页面大小
        params.put("state", state);
        params.put("userId", userId);
        params.put("deviceTypeId", deviceTypeId);//设备类型id
        params.put("supplierId", supplierId);//供应商id
        params.put("startTime", startTime);//开始时间
        params.put("endTime", endTime);//结束时间
        ApiHttpClient.post("/bankJobOrder/conditionJobOrderList", params, handler);
    }

    /**
     * 工单详情
     */
    public static void getJobOrderDetails(String jobOrderId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);//当前页
        ApiHttpClient.post("/bankJobOrder/detail", params, handler);
    }
    /**
     * 关闭工单
     */
    public static void closeOrder(String jobOrderId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);//当前页
        ApiHttpClient.post("/supJobOrder/closeOrder", params, handler);
    }

    /**
     * 2.2.3.工单详情跟踪
     */
    public static void getWorkOrderTracking(String jobOrderId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);//当前页
        ApiHttpClient.post("/bankJobOrder/track", params, handler);
    }

    /**
     * 工单转发
     */
    public static void forward(String jobOrderId, String userId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);//当前页
        params.put("userId", userId);//当前页
        ApiHttpClient.post("/supJobOrder/forward", params, handler);
    }

    /**
     * 2.4.5.转发客服列表
     */
    public static void ForwardList(String supplierId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("supplierId", supplierId);
        ApiHttpClient.post("/bankJobOrder/forwardList", params, handler);
    }

    /**
     * 2.4.7.工单评价
     */
    public static void setEvaluate(String JobOrderId, String userId, String userName, String content, String star, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("jobOrderId", JobOrderId));
        list.add(new BasicNameValuePair("userId", userId));
        list.add(new BasicNameValuePair("userName", userName));
        list.add(new BasicNameValuePair("content", content));
        list.add(new BasicNameValuePair("star", star));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/bankJobOrder/evaluate", entity, handler);
    }

    /**
     * 2.4.8.撤单
     */
    public static void cancelOrder(String JobOrder_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", JobOrder_id);
        ApiHttpClient.post("/bankJobOrder/cancelOrder", params, handler);
    }

    /**
     * 2.2.7.接单和确认
     */
    public static void comfirmOrder(String jobOrderId, String userId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);
        params.put("userId", userId);
        ApiHttpClient.post("/supJobOrder/comfirmOrder", params, handler);
    }

    /*添加工单 -- 上门维修*/
    public static void Addworder_door(String branch, String userId, String phone, String bankId, String supplierId, String image, String remark,
                                      String voice, String situation, String deviceModelName, String deviceTypeName, String deviceModelId,
                                      AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("branch", branch);
        params.put("userId", userId);
        params.put("phone", phone);
        params.put("bankId", bankId);
        params.put("supplierId", supplierId);
        params.put("image", image);
        params.put("remark", remark);
        params.put("voice", voice);
        params.put("situation", situation);
        params.put("deviceModelName", deviceModelName);
        params.put("deviceTypeName", deviceTypeName);
        params.put("deviceModelId", deviceModelId);
        ApiHttpClient.post("/bankJobOrder/doorApplication", params, handler);
    }

    /*添加工单 --  寄件返厂*/
    public static void Addworder_send(String userId, String deviceNum, String express, String courierNum, String comAddress, String phone, String bankId,
                                      String supplierId, String image, String remark, String voice, String deviceModelId, String deviceModelName, String deviceTypeName,
                                      String situation, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("deviceNum", deviceNum);
        params.put("express", express);
        params.put("courierNum", courierNum);
        params.put("comAddress", comAddress);
        params.put("phone", phone);
        params.put("bankId", bankId);
        params.put("supplierId", supplierId);
        params.put("image", image);
        params.put("remark", remark);
        params.put("voice", voice);
        params.put("deviceModelId", deviceModelId);
        params.put("deviceModelName", deviceModelName);
        params.put("deviceTypeName", deviceTypeName);
        params.put("situation", situation);

        ApiHttpClient.post("/bankJobOrder/sendApplication", params, handler);
    }


    /*获取供应商列表*/
    public static void getSuppliers(String type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("deviceTypeId", type);
        ApiHttpClient.post("/bankJobOrder/suppliersByType", params, handler);
    }
    /*获取供应商对应的列表*/
    public static void getDeviceTypes(AsyncHttpResponseHandler handler) {
        ApiHttpClient.post("/bankJobOrder/deviceTypes", handler);
    }
    /*获取供应商对应的列表*/
    public static void getdeviceTypes(String supplierId,AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("supplierId", supplierId);
        ApiHttpClient.post("/bankJobOrder/deviceTypesBySup",params, handler);
    }

    /*获取供应商设备的型号*/
    public static void getdeviceModels(String supplierId, String deviceTypeId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("supplierId", supplierId);
        params.put("deviceTypeId", deviceTypeId);
        ApiHttpClient.post("/bankJobOrder/deviceModels", params, handler);
    }

    /**
     * 2.2.2.工单搜索
     */
    public static void getSearchJobOrder(int page, int size, String userId, String keyWord, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("size", size);
        params.put("userId", userId);
        params.put("keyWord", keyWord);
        ApiHttpClient.post("/bankJobOrder/searchJobOrder", params, handler);
    }

    /**
     * 文件上传
     *
     * @param params
     * @param handler
     */
    public static void uploadSingleFile(RequestParams params, AsyncHttpResponseHandler handler) {
        ApiHttpClient.post("/file/uploadAppFile", params, handler);
    }

    /**
     * 2.4.12.录入快递单号和快递公司
     */
    public static void inputCourierNum(String JobOrderId, String express, String courierNum, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("jobOrderId", JobOrderId));
        list.add(new BasicNameValuePair("express", express));
        list.add(new BasicNameValuePair("courierNum", courierNum));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/bankJobOrder/inputCourierNum", entity, handler);
    }

    /*2.3.15我要评价*/
    public static void getEvaluate(String jobOrderId,AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);
        ApiHttpClient.post("/bankJobOrder/toEvaluate", params, handler);
    }

    /**
     * 2.7.1.提交意见反馈
     */
    public static void feedback(String userId, String advice, AsyncHttpResponseHandler handler) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userId", userId));
        list.add(new BasicNameValuePair("advice", advice));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("/feedback/save", entity, handler);
    }


    public static void download(String dir, AsyncHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("dir", dir);
        ApiHttpClient.download("", params, handler);
    }

    /*2.1.14.获取签到路线*/
    public static void getSignDetail(String jobOrderId,AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("jobOrderId", jobOrderId);
        ApiHttpClient.post("/supJobOrder/signDetail",params, handler);
    }

}
