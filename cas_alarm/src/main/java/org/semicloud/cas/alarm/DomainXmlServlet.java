package org.semicloud.cas.alarm;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 用于读取跨域文件的Servlet，用于与人工编辑系统交互
 *
 * @author Semicloud
 */
public class DomainXmlServlet extends HttpServlet {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * 读取跨域文件，封装为ListOfString的形式
     *
     * @return the string list
     */
    public static List<String> getStringList() {
        List<String> strs = new ArrayList<String>();
        String fileName = "clientaccesspolicy.xml";
        try {
            // 以流文件的形式获取资源较好，如果不使用流文件的话，在部署时可能会出现错误
            InputStream in = ClassLoader.getSystemResourceAsStream(fileName);
            Iterator<String> itor = IOUtils.lineIterator(in, "UTF-8");
            while (itor.hasNext()) {
                strs.add(itor.next());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return strs;
    }

    /*
     * doGet()
     *
     * @see
     * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        for (String str : getStringList()) {
            out.write(str);
        }
        out.flush();
        out.close();
    }

    /*
     * doPost()
     *
     * @see
     * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
