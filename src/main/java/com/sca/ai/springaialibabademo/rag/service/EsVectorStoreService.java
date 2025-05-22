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
        elasticsearchVectorStore.add(documents);
    }
}
