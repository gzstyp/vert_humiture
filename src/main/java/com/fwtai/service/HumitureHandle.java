package com.fwtai.service;

import com.fwtai.tool.ToolClient;
import com.fwtai.tool.ToolJdbc;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;

public class HumitureHandle implements Handler<RoutingContext>{

  private final Vertx vertx;

  public HumitureHandle(final Vertx vertx){
    this.vertx = vertx;
  }

  @Override
  public void handle(final RoutingContext context){
    final HttpServerRequest request = context.request();
    final String route = request.getParam("route");
    final String json = ToolClient.jsonParams();
    if(route == null || route.trim().length() == 0){
      ToolClient.responseJson(context,json);
      return;
    }
    switch (route){
      case "map":
        map(context);
        break;
      case "list":
        list(context);
        break;
      case "redirect":
        redirect(context);
        break;
      default:
        ToolClient.responseJson(context,json);
        break;
    }
  }

  public void map(final RoutingContext context){
    final HttpServerRequest request = context.request();
    final String _kid = "kid";
    final String validateField = ToolClient.validateField(request,_kid);
    if(validateField != null){
      ToolClient.responseJson(context,validateField);
      return;
    }
    final String kid = request.getParam(_kid);
    final String sqlMap = "SELECT de.ID,de.Name from Device de WHERE de.ID = ?";
    final JsonArray params = new JsonArray().add(kid);
    new ToolJdbc(vertx).queryMap(sqlMap,context,params);
  }

  public void list(final RoutingContext context){
    final String sqlList = "SELECT cl.DeviceID,cl.Val value,cl.SeqType,de.Name name from Channel cl LEFT JOIN Device de on de.ID = cl.DeviceID";
    new ToolJdbc(vertx).queryList(sqlList,context,null);
  }

  public void redirect(final RoutingContext context){
    ToolClient.responseSucceed(context);
  }
}