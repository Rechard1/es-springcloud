package com.jwell56.security.cloud.service.netstruct.entity.commons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wsg
 * @since 2019-06-27
 * 与AUParam参数和调用方式相同，区别在NetStructParam不做权限处理，AUParam会做权限处理
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetStructParam implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	@ApiModelProperty(example = "1,2,3", value = "区域id列表，多个id以,号隔开，例如：4,12,23,34")
    private String areaIdList;

    @ApiModelProperty(example = "1,4,5", value = "单位id列表，多个id以,号隔开，例如：4,12,23,34")
    private String unitIdList;

    @ApiModelProperty(example = "47", value = "区域id", hidden = true)
    private Integer areaId;

    @ApiModelProperty(example = "47", value = "单位id", hidden = true)
    private Integer unitId;

    @ApiModelProperty(example = "1", value = "区域id列表，多个id以,号隔开，例如：4,12,23,34")
    private String dAreaIdList;

    @ApiModelProperty(example = "1", value = "单位id列表，多个id以,号隔开，例如：4,12,23,34")
    private String dUnitIdList;

    @ApiModelProperty(example = "47", value = "区域id", hidden = true)
    private Integer dAreaId;

    @ApiModelProperty(example = "47", value = "单位id", hidden = true)
    private Integer dUnitId;

    @ApiModelProperty(example = "1", value = "区域id列表，多个id以,号隔开，例如：4,12,23,34")
    private String sAreaIdList;

    @ApiModelProperty(example = "1", value = "单位id列表，多个id以,号隔开，例如：4,12,23,34")
    private String sUnitIdList;

    @ApiModelProperty(example = "47", value = "区域id", hidden = true)
    private Integer sAreaId;

    @ApiModelProperty(example = "47", value = "单位id", hidden = true)
    private Integer sUnitId;

    public List<Integer> areaIdList() {
        return getIdList(areaId, areaIdList);
    }

    public List<Integer> unitIdList() {
        return getIdList(unitId, unitIdList);
    }

    public List<Integer> dAreaIdList() {
        return getIdList(dAreaId, dAreaIdList);
    }

    public List<Integer> dUnitIdList() {
        return getIdList(dUnitId, dUnitIdList);
    }

    public List<Integer> sAreaIdList() {
        return getIdList(sAreaId, sAreaIdList);
    }

    public List<Integer> sUnitIdList() {
        return getIdList(sUnitId, sUnitIdList);
    }

    private List<Integer> getIdList(Integer id, String idListStr) {
        List<Integer> idList = new ArrayList<>();

        if (id != null && id != 0) {
            idList.add(id);
        }

        if (idListStr != null && !idListStr.isEmpty() && !idListStr.equals("undefined")) {
            idList = strListToIntList(idListStr);
        }

        return idList;
    }

    private List<Integer> strListToIntList(String strIntList) {
        List<Integer> intList = new ArrayList<>();
        try {
            if (strIntList != null && !strIntList.isEmpty()) {
                String[] strList = strIntList.split(",");
                for (String strArea : strList) {
                    intList.add(Integer.parseInt(strArea));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intList;
    }
}