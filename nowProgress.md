## 0909进度

1. 变灰有bug?

2. 从cluster回来有bug，无法上拉更多?

5. Fragment Already Added Exception  多Fragment回收?

   java.lang.IllegalStateException: Fragment already added: TagFragment{refreshLayout=null, currentTag='news', recyclerView=androidx.recyclerview.widget.RecyclerView{a0a30e0 VFE...... ......ID 0,0-1080,1835 #7f08017f app:id/recycler_view}, newsListAdapter=com.java.renyi.NewsListAdapter@e7b3f99, newsEntityList=null, mEntryViewModel=com.java.renyi.db.EntryViewModel@130

4. java.lang.IllegalStateException: Bindings already cleared.

5. 后端去掉toast？

6. 加入Welcome Activity？(尝试)

7. 转圈时间太长?

8. 退出再进入，标签页重置了

9. 去掉微博分享标志

10. 国内的省份是否转为汉字

11. **没网处理，否则闪退**

12. **离线搜索和离线加载更多？**

13. bar返回，侧栏不对

14. 关于里面添加开发者信息

    



2. 疫情图谱搜索时不要弹toast，实体搜索没有结果时弹窗/提示？
3. API版本？
4. 一个PAGE多大，一次ADD多少
5. 夜间模式与开关匹配(系统的夜间模式与软件的夜间模式/开始默认日间模式)——Toast弹窗，疫情数据
5. 去掉一些Toast



视频流程：

自我介绍

完成了基础+扩展功能

1. 系统支持(流畅不卡不崩溃)
2. 布局合理美观点击正确
3. 删除和添加演示(修改有动态)
4. 正确美观显示新闻列表内容，点击进入详情，变灰
5. 本地存储离线查看(退出断网，再来，变灰的保持)
6. (回到网络)上拉更多，下拉最新
7. 显示来源和时间(列表和详情页，news和paper)
8. 新闻关键词搜索(香港、COVID)
9. 分享到微信(给刘畅分享并打开，有摘要)
10. 各省各国的疫情统计
11. 疫情图谱查询(疫苗)
12. 聚类在列表界面查看(关键词、聚类新闻)
13. 学者们，点击查看



我们使用的聚类方法是隐含迪利克雷模型，Latent Dirichlet Allocation简称LDA，这是一种三层[贝叶斯](https://baike.baidu.com/item/贝叶斯)概率模型。其简要原理是，指定聚类的类别数，把训练集也就是所有event作为输入，通过吉布斯采样或EM算法等方法，得到LDA概率模型中的参数，同时得到每个类的关键词，根据关键词判断每个类的具体含义。在预测时，针对输入的event，可以获得它属于各个类的概率，选择概率最大的类作为聚类结果即可。此外，我们的聚类是在APP中实时进行的，具有速度快、效果好的优点。

