package cn.iocoder.yudao.module.member.service.group;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.group.vo.MemberGroupCreateReqVO;
import cn.iocoder.yudao.module.member.controller.admin.group.vo.MemberGroupPageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.group.vo.MemberGroupUpdateReqVO;
import cn.iocoder.yudao.module.member.convert.group.MemberGroupConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.group.MemberGroupDO;
import cn.iocoder.yudao.module.member.dal.mysql.group.MemberGroupMapper;
import cn.iocoder.yudao.module.member.service.user.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.GROUP_HAS_USER;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.GROUP_NOT_EXISTS;

/**
 * 用户分组 Service 实现类
 *
 * @author owen
 */
@Service
@Validated
public class MemberGroupServiceImpl implements MemberGroupService {

    @Resource
    private MemberGroupMapper groupMapper;
    @Resource
    private MemberUserService memberUserService;

    @Override
    public Long createGroup(MemberGroupCreateReqVO createReqVO) {
        // 插入
        MemberGroupDO group = MemberGroupConvert.INSTANCE.convert(createReqVO);
        groupMapper.insert(group);
        // 返回
        return group.getId();
    }

    @Override
    public void updateGroup(MemberGroupUpdateReqVO updateReqVO) {
        // 校验存在
        validateGroupExists(updateReqVO.getId());
        // 更新
        MemberGroupDO updateObj = MemberGroupConvert.INSTANCE.convert(updateReqVO);
        groupMapper.updateById(updateObj);
    }

    @Override
    public void deleteGroup(Long id) {
        // 校验存在
        validateGroupExists(id);
        // 校验分组下是否有用户
        validateGroupHasUser(id);
        // 删除
        groupMapper.deleteById(id);
    }

    void validateGroupExists(Long id) {
        if (groupMapper.selectById(id) == null) {
            throw exception(GROUP_NOT_EXISTS);
        }
    }

    void validateGroupHasUser(Long id) {
        Long count = memberUserService.getUserCountByGroupId(id);
        if (count > 0) {
            throw exception(GROUP_HAS_USER);
        }
    }

    @Override
    public MemberGroupDO getGroup(Long id) {
        return groupMapper.selectById(id);
    }

    @Override
    public List<MemberGroupDO> getGroupList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return ListUtil.empty();
        }
        return groupMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<MemberGroupDO> getGroupPage(MemberGroupPageReqVO pageReqVO) {
        return groupMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MemberGroupDO> getGroupListByStatus(Integer status) {
        return groupMapper.selectListByStatus(status);
    }

}
