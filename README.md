MyFastCode
==========

快速构建面向soa的Web应用，基于<br>
Mysql<br>
Hessian<br>
Spring&amp;MVC<br>
Freemarker<br>
redis<br><br>

web容器采用jetty8,集群http session采用redis存储（jetty自身提供了mongodb/rdb的支持）<br>
文件服务采用servlet3.1原生支持，运行于tomcat8中<br><br>

=========================<br>
MyFastCode 特性：<br>
1：解决hessian繁琐的配置，采用annotation扫描自动export和客户端bind<br>
2：对数据库单表操作不需要写代码，采用了动态构建mybatis statement<br>
3：hessian 序列化/反序列化对BigDecimal支持<br>
4：使用CodeGenerater小工程，全自动生成Dto,mybatis mapper, dao , service 接口，service实现及controller和简单的各表对应的form.ftl<br><br><br>



持续更新中，敬请关注.