# Development Process

## 0831

1. API里面是啥？咋调用？

   GET 

   浏览器打开

2. 本地存储：数据库性能有差异(Room?或其他数据库)

   - Room: have an abstraction layer over SQLite (browse offline and sync when online. **highly recommend** **using Room instead of SQLite**) 

     

     Observable so that it can notify the presentation layer for the changes i wanted to keep an eye on.

     

     A satisfying speed

     

     based on build-in SQLite database

     

   - Realm

     A relatively fast and convenient library, all links are simply implemented

     

     one of the best option for storing data on a mobile device at the moment, the minus can only be an increase in the size of the apk-file by 2.5 MB.

     

     Realm uses more RAM and increases the apk size, build time. So I prefer Room.

3. 新闻关键词搜索？

4. SDK 

5. 数据可视化？Java

6. 疫情图谱在干嘛

7. 聚类用什么库？

8. RY:熟悉前端逻辑(自己看，lc讲)



## 0901

1. 数据库设计与学习

   |      | _id  | title | content | source | time | type | viewed | category |
   | ---- | ---- | ----- | ------- | ------ | ---- | ---- | ------ | -------- |
   |      |      |       |         |        |      |      |        |          |
   |      |      |       |         |        |      |      |        |          |
   |      |      |       |         |        |      |      |        |          |

   Q: 

   - 是否加入type列？(news, paper, event)，还是各做三张表
   - category有很多？境内疫情/境外疫情/政府行动等等？
   - 分享时需要内容摘要？？

2. 界面修改(LC)

3. Push(RY)

4. GET 子线程?Main Activity?

