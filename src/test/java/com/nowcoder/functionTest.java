package com.nowcoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class functionTest {
    public static void main(String[] args) {
        String pattern = "^(http://|ftp://|https://)[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*";
        String test = "https://www.baidu.com/s?wd=java%20%E7%BD%91%E5%9D%80%20%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F%E5%8C%B9%E9%85%8D&rsv_spt=1&rsv_iqid=0xd07888ec00130fc2&issp=1&f=8&rsv_bp=1&rsv_idx=2&ie=utf-8&rqlang=cn&tn=98627918_hao_pg&rsv_enter=1&rsv_dl=tb&oq=%25E7%25BD%2591%25E5%259D%2580%2520%25E6%25AD%25A3%25E5%2588%2599%25E8%25A1%25A8%25E8%25BE%25BE%25E5%25BC%258F%25E5%258C%25B9%25E9%2585%258D&rsv_t=614b%2FL5AK439WbFun%2FRNSi06JOsiNhL5ld97Jk5THiRcvTvk%2FctCiHUgz4dEO7f29Kmrj%2BpD&rsv_btype=t&inputT=1149&rsv_pq=f8a9582d0001cb2f&rsv_sug3=199&rsv_sug2=0&rsv_sug4=2112";
        boolean isMatch = Pattern.matches(pattern, test);
        System.out.println(isMatch);
    }

}
