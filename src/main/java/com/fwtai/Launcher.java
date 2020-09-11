package com.fwtai;

import com.fwtai.config.ConfigFiles;
import com.fwtai.service.HumitureHandle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.HashSet;
import java.util.Set;

public class Launcher extends AbstractVerticle {

  //第一步,声明router,如果有重复的 path 路由的话,它匹配顺序是从上往下的,仅会执行第一个.那如何更改顺序呢？可以通过 order(x)来更改顺序,值越小越先执行!
  private Router router;

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {

    //创建HttpServer
    final HttpServer httpServer = vertx.createHttpServer();

    //第二步,初始化|实例化 Router
    router = Router.router(vertx);

    //若想要或body的参数[含表单的form-data和json格式]需要添加,可选
    router.route().handler(BodyHandler.create());//支持文件上传的目录,ctrl + p 查看

    final Set<HttpMethod> methods = new HashSet<>();
    methods.add(HttpMethod.OPTIONS);
    methods.add(HttpMethod.GET);
    methods.add(HttpMethod.POST);

    router.route().handler(CorsHandler.create(ConfigFiles.allowedOriginPattern).allowedMethods(methods));//支持正则表达式

    //第三步,将router和 HttpServer 绑定
    httpServer.requestHandler(router).listen(ConfigFiles.port,http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("---应用启动成功---"+ConfigFiles.port);
      } else {
        startPromise.fail(http.cause());
        System.out.println("---应用启动失败---");
      }
    });

    //第四步,配置Router解析url
    // http://127.0.0.1:10004/api/humiture?route=list
    router.route("/api/humiture").handler(new HumitureHandle(vertx));
  }
}