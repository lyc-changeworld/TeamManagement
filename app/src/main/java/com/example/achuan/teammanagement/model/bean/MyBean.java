package com.example.achuan.teammanagement.model.bean;

/**
 * Created by achuan on 17-2-3.
 * 功能：模板数据模型类(单个实例对应于单个item中要显示适配的信息)
 */

public class MyBean {

    private String Title;
    private String Content;

    public MyBean(String title, String content) {
        Title = title;
        Content = content;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
