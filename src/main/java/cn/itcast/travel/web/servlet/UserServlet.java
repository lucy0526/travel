package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    UserService service = new UserServiceImpl();

    public void regist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*
        验证码
         */
        String check = request.getParameter("check");
        HttpSession session = request.getSession();
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");//保证验证码只能够使用一次
        if (checkcode_server == null || !checkcode_server.equalsIgnoreCase(check))
        {
            ResultInfo info = new ResultInfo();
            info.setFlag(false);
            info.setErrorMsg("验证码错误");
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(info);
            response.getWriter().write(json);
            return;
        }



        //获取网页数据
        Map<String, String[]> map = request.getParameterMap();
        User user = new User();
        try {
            BeanUtils.populate(user, map);//封装对象，将map的数据封装入user对象中
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
//        处理数据
        boolean flag = service.regist(user);
        ResultInfo info = new ResultInfo();//响应数据对象
        if (flag){
            //注册成功
            info.setFlag(true);
        }else{
//            注册失败
            info.setFlag(false);
            info.setErrorMsg("注册失败！");
        }

        //将info对象序列化为json
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);

//        将json数据写回客户端（字符串数据）
//        response.setContentType("application/json;charset=utf-8");//不能用，都在传给网页的数据又为object
        response.getWriter().write(json);
    }

    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String[]> map = request.getParameterMap();
        User user = new User();
        try {
            BeanUtils.populate(user, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        User u = service.login(user);
        ResultInfo info = new ResultInfo();

        if (u == null) {
            info.setFlag(false);
            info.setErrorMsg("用户名或密码错误！");
        } else if ("N".equalsIgnoreCase(u.getStatus())) {
            info.setFlag(false);
            info.setErrorMsg("您尚未激活账号，请先激活！");
        } else if ("Y".equalsIgnoreCase(u.getStatus())) {
            request.getSession().setAttribute("user", u);//设置用户信息
            info.setFlag(true);
        }

        //响应数据
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(), info);

    }

    public void active(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        if (code != null) {
            boolean flag = service.active(code);
            String msg = null;
            if (flag) {
//                msg = "激活成功，请<a href='login.html'>登陆</a>";
                msg = "激活成功，请登陆";
            } else {
                msg = "激活失败，请联系管理员";
            }
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
        }
    }

    /**
     * 用于自动填写用户名
     * @param request
     * @param response
     * @throws IOException
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object user = request.getSession().getAttribute("user");
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(), user);
    }

    public void exit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().invalidate();
        response.sendRedirect(request.getContextPath() + "/login.html");
    }

}