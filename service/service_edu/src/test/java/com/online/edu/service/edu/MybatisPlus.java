package com.online.edu.service.edu;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.edu.service.edu.entity.Teacher;
import com.online.edu.service.edu.mapper.TeacherMapper;
import com.online.edu.service.edu.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MybatisPlus {

    @Autowired
    private TeacherService teacherService;
    @Autowired
    private TeacherMapper teacherMapper;

    @Test
    public void page(){
        Page<Teacher> teacherPage = new Page<>(1, 2);
        Page<Teacher> pages = teacherMapper.selectPage(teacherPage,null);

        System.out.println(teacherPage);
        System.out.println("*******************************");
        //第一页的搜索记录即五条数据  这里也会自动查询数据的数量
        List<Teacher> records = pages.getRecords();
        records.forEach(System.out::println);

        long total = pages.getTotal();
        //Total : 总数据量      一共有多少条记录
        System.out.println(total);

    }

    @Test
    public void list(){
        List<Teacher> list = teacherService.list();
        list.forEach(System.out::println);
    }

    //对象引用还是拷贝
    @Test
    public void run(){

        Person person=new Person("lili","nv",18);
        Person person1 = person;
        //结果：lili  lili
        System.out.println(person.getName());
        System.out.println(person1.getName());

        person1.setName("hh");
        //结果：hh
        System.out.println(person.getName());
        System.out.println(person1.getName());
        //是对象引用
    }

}
