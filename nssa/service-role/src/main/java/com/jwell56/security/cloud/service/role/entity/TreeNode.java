package com.jwell56.security.cloud.service.role.entity;

import lombok.*;
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

    private String nav;

    private List<TreeNode> children = new ArrayList<TreeNode>();//子孙节点

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
        this.nav = moduleName;
    }

    public TreeNode(int id, int pid, int level, String moduleName, String basePath,String nav) {
        this.id = id;
        this.pid = pid;
        this.level = level;
        this.moduleName = moduleName;
        this.basePath = basePath;
        this.nav = nav;
    }
    
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
//        this.nav = this.moduleName;
    }
    
    public TreeNode(int id, int pid, String moduleName, String basePath,String nav) {
        this.id = id;
        this.pid = pid;
        this.moduleName = moduleName;
        this.basePath = basePath;
        this.nav = nav;
    }

}
