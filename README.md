MyFastCode
==========

快速构建面向soa的大型集群应用，基于<br>
mysql-5.6<br>
hessian-4.0.8<br>
Spring&amp;MVC-3.1.0.RELEASE<br>
freemarker-2.3.20<br>
redis-2.8.19<br><br>

web容器采用jetty8
http session采用redis存储（jetty自身提供了mongodb/rdb的支持）<br>
基于redis pubsub特性，实现服务节点上/下线及配置管理<br>
文件服务采用servlet3.1原生支持，对接了Spring Security<br><br>

=========================<br>
MyFastCode 特性：<br>
1：解决hessian繁琐的配置，采用annotation扫描自动export和客户端bind<br>
2：对数据库单表操作不需要写代码，采用了动态构建mybatis statement<br>
3：hessian 序列化/反序列化对BigDecimal支持<br>
4：使用CodeGenerater小工程，全自动生成Dto,mybatis mapper, dao , service 接口，service实现及controller和简单的各表对应的form.ftl<br>
5：基于redis的pub/sub实现远程服务协调（类似阿里dubbo，但比dubbo更纯粹），集群场景下采用rr算法，支持节点下线检测和节点主节下线，单一服务降级<br><br>

=========================<br>
各工程描述：
MyClipAdmin：一个web应用，配合MyHessian组件实现服务节点管理，配置管理<br>
MyHessian：Hessian服务端暴露处理，客户端服务代理处理及节点管理事件，配置管理事件处理<br>
MyHttpSession：由于jetty8版本没有redis http session的管理实现，所以这个工程做为jetty的插件形式存在，需要部署到${JETTY_HOME}/lib/ext目录下<br>
MyFileServer：基于Servlet3.0标准实现业务过程中，用户上传的文件并回显上传结果<br>
MyAnnotation：数据基础操作(增删改查)注解支持，并发场景下，不建议使用“改”操作，因为它不安全（采用了load->change->save）<br>


持续更新中，敬请关注.