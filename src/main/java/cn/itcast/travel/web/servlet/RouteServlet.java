package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.FavoriteService;
import cn.itcast.travel.service.RouteService;
import cn.itcast.travel.service.impl.FavoriteServiceImpl;
import cn.itcast.travel.service.impl.RouteServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/route/*")
public class RouteServlet extends BaseServlet {
    private RouteService routeService = new RouteServiceImpl();
    private FavoriteService favoriteService = new FavoriteServiceImpl();

    /**
     * 分页处理
     */
    public void pageQuery(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String currentPageStr = request.getParameter("currentPage");
        String pageSizeStr = request.getParameter("pageSize");
        String cidStr = request.getParameter("cid");
        String rname = request.getParameter("rname");
        rname = new String(rname.getBytes("iso-8859-1"), "utf-8");

        int cid = 0;
//        将字符串转为数字
        if (!"null".equalsIgnoreCase(cidStr) && cidStr != null && cidStr.length() > 0) {
            cid = Integer.parseInt(cidStr);
        }

        int currentPage = 1;
        if (currentPageStr != null && currentPageStr.length() > 0) {
            currentPage = Integer.parseInt(currentPageStr);
        }

        int pageSize = 5;
        if (pageSizeStr != null && pageSizeStr.length() > 0) {
            pageSize = Integer.parseInt(pageSizeStr);
        }

        //查询数据库
        PageBean<Route> pb = routeService.pageQuery(cid, currentPage, pageSize, rname);
        writeValue(response, pb);
    }

    /**
     * 详细信息
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String rid = request.getParameter("rid");
        Route route = routeService.findOne(rid);
        writeValue(response, route);
    }

    /**
     * 判断是否收藏
     */
    public void isFavorite(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String rid = request.getParameter("rid");
        User user = (User) request.getSession().getAttribute("user");
        int uid = 0;
        if (user != null) {
            uid = user.getUid();
        }
        boolean flag = favoriteService.isFavorite(rid, uid);
        writeValue(response, flag);
    }

    /**
     * 用户添加收藏
     */
    public void addFavorite(HttpServletRequest request, HttpServletResponse response){
        String rid = request.getParameter("rid");
        User user = (User) request.getSession().getAttribute("user");
        int uid;
        if (user == null)
            return;
        else
            uid = user.getUid();
        favoriteService.add(rid, uid);

    }
}
