package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.CategoryDao;
import cn.itcast.travel.dao.impl.CategoryDaoImpl;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.service.CategoryService;
import cn.itcast.travel.util.JedisUtil;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryServiceImpl implements CategoryService {
    private CategoryDao categoryDao = new CategoryDaoImpl();

    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    /*
    @Override
    public List<Category> findAll() {
//        将数据库中的数据存入缓存，提升效率
        Jedis jedis = JedisUtil.getJedis();
        List<Category> cs_tab = null;
//        使缓存中的数据和数据库排序一致， 0~-1表示查询所有
        Set<String> cs_j = jedis.zrange("category", 0, -1);

        if (cs_j == null || cs_j.size() == 0) {
            //从数据库中找数据
            cs_tab = categoryDao.findAll();
//            存入缓存
            for (int i = 0; i< cs_tab.size(); i++){
                jedis.zadd("category", cs_tab.get(i).getCid(), cs_tab.get(i).getCname());
            }
        }else {
            //从缓存中找数据
            cs_tab = new ArrayList<Category>();
            for (String name : cs_j) {
                Category category = new Category();
                category.setCname(name);
                cs_tab.add(category);
            }

        }

        return cs_tab;
    }

    */
}
