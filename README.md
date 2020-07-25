### 一、建立目的
学习牛客网的中级项目 —— 仿今日头条资讯网站，并利用这个逐步搭建自己的一个网站，用来收集一些有趣、好玩的信息供大家和自己搬砖间隙放松。

### 二、知识点整理
见学习中记录的一些的知识点

### 三、目前完善或修改
#### 1.前端界面修改
* 增加登出按钮
* 登录后显示个人图像
* 将一些显示信息更改
* 详情页“登录后评论” 原本跳到主界面登录，现在修改为当前界面登录
* 详情页：点链接跳到具体新闻上

#### 2. 敏感词过滤
* HTML过滤:
    * 注册用户不能HTML
    * 发布的标题和评论内容
    * 如果发布资讯内容类似为：“\<script>alert("hi")\</script>”，则可能导致一直弹窗
    * 如果脚本为发送cookie，则会导致用户身份泄露
    * HTML过滤避免跨站脚本攻击
 * 敏感词过滤
    * 利用前缀树建立过滤算法
    * 对用户名、发布标题进行检测，不允许出现
    * 对评论内容进行过滤替换
    
 #### 3. 资讯链接检测
 * 需以HTTP/HTTPS开头
 * 反馈到前端，以alert提示
 
 #### 4. 分页显示
 * 分页前端代码参考：[百度经验](https://jingyan.baidu.com/article/19192ad804c81fe53e57072e.html)
 * 主页分页前后端完成：可以在HomeController中设置一次显示多少个资讯（limit），也可以设置显示几个分页（pageShow）
 * 站内信的列表和详情均类似分页实现
    
 #### 5. redis异步事件处理增加线程池
 * 利用redis的列表作为事件存储，通过brpop阻塞线程运行
 * 每有一个事件产生时，利用CashThreadPool新开一个线程（CashThreadPool适合并发短小任务）
 * 通过JConsole检测结果如下：
 ![检测结果](http://qiniu.cameree.com/blog/1593223027164.png)
 
 #### 6. 点赞点踩后异步事件处理
 * 基于上述异步模型，点赞时将产生两个异步事件：
    * 同步MySQL中某条资讯点赞数
    * 点赞时系统发送站内信到对应用户，提示谁点赞了哪一条资讯
* MySQL同步后的点赞数用于主页界面或者某条资讯详情页展示时的界面点赞数展示
 
 #### 7. 站内信消息
 * 增加发送消息按钮和对话框，实现前后端事件绑定
 * 阅读详情后通过上述异步模型进行异步处理，将mysql中已读数字更新为0
 * 进入站内信时，MySQL语句应该获取最新的内容到前端显示，但排序后并没有效果，语句如下：
 ```sql
 select from_id, to_id, content, has_read, conversation_id, created_date, count(id) as id 
 from ( select * from message where from_id= 3 or to_id=3 order by id desc) tt 
 group by conversation_id  
 order by created_date desc 
 limit 0,10
 ```
 **原因**： sql查询优化时在外层有group by时将里层的order by给忽视了，导致取到的都是插入到数据库的第一条信息。
 
 **更改**： 里层加一个limit,参考：[手把手教你如何玩转Mysql分组取每组最新的一条数据](https://blog.csdn.net/Cs_hnu_scw/article/details/105397337)
 ```sql
 select from_id, to_id, content, has_read, conversation_id, created_date, count(id) as id 
 from ( select * from message where from_id= 3 or to_id=3 order by id desc LIMIT 1000000) tt 
 group by conversation_id  
 order by created_date desc 
 limit 0,10
 ```
 **更新**： limit起作用是由于里层的子查询需要先排序再获取限制行数显示，这个时候优化器无法对其优化，因此能正常显示。但这样会有明显的问题，我这里虽然limit虽然足够大，目前业务量下，因此不会出现页面获取失败，但是存在风险的。可以采取的策略有：
  1. 先统计站内信总数，然后在limit限制，需要两条查询语句;
  2. 建立一个只包含最新消息的临时表，然后内部联结。

#### 8.七牛云SDK修改
* 申请了自己的测试账号
* 之前代码版本较低，现更改为7.2.29，并完成代码更新
* 主界面和详情页的图的加载实现实时缩图，?imageView2/0/w/100/h/80/

