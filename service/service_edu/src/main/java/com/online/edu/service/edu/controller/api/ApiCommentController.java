package com.online.edu.service.edu.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.edu.common.base.result.R;
import com.online.edu.common.base.result.ResultCodeEnum;
import com.online.edu.common.base.util.JwtInfo;
import com.online.edu.common.base.util.JwtUtils;
import com.online.edu.service.base.dto.MemberCommentDto;
import com.online.edu.service.base.exception.EduException;
import com.online.edu.service.edu.entity.Comment;
import com.online.edu.service.edu.entity.Teacher;
import com.online.edu.service.edu.feign.UcenterMemberService;
import com.online.edu.service.edu.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@CrossOrigin  //允许跨域访问
@Api( tags = "前端评论")
@RestController
@RequestMapping("/api/edu/course-comment")
public class ApiCommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UcenterMemberService ucenterMemberService;

    // 根据课程ID 查询课程的评论 进行分页查询
    @ApiOperation(value = "评论分页列表")
    @GetMapping("list/{courseId}/{page}/{limit}")
    public R listPage(@ApiParam(value = "当前页码",required = true)@PathVariable Long page,
                      @ApiParam(value = "每页记录数",required = true)@PathVariable Long limit,
                      @ApiParam(value = "课程ID",required = true)@PathVariable String courseId ){

       // System.out.println("courseId:" + courseId +"^^^************************");
        Page<Comment> pageParam = new Page<>(page, limit);
        IPage<Comment> commentPage = commentService.selectPage(pageParam, courseId);
        //获取分页查询到的数据
        List<Comment> commentList = commentPage.getRecords();
        //获取总记录数
        long total = commentPage.getTotal();
        return R.ok().data("items",commentList).data("total",total);
    }


    //添加评论 根据用户ID 需要调用接口 获取token  评论前必须登陆
    @ApiOperation(value = "添加评论")
    @PostMapping("auth/save")
    public R save(@RequestBody Comment comment, HttpServletRequest request) {

        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        if(StringUtils.isEmpty(jwtInfo.toString())) {
            throw new EduException(ResultCodeEnum.LOGIN_AUTH);
        }
        String memberId = jwtInfo.getId();
        comment.setMemberId(memberId);

        MemberCommentDto memberCommentDto = ucenterMemberService.getMemberCommentDtoByMemberId(memberId);

        comment.setNickname(memberCommentDto.getNickname());
        comment.setAvatar(memberCommentDto.getAvatar());

        commentService.save(comment);
        return R.ok();
    }

    // 根据评论ID删除评论，只能删除自己的评论 必须登陆  TODO 注意：这里我们应该对评论进行铭感词过滤
    @ApiOperation(value = "添加评论")
    @DeleteMapping("auth/remove/{id}")
    public R removeById(@PathVariable String id, HttpServletRequest request){

        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        if(StringUtils.isEmpty(jwtInfo.toString())) {
            throw new EduException(ResultCodeEnum.LOGIN_AUTH);
        }
        boolean result = commentService.removeById(id);
        if(result){
            return R.ok().message("删除成功");
        }else {
            return R.error().message("删除失败");
        }
    }



}
