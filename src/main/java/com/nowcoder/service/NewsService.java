package com.nowcoder.service;

import com.nowcoder.dao.NewsDAO;
import com.nowcoder.model.News;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.velocity.texen.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by nowcoder on 2016/7/2.
 */
@Service
public class NewsService {
    @Autowired
    private NewsDAO newsDAO;

    @Autowired
    private SensitiveService sensitiveService;

    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }


    /**
     * 用来加载更多的页面
     * @param userId
     * @param curPage 当前页，从0开始，但最后显示到界面的是从1-n
     * @param limit
     * @param showPage 标签一次显示有几页
     * @return
     */
    public List<Integer> getPagesList(int userId, int curPage, int limit, int showPage){
        List<Integer> pageList = new ArrayList<>();
        int count = newsDAO.selectByUserIdCount(userId);
        int lastPage = (int)Math.ceil(count/(double)limit)-1;



        if(curPage <= showPage/2){
            for (int i = 0; i < showPage && i <= lastPage; i++) {
                pageList.add(i);
            }
        }else if(curPage >= lastPage - showPage/2){
            for (int i = lastPage; i > lastPage-showPage; i--) {
                pageList.add(i);
            }
            Collections.reverse(pageList);
        }else{
            for (int i = curPage-showPage/2; i <= curPage+showPage/2 && i <= lastPage; i++) {
                pageList.add(i);
            }
        }

        return pageList;
    }

    public Map<String, Object> addNews(News news) {
        Map<String, Object> map = new HashMap<>();
        //HTML过滤
        news.setTitle(HtmlUtils.htmlEscape(news.getTitle()));

        //敏感词过滤
        boolean hasSensitive =  sensitiveService.isSensitive(news.getTitle());
        if(hasSensitive){
            map.put("msgtitle","包含敏感词，请修改");
            return map;
        }

        //正则表达式检测网址是否合理
        String pattern = "^(http://|ftp://|https://)[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*";
        boolean isMatch = Pattern.matches(pattern, news.getLink());


        if(!isMatch){
            map.put("msglink","链接格式不正确，请以http/https/ftp开头");
            return map;
        }

        newsDAO.addNews(news);
        map.put("userid", news.getId());
        return map;
    }

    public News getById(int newsId) {
        return newsDAO.getById(newsId);
    }

    public String saveImage(MultipartFile file) throws IOException {
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        if (!ToutiaoUtil.isFileAllowed(fileExt)) {
            return null;
        }

        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
        Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR + fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        return ToutiaoUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;
    }

    public int updateCommentCount(int id, int count) {
        return newsDAO.updateCommentCount(id, count);
    }

    public int updateLikeCount(int id, int count) {
        return newsDAO.updateLikeCount(id, count);
    }
}
