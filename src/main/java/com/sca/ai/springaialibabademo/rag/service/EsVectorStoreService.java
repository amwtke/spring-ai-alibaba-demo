package com.sca.ai.springaialibabademo.rag.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EsVectorStoreService {
    @Autowired
    ElasticsearchVectorStore elasticsearchVectorStore;

    public void AddDocs() {
        // 生成室内设计案例文档
        List<Document> documents = new ArrayList<>();

// 现代简约风格客厅案例
        documents.add(new Document(
                """
                        案例编号：LR-2023-001
                        项目概述：180平米大平层现代简约风格客厅改造
                        设计要点：
                        1. 采用5.2米挑高的落地窗，最大化自然采光
                        2. 主色调：云雾白(哑光，NCS S0500-N)配合莫兰迪灰
                        3. 家具选择：意大利B&B品牌真皮沙发，北欧白橡木茶几
                        4. 照明设计：嵌入式筒灯搭配意大利Flos吊灯
                        5. 软装配饰：进口黑胡桃木电视墙，几何图案地毯
                        空间效果：通透大气，适合商务接待和家庭日常起居""",
                Map.of(
                        "type", "interior",    // 文档类型
                        "year", "2023",        // 年份
                        "month", "06",         // 月份
                        "location", "indoor",   // 位置类型
                        "style", "modern",      // 装修风格
                        "room", "living_room"   // 房间类型
                )));
        documents.add(new Document(
                """
                        案例编号：LR-2024-001
                        项目概述：100平米二手房改造
                        设计要点：
                        1. 厨房落地窗，最大化自然采光
                        2. 主色调：胡桃色，原木色
                        3. 家具选择：厨房柜子，白色，灰色；进口水槽，抽油烟机；
                        4. 照明设计：厨房吊灯
                        5. 软装配饰：防滑瓷砖
                        空间效果：通透大气，适合商务接待和家庭日常起居""",
                Map.of(
                        "type", "public",    // 文档类型
                        "year", "2024",        // 年份
                        "month", "06",         // 月份
                        "location", "indoor",   // 位置类型
                        "style", "classic",      // 装修风格
                        "room", "kitchen"   // 房间类型
                )));
        documents.add(new Document(
                """
                        案例编号：LR-2025-001
                        项目概述：阳光电梯房新房装修
                        设计要点：
                        1. 毛坯风格，废土风格
                        2. 主色调：大理石，水泥原色，红砖铺垫
                        3. 家具选择：地毯，小米电视，扫地机器人，高档进口沙发
                        4. 照明设计：LED大灯
                        5. 软装配饰：诺贝尔瓷砖，老板抽油烟机
                        空间效果：通透大气，适合商务接待和家庭日常起居""",
                Map.of(
                        "type", "public",    // 文档类型
                        "year", "2025",        // 年份
                        "month", "06",         // 月份
                        "location", "indoor",   // 位置类型
                        "style", "新式",      // 装修风格
                        "room", "客厅"   // 房间类型
                )));

        documents.add(new Document(
                """
                        案例编号：LR-2025-002
                        项目概述：老式学区房装修
                        设计要点：
                        1. 北欧轻奢风格，实用为主
                        2. 主色调：中式，红木色调，配合暖色调墙纸
                        3. 家具选择：LED大电视，德国壁挂式暖气
                        4. 照明设计：LED大灯
                        5. 软装配饰：诺贝尔瓷砖，老板抽油烟机
                        空间效果：实用性强，南北通透""",
                Map.of(
                        "type", "public",    // 文档类型
                        "year", "2025",        // 年份
                        "month", "11",         // 月份
                        "location", "indoor",   // 位置类型
                        "style", "二手房",      // 装修风格
                        "room", "客厅"   // 房间类型
                )));
        documents.add(new Document(
                """
                        案例编号：LR-2025-003
                        项目概述：老式学区房装修
                        设计要点：
                        1. 北欧轻奢风格，实用为主
                        2. 主色调：中式，红木色调，配合暖色调墙纸
                        3. 家具选择：LED大电视，德国壁挂式暖气
                        4. 照明设计：LED大灯
                        5. 软装配饰：诺贝尔瓷砖，老板抽油烟机
                        空间效果：实用性强，南北通透""",
                Map.of(
                        "type", "public",    // 文档类型
                        "year", "2025",        // 年份
                        "month", "12",         // 月份
                        "location", "indoor",   // 位置类型
                        "style", "二手房",      // 装修风格
                        "room", "客厅"   // 房间类型
                )));
        elasticsearchVectorStore.add(documents);
    }
}
