# ConPlat
Conatiner Cloud Cluster Analysis Platform(based on Machine Learning)
<br/>容器云集群分析平台（基于机器学习）
<br/>资源统计与配置推荐，日志分析，异常检测
<br/>已完成部分：
<br/>资源统计：
<br/>访问并解析Prometheus监控数据，通过fabric8获取集群信息。
<br/>配置推荐：部署了XGBoost模型到项目中，根据API-Server的Num, CPU, Mem,和Node的Num四个输入，预测响应时间RT。
