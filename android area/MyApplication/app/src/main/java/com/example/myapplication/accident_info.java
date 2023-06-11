package com.example.myapplication;

import com.naver.maps.geometry.LatLng;

public class accident_info {
    private String lld;
    private String pro;
    private String phone;
    private String img_str;
    private LatLng l_lld;

    public String getLld() {
        return lld;
    }

    public void setLld(String lld) {
        this.lld = lld;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImg_str() {
        return img_str;
    }

    public void setImg_str(String img_str) {
        this.img_str = img_str;
    }

    public LatLng getL_lld() {
        return l_lld;
    }

    public void setL_lld(LatLng l_lld) {
        this.l_lld = l_lld;
    }

    public accident_info(String lld, String pro, String phone, String img_str) {
        this.lld = lld;
        this.pro = pro;
        this.phone = phone;
        this.img_str = img_str;
        String[] temp=lld.split(",");
        LatLng temp_lld=new LatLng(Double.parseDouble(temp[0]),Double.parseDouble(temp[1]));
        l_lld=temp_lld;
    }
    public accident_info() {

    }
    public accident_info(String lld, String pro, String phone) {
        this.lld = lld;
        this.pro = pro;
        this.phone = phone;
        this.img_str = "aaaa";
    }
}
