package com.jwell56.security.cloud.service.sso.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Accessors(chain = true)
public class TreeNode {

    private Integer id;//主键ID

    private Integer pid;//父节点ID

    private Integer level;//节点的层级

    private String moduleName;//节点内容

    private String basePath;//节点路径

    private String permission;//权限 0：无权限，1：有权限

    private List<TreeNode> children = new ArrayList<TreeNode>();//子孙节点

    public TreeNode(int id, int pid, int level, String moduleName, String basePath) {
        this.id = id;
        this.pid = pid;
        this.level = level;
        this.moduleName = moduleName;
        this.basePath = basePath;
    }

    public TreeNode(int id, int pid, String moduleName, String basePath) {
        this.id = id;
        this.pid = pid;
        this.moduleName = moduleName;
        this.basePath = basePath;
    }

}
